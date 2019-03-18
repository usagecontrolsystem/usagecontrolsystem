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
package iit.cnr.it.peprest;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;

import it.cnr.iit.ucsinterface.message.MEAN;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToExternalInterface;
import it.cnr.iit.utility.Utility;

import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.peprest.configuration.PEPConf;
import iit.cnr.it.peprest.messagetrack.MessageStorage;
import iit.cnr.it.peprest.messagetrack.MessageStorageInterface;
import iit.cnr.it.peprest.messagetrack.MessagesPerSession;
import iit.cnr.it.peprest.proxy.ProxyRequestManager;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * This is the PEP using rest
 *
 * @author antonio
 *
 */
@Component
public class PEPRest implements PEPInterface, Runnable {

    protected static final Logger LOGGER = Logger.getLogger( PEPRest.class.getName() );
    private static final String DENY = DecisionType.DENY.value();
    private static final String PERMIT = DecisionType.PERMIT.value();

    private Configuration configuration;
    private PEPConf pepConf;

    private RequestManagerToExternalInterface requestManager;

    // map of unanswered messages, the key is the id of the message
    private ConcurrentHashMap<String, Message> unanswered = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Message> responses = new ConcurrentHashMap<>();
    private MessageStorage messageHistory = new MessageStorage();

    private Object mutex = new Object();

    private volatile boolean initialized = false;

    public PEPRest() {
        configuration = Utility.retrieveConfiguration( "conf.xml", Configuration.class );
        if( configuration == null ) {
            LOGGER.log( Level.SEVERE, "Unable to load configuration." );
            return;
        }
        pepConf = configuration.getPepConf();
        requestManager = new ProxyRequestManager( configuration.getRMConf() );
        initialized = true;
    }

    public String tryAccess() {
        String request = Utility.readFileAbsPath( pepConf.getRequestPath() );
        String policy = Utility.readFileAbsPath( pepConf.getPolicyPath() );

        TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder( configuration.getPepConf().getId(),
            configuration.getPepConf().getIp() );
        tryAccessBuilder.setPepUri( buildOnGoingEvaluationInterface() ).setPolicy( policy ).setRequest( request );
        TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
        tryAccessMessage.setCallback( buildResponseInterface( "tryAccessResponse" ), MEAN.REST );
        LOGGER.log( Level.INFO, "[TIME] TRYACCESS " + System.currentTimeMillis() );
        Message message = requestManager.sendMessageToCH( tryAccessMessage );
        if( message.isDeliveredToDestination() ) {
            unanswered.put( tryAccessMessage.getID(), tryAccessMessage );
            messageHistory.addMessage( tryAccessMessage );
            return tryAccessMessage.getID();
        } else {
            LOGGER.log( Level.WARNING, "isDeliveredToDestination: " + message.isDeliveredToDestination() );
            throw Throwables.propagate( new IllegalAccessException( "Unable to deliver messsage to UCS" ) );
        }
    }

    public String startAccess( String sessionId ) {
        StartAccessMessage startAccessMessage = new StartAccessMessage( configuration.getPepConf().getId(),
            configuration.getPepConf().getIp() );
        startAccessMessage.setSessionId( sessionId );
        startAccessMessage.setCallback( buildResponseInterface( "startAccessResponse" ), MEAN.REST );
        try {
            LOGGER.log( Level.INFO, "[TIME] STARTACCESS " + System.currentTimeMillis() );
            Message message = requestManager.sendMessageToCH( startAccessMessage );
            if( message.isDeliveredToDestination() ) {
                unanswered.put( startAccessMessage.getID(), startAccessMessage );
                messageHistory.addMessage( startAccessMessage );
                return startAccessMessage.getID();
            } else {
                LOGGER.log( Level.WARNING, "isDeliveredToDestination: " + message.isDeliveredToDestination() );
                throw Throwables.propagate( new IllegalAccessException( "Unable to deliver messsage to UCS" ) );
            }
        } catch( Exception e ) {
            LOGGER.log( Level.SEVERE, e.getLocalizedMessage() );
            throw Throwables.propagate( e );
        }
    }

    public String endAccess( String sessionId ) {
        EndAccessMessage endAccessMessage = new EndAccessMessage( configuration.getPepConf().getId(),
            configuration.getPepConf().getIp() );
        endAccessMessage.setSessionId( sessionId );
        endAccessMessage.setCallback( buildResponseInterface( "endAccessResponse" ), MEAN.REST );
        try {
            System.out.println( "[TIME] ENDACCESS " + System.currentTimeMillis() );
            Message message = requestManager.sendMessageToCH( endAccessMessage );
            if( message.isDeliveredToDestination() ) {
                unanswered.put( endAccessMessage.getID(), endAccessMessage );
                messageHistory.addMessage( endAccessMessage );
                return endAccessMessage.getID();
            } else {
                LOGGER.log( Level.INFO, "isDeliveredToDestination: " + message.isDeliveredToDestination() );
                throw Throwables.propagate( new IllegalAccessException( "Unable to deliver messsage to UCS" ) );
            }
        } catch( Exception e ) {
            LOGGER.log( Level.SEVERE, e.getLocalizedMessage() );
            throw Throwables.propagate( e );
        }
    }

    @Override
    @Async
    public Message onGoingEvaluation( Message message ) {
        // BEGIN parameter checking
        if( message == null || !( message instanceof ReevaluationResponse ) ) {
            LOGGER.log( Level.SEVERE, "Message not valid" );
            throw Throwables.propagate( new IllegalArgumentException( "Invalid message type'" ) );
        }
        if( !initialized ) {
            LOGGER.log( Level.SEVERE, "Cannot answer the message due to not properly initilization." );
            throw Throwables.propagate( new IllegalStateException( "PEP is not properly initilization" ) );
        }
        // END parameter checking
        responses.put( message.getID(), message );
        messageHistory.addMessage( message );
        LOGGER.log( Level.INFO, "[TIME] ON_GOING_EVAL " + System.currentTimeMillis() );

        ReevaluationResponse chPepMessage = (ReevaluationResponse) message;
        if( pepConf.getRevoke().equals( "HARD" ) ) {
            LOGGER.log( Level.INFO, "[TIME] sending endacces " + System.currentTimeMillis() );
            EndAccessMessage endAccess = new EndAccessMessage( configuration.getPepConf().getId(),
                configuration.getPepConf().getIp() );
            endAccess.setCallback( null, MEAN.REST );
            endAccess.setSessionId( chPepMessage.getPDPEvaluation().getSessionId() );

            message = requestManager.sendMessageToCH( endAccess );
            if( message.isDeliveredToDestination() ) {
                unanswered.put( endAccess.getID(), endAccess );
                messageHistory.addMessage( endAccess );
            } else {
                LOGGER.log( Level.INFO, "isDeliveredToDestination: " + message.isDeliveredToDestination() );
                throw Throwables.propagate( new IllegalAccessException( "Unable to deliver messsage to UCS" ) );
            }
        } else {
            // generic case to cater for multiple scenarios, e.g. pause/resume/pause/end etc...
            if( chPepMessage.getPDPEvaluation().getResult().contains( PERMIT ) ) {
                LOGGER.log( Level.INFO, "RESUME EXECUTION" );
            }
            if( chPepMessage.getPDPEvaluation().getResult().contains( DENY ) ) {
                LOGGER.log( Level.INFO, "STOP EXECUTION" );
            }
        }

        message.setMotivation( "OK" );
        return message;
    }

    @Async
    public void receiveResponseOld( Message message ) {
        responses.put( message.getID(), message );
        synchronized( mutex ) {
            mutex.notifyAll();
        }
    }

    @Override
    @Async
    public String receiveResponse( Message message ) {
        try {
            responses.put( message.getID(), message );
            unanswered.remove( message.getID() );
            messageHistory.addMessage( message );
            return handleResponse( message );
        } catch( Exception e ) {
            LOGGER.log( Level.SEVERE, "Error occured while evaluating the response: " + e.getLocalizedMessage() );
            throw Throwables.propagate( e );
        }
    }

    private String handleResponse( Message message ) throws Exception {
        if( message instanceof TryAccessResponse ) {
            return handleTryAccessResponse( (TryAccessResponse) message );
        } else if( message instanceof StartAccessResponse ) {
            return handleStartAccessResponse( (StartAccessResponse) message );
        } else if( message instanceof ReevaluationResponse ) {
            return handleReevaluationResponse( (ReevaluationResponse) message );
        } else if( message instanceof EndAccessResponse ) {
            return handleEndAccessResponse( (EndAccessResponse) message );
        } else {
            throw new Exception( "INVALID MESSAGE: " + message.toString() );
        }
    }

    /**
     * Function that handles a tryAccessResponse
     *
     * @param response the response received by the UCS
     * @return a String stating the result of the evaluation or the ID of the startaccess message
     */
    private String handleTryAccessResponse( TryAccessResponse response ) {
        LOGGER.info( " Evaluation " + response.getPDPEvaluation().getResult() );
        if( response.getPDPEvaluation().getResult().contains( PERMIT ) ) {
            return startAccess( response.getSessionId() );
        }
        return response.getPDPEvaluation().getResult();
    }

    private String handleStartAccessResponse( StartAccessResponse response ) {
        LOGGER.info( response.getID() + " Evaluation " + response.getPDPEvaluation().getResult() );
        // TODO: what happens if the start access response is deny?
        return response.getPDPEvaluation().getResult();
    }

    private String handleReevaluationResponse( ReevaluationResponse response ) {
        // TODO isn't this redundant because onGoingEvaluation() is called directly from the rest controller
        onGoingEvaluation( response );
        return response.getPDPEvaluation().getResult();
    }

    private String handleEndAccessResponse( EndAccessResponse response ) {
        LOGGER.info( response.getID() + " Evaluation " + response.getPDPEvaluation().getResult() );
        return response.getPDPEvaluation().getResult();
    }

    @Override
    public void run() { // TODO: this method is for local demo tests and needs to be re-coded for PROD
        try {
            String id = tryAccess();
            TryAccessResponse tryAccessResponse = (TryAccessResponse) waitForResponse( id );
            LOGGER.log( Level.INFO, "[TIME] TRYACCESS END " + System.currentTimeMillis() );
            if( tryAccessResponse.getPDPEvaluation().getResult().contains( PERMIT ) ) {
                id = startAccess( tryAccessResponse.getSessionId() );
                StartAccessResponse startAccessResponse = (StartAccessResponse) waitForResponse( id );
                LOGGER.log( Level.INFO, "[TIME] STARTACCESS END " + System.currentTimeMillis() );
                if( startAccessResponse.getPDPEvaluation().getResult().contains( PERMIT ) ) {} else {
                    LOGGER.log( Level.SEVERE, "[TIME] STARTACCESS DENIED " + System.currentTimeMillis() );
                }
            } else {
                LOGGER.log( Level.SEVERE, "[TIME] TRYACCESS DENIED " + System.currentTimeMillis() );
            }
        } catch( Exception e ) {
            LOGGER.log( Level.SEVERE, e.getLocalizedMessage() );
        }
    }

    @VisibleForTesting
    Message waitForResponse( String id ) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TaskBody taskBody = new TaskBody( id );
        return executor.submit( taskBody ).get();
    }

    private class TaskBody implements Callable<Message> {

        private String id;

        public TaskBody( String id ) {
            this.id = id;
        }

        @Override
        public Message call() {
            try {
                while( !responses.containsKey( id ) ) {
                    LOGGER.log( Level.INFO, "First wait" );
                    synchronized( mutex ) {
                        mutex.wait();
                    }
                }
                LOGGER.log( Level.INFO, "WAKE UP!" );
                return responses.remove( id );
            } catch( InterruptedException e ) {
                LOGGER.log( Level.SEVERE, e.getLocalizedMessage() );
                return null;
            }
        }
    }

    public ConcurrentHashMap<String, Message> getResponses() {
        return responses;
    }

    private final String buildResponseInterface( String name ) {
        StringBuilder response = new StringBuilder();
        response.append( "http://" + pepConf.getIp() + ":" );
        response.append( pepConf.getPort() + "/" );
        response.append( name );
        return response.toString();
    }

    private String buildOnGoingEvaluationInterface() {
        return buildResponseInterface( pepConf.getStatusChanged() );
    }

    public void end( String sessionId ) throws InterruptedException, ExecutionException {
        LOGGER.log( Level.INFO, "[TIME] Sending endAccess " + System.currentTimeMillis() );
        endAccess( sessionId );
    }

    public ConcurrentHashMap<String, Message> getUnanswered() {
        return unanswered;
    }

    /**
     * Retrieves the sessionId assigned in the tryAccessResponse
     * @param messageId the messageId assigned in the tryAccess request
     * @return an optional containing either the sessionId either nothing
     */
    public Optional<String> getSessionIdInTryAccess( String messageId ) {
        if( messageId == null || messageId.isEmpty() ) {
            throw new NullPointerException( "Passed message is null" );
        }
        Optional<Message> message = getMessageFromId( messageId );
        if( message.isPresent() ) {
            TryAccessResponse response = (TryAccessResponse) message.get();
            return Optional.ofNullable( response.getSessionId() );
        } else {
            return Optional.empty();
        }
    }

    /**
     * Retrieves the evaluation from the returned messageId
     * @param messageId the messageId assigned to that evaluation
     * @return an optional containing either the required evaluation or an empty one
     */
    public Optional<String> getEvaluationResult( String messageId ) {
        if( messageId == null || messageId.isEmpty() ) {
            throw new NullPointerException( "Passed message is null" );
        }
        Optional<Message> optional = getMessageFromId( messageId );
        if( optional.isPresent() ) {
            Message message = optional.get();
            return extractEvaluationFromMessage( message );
        } else {
            return Optional.empty();
        }
    }

    /**
     * Given the message extracts the result of the evaluation
     * @param message the message from which the evaluation has to be extraced
     * @return an optional containing either the result as a string or nothing
     */
    private Optional<String> extractEvaluationFromMessage( Message message ) {
        if( message instanceof TryAccessResponse ) {
            return Optional.ofNullable( ( (TryAccessResponse) message ).getPDPEvaluation().getResult() );
        }
        if( message instanceof StartAccessResponse ) {
            return Optional.ofNullable( ( (StartAccessResponse) message ).getPDPEvaluation().getResult() );
        }
        if( message instanceof EndAccessResponse ) {
            return Optional.ofNullable( ( (EndAccessResponse) message ).getPDPEvaluation().getResult() );
        }
        return Optional.empty();
    }

    /**
     * Retrieves a message in the responses map
     * @param messageId the messageid assigned in the evaluation
     * @return an optional containing the message or nothing
     */
    private Optional<Message> getMessageFromId( String messageId ) {
        if( responses.containsKey( messageId ) ) {
            return Optional.of( responses.get( messageId ) );
        } else {
            return Optional.empty();
        }
    }

    public MessageStorageInterface getMessageHistory() {
        return messageHistory;
    }

    public MessagesPerSession getMessagesPerSession() {
        return messageHistory;
    }

    public void setMessageStorage( MessageStorage messageStorage ) {
        this.messageHistory = messageStorage;
    }

}
