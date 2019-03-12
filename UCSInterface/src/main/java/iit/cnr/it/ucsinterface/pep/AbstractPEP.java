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
package iit.cnr.it.ucsinterface.pep;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPep;

import iit.cnr.it.ucsinterface.message.MEAN;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import iit.cnr.it.utility.Utility;

/**
 * USED for local experiments
 * 
 * @author antonio
 *
 */
/* abstract */ public class AbstractPEP implements PEPInterface {

    private static final String START_PATH = "/home/antonio/projects/Policies";

    protected static final Logger LOGGER = Logger
        .getLogger( AbstractPEP.class.getName() );

    private XMLPep xmlPep;
    private RequestManagerToExternalInterface requestManager;
    // private ContextHandlerInterface contextHandler;

    // map of unanswered messages, the key is the id of the message
    private HashMap<String, Message> unanswered = new HashMap<>();
    private ConcurrentHashMap<String, Message> responses = new ConcurrentHashMap<>();

    private volatile boolean initialized = false;

    private Object mutex = new Object();

    public AbstractPEP( XMLPep xmlPep ) {
        // BEGIN parameter checking
        if( xmlPep == null ) {
            return;
        }
        // END parameter checking
        this.xmlPep = xmlPep;
        initialized = true;
    }

    public String tryAccess() {
        String request;
        String policy;
        int random = 0;
        if( random % 2 == 0 ) {
            request = Utility.readFileAbsPath( START_PATH + "/RequestAntonio.xml" );
            policy = Utility.readFileAbsPath( START_PATH + "/Policy.xml" );
        } else {
            request = Utility.readFileAbsPath( START_PATH + "/RequestGiacomo.xml" );
            policy = Utility.readFileAbsPath( START_PATH + "/PolicyGiacomo.xml" );
        }
        String pepUri = xmlPep.getIp();

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

            // return (StartAccessResponse) contextHandler
            // .startAccess(startAccessMessage);
        } catch( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

            // return (StartAccessResponse) contextHandler
            // .startAccess(startAccessMessage);
        } catch( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return endAccessMessage.getID();
    }

    /*
     * public void setCHInterface(ContextHandlerInterface contextHandlerInterface)
     * { this.contextHandler = contextHandlerInterface; }
     */

    public void setRequestManagerInterface(
            RequestManagerToExternalInterface requestManager ) {
        this.requestManager = requestManager;
    }

    @Override
    public Message onGoingEvaluation( Message message ) {
        // BEGIN parameter checking
        if( message == null || !( message instanceof ReevaluationResponse ) ) {
            System.err.println( "Message not valid" );
            return null;
        }
        if( !initialized ) {
            System.err.println( "Cannot answer the message" );
            return null;
        }
        if( !message.getSource().equals( PART.CH.toString() )
                || !message.getDestination().equals( PART.PEP.toString() ) ) {
            System.err.println( "Message in invalid format" );
            return null;
        }
        // END parameter checking

        ReevaluationResponse chPepMessage = (ReevaluationResponse) message;
        if( xmlPep.getRevoke().equals( "HARD" ) ) {
            EndAccessMessage endAccess = new EndAccessMessage( PART.PEP.toString(),
                PART.CH.toString() );
            endAccess.setCallback( null, MEAN.API );
            endAccess.setSessionId( chPepMessage.getPDPEvaluation().getSessionId() );
            requestManager.sendMessageToCH( endAccess );
        } else {
            if( chPepMessage.getPDPEvaluation().getResult().contains( "Permit" ) ) {
                LOGGER.log( Level.INFO,
                    "[TIME] RESUME EXECUTION " + System.currentTimeMillis() );
            }
            if( chPepMessage.getPDPEvaluation().getResult().contains( "Deny" ) ) {
                LOGGER.log( Level.INFO,
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
        LOGGER.log( Level.INFO, id );
        TryAccessResponse tryAccessResponse = (TryAccessResponse) waitForResponse(
            id );
        System.out.println(
            "Response: " + tryAccessResponse.getPDPEvaluation().getResult() );
        if( tryAccessResponse.getPDPEvaluation().getResult().contains( "Permit" ) ) {
            LOGGER.log( Level.INFO, "Starting startaccess" );
            id = startAccess( tryAccessResponse.getSessionId() );
            StartAccessResponse startAccessResponse = (StartAccessResponse) waitForResponse(
                id );
            if( startAccessResponse.getPDPEvaluation().getResult()
                .contains( "Permit" ) ) {
                LOGGER.log( Level.INFO, "Permit success" );
                Thread thread = new Thread(
                    new EndThread( tryAccessResponse.getSessionId() ) );
                thread.start();
            }

        }

    }

    private class EndThread implements Runnable {
        private String sessionId;

        public EndThread( String s ) {
            this.sessionId = s;
        }

        @Override
        public void run() {
            int random = ThreadLocalRandom.current().nextInt( 20, 30 );
            try {
                Thread.sleep( random * 1000 );
                String id = endAccess( sessionId );
                EndAccessResponse endAccessResponse;
                endAccessResponse = (EndAccessResponse) waitForResponse( id );
                LOGGER.log( Level.INFO,
                    endAccessResponse.getPDPEvaluation().getResult() );
            } catch( InterruptedException | ExecutionException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
                    LOGGER.log( Level.INFO, responses.toString() + "\t"
                            + responses.containsKey( id ) + "\t" + id );
                }
                LOGGER.log( Level.INFO, "CALL Message arrived" );
                return responses.remove( id );
            } catch( InterruptedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

}
