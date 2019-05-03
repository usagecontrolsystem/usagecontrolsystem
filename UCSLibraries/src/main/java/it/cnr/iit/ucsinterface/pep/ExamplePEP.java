/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucsinterface.pep;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucsinterface.message.MEAN;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.requestmanager.UCSCHInterface;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * USED for local experiments
 *
 * @author antonio
 *
 */
public class ExamplePEP implements PEPInterface {

    protected static final Logger log = Logger.getLogger( ExamplePEP.class.getName() );

    private static final String MSG_ERR_SEND_CH = "Error sending message to CH : {0}";

    private static final String START_PATH = "data";

    private PepProperties configuration;
    private UCSCHInterface requestManager;

    // map of unanswered messages, the key is the id of the message
    private HashMap<String, Message> unanswered = new HashMap<>();
    private ConcurrentHashMap<String, Message> responses = new ConcurrentHashMap<>();

    private volatile boolean initialized = false;

    private Object mutex = new Object();

    public ExamplePEP( PepProperties configuration ) {
        Reject.ifNull( configuration );
        this.configuration = configuration;

        initialized = true;
    }

    public String tryAccess() {
        String request = Utility.readFileAbsPath( START_PATH + "/RequestAntonio.xml" );
        String policy = Utility.readFileAbsPath( START_PATH + "/PolicyGiacomo.xml" );
        String pepUri = configuration.getBaseUri(); // TODO or host?

        TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder(
            PART.PEP.toString(), PART.CH.toString() );
        tryAccessBuilder.setPepUri( pepUri ).setPolicy( policy ).setRequest( request );
        TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
        tryAccessMessage.setCallback( null, MEAN.API );
        unanswered.put( tryAccessMessage.getID(), tryAccessMessage );
        Message message = requestManager.sendMessageToCH( tryAccessMessage );
        return tryAccessMessage.getID();

        // return (TryAccessResponse) contextHandler.tryAccess(tryAccessMessage);
    }

    public String startAccess( String sessionId ) {
        StartAccessMessage startAccessMessage = new StartAccessMessage(
            PART.PEP.toString(), PART.CH.toString() );
        startAccessMessage.setSessionId( sessionId );
        startAccessMessage.setCallback( null, MEAN.API );
        unanswered.put( startAccessMessage.getID(), startAccessMessage );
        try {
            Message message = requestManager.sendMessageToCH( startAccessMessage );
        } catch( Exception e ) {
            log.severe( String.format( MSG_ERR_SEND_CH, e.getMessage() ) );
            return null;
        }
        return startAccessMessage.getID();
    }

    private String endAccess( String sessionId ) {
        EndAccessMessage endAccessMessage = new EndAccessMessage(
            PART.PEP.toString(), PART.CH.toString() );
        endAccessMessage.setSessionId( sessionId );
        endAccessMessage.setCallback( null, MEAN.API );
        unanswered.put( endAccessMessage.getID(), endAccessMessage );
        try {
            Message message = requestManager.sendMessageToCH( endAccessMessage );
        } catch( Exception e ) {
            log.severe( String.format( MSG_ERR_SEND_CH, e.getMessage() ) );
            return null;
        }
        return endAccessMessage.getID();
    }

    /*
     * public void setCHInterface(ContextHandlerInterface contextHandlerInterface)
     * { this.contextHandler = contextHandlerInterface; }
     */

    public void setRequestManagerInterface(
            UCSCHInterface requestManager ) {
        this.requestManager = requestManager;
    }

    @Override
    public Message onGoingEvaluation( Message message ) {
        // BEGIN parameter checking
        if( message == null || !( message instanceof ReevaluationResponse ) ) {
            log.severe( "Message not valid" );
            return null;
        }
        if( !initialized ) {
            log.severe( "Cannot answer the message" );
            return null;
        }
        if( !message.getSource().equals( PART.CH.toString() )
                || !message.getDestination().equals( PART.PEP.toString() ) ) {
            log.severe( "Message in invalid format" );
            return null;
        }
        // END parameter checking

        ReevaluationResponse chPepMessage = (ReevaluationResponse) message;
        if( configuration.getRevokeType().equals( "HARD" ) ) {
            EndAccessMessage endAccess = new EndAccessMessage( PART.PEP.toString(),
                PART.CH.toString() );
            endAccess.setCallback( null, MEAN.API );
            endAccess.setSessionId( chPepMessage.getPDPEvaluation().getSessionId() );
            requestManager.sendMessageToCH( endAccess );
        } else {
            if( chPepMessage.getPDPEvaluation().getResult().contains( "Permit" ) ) {
                log.info(
                    "[TIME] RESUME EXECUTION " + System.currentTimeMillis() );
            }
            if( chPepMessage.getPDPEvaluation().getResult().contains( "Deny" ) ) {
                log.info(
                    "[TIME] STOP EXECUTION " + System.currentTimeMillis() );
            }
        }
        // contextHandler.endAccess(endAccess);
        message.setMotivation( "OK" );

        return message;
    }

    @Override
    public String receiveResponse( Message message ) {
        responses.put( message.getID(), message );
        return "";
    }

    public void start() throws InterruptedException, ExecutionException {
        String id = tryAccess();
        log.info( id );
        TryAccessResponse tryAccessResponse = (TryAccessResponse) waitForResponse(
            id );
        log.info( "Response: " + tryAccessResponse.getPDPEvaluation().getResult() );
        if( tryAccessResponse.getPDPEvaluation().getResult().contains( "Permit" ) ) {
            log.info( "Starting startaccess" );
            id = startAccess( tryAccessResponse.getSessionId() );
            StartAccessResponse startAccessResponse = (StartAccessResponse) waitForResponse(
                id );
            if( startAccessResponse.getPDPEvaluation().getResult()
                .contains( "Permit" ) ) {
                log.info( "Permit success" );
                Thread thread = new Thread(
                    new EndThread( tryAccessResponse.getSessionId() ) );
                thread.start();
            }

        }

    }

    private class EndThread implements Runnable {
        private String sessionId;

        public EndThread( String s ) {
            sessionId = s;
        }

        @Override
        public void run() {
            int random = ThreadLocalRandom.current().nextInt( 20, 30 );
            try {
                Thread.sleep( random * 1000L );
                String id = endAccess( sessionId );
                EndAccessResponse endAccessResponse;
                endAccessResponse = (EndAccessResponse) waitForResponse( id );
                log.info( endAccessResponse.getPDPEvaluation().getResult() );
            } catch( InterruptedException | ExecutionException e ) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Waits for a certain message containing a certain id to come
     *
     * @param id
     *          the id of the message we're waiting for
     * @return the message arrived
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private Message waitForResponse( String id )
            throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TaskBody taskBody = new TaskBody( id );
        return executor.submit( taskBody ).get();
    }

    /**
     * Class that waits for a message to become available
     *
     * @author antonio
     *
     */
    private class TaskBody implements Callable<Message> {

        private String id;

        public TaskBody( String id ) {
            this.id = id;
        }

        @Override
        public Message call() {
            try {
                while( !responses.containsKey( id ) ) {
                    synchronized( mutex ) {
                        mutex.wait();
                    }
                    log.info( responses.toString() + "\t"
                            + responses.containsKey( id ) + "\t" + id );
                }
                log.info( "CALL Message arrived" );
                return responses.remove( id );
            } catch( InterruptedException e ) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

}
