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
package it.cnr.iit.peprest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.common.base.Throwables;

import it.cnr.iit.peprest.configuration.PEPProperties;
import it.cnr.iit.peprest.configuration.UCSProxyProperties;
import it.cnr.iit.peprest.messagetrack.MessageStorage;
import it.cnr.iit.peprest.messagetrack.MessageStorageInterface;
import it.cnr.iit.peprest.messagetrack.MessagesPerSession;
import it.cnr.iit.peprest.proxy.UCSProxy;
import it.cnr.iit.ucsinterface.message.EvaluatedResponse;
import it.cnr.iit.ucsinterface.message.MEAN;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.requestmanager.UCSCHInterface;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.wrappers.PolicyWrapper;
import it.cnr.iit.xacmlutilities.wrappers.RequestWrapper;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * This is the PEP using rest
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
@Component
public class PEPRest implements PEPInterface {

    private static final Logger log = Logger.getLogger( PEPRest.class.getName() );

    private static final String UNABLE_TO_DELIVER_MESSSAGE_TO_UCS = "Unable to deliver messsage to UCS";
    private static final String IS_MSG_DELIVERED_TO_DESTINATION = "isDeliveredToDestination: {0} ";

    // map of unanswered messages, the key is the id of the message
    private ConcurrentMap<String, Message> unanswered = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Message> responses = new ConcurrentHashMap<>();
    private MessageStorage messageStorage = new MessageStorage();

    @Autowired
    private PEPProperties pep;

    @Autowired
    private UCSCHInterface ucs;

    @Bean
    public PEPProperties getPEPProperties() {
        return new PEPProperties();
    }

    @Bean
    public UCSProxyProperties getUCSProxyProperties() {
        return new UCSProxyProperties();
    }

    @Bean
    public UCSCHInterface getUCSInterface() {
        return new UCSProxy();
    }

    public String tryAccess() {
        RequestWrapper request = RequestWrapper.build( Utility.readFileAbsPath( pep.getRequestPath() ) );
        PolicyWrapper policy = PolicyWrapper.build( Utility.readFileAbsPath( pep.getPolicyPath() ) );
        TryAccessMessage message = buildTryAccessMessage( request, policy );

        log.log( Level.INFO, "tryAccess at {0} ", System.currentTimeMillis() );
        if( ucs.sendMessageToCH( message ).isDelivered() ) {
            unanswered.put( message.getMessageId(), message );
            messageStorage.addMessage( message );
            return message.getMessageId();
        } else {
            log.log( Level.WARNING, IS_MSG_DELIVERED_TO_DESTINATION, message.isDelivered() );
            throw Throwables.propagate( new IllegalAccessException( UNABLE_TO_DELIVER_MESSSAGE_TO_UCS ) );
        }
    }

    public TryAccessMessage buildTryAccessMessage( RequestWrapper request, PolicyWrapper policy ) {
        TryAccessMessage message = new TryAccessMessage( pep.getId(), pep.getBaseUri() );
        message.setPepUri( buildOnGoingEvaluationInterface() );
        message.setPolicy( policy.getPolicy() );
        message.setRequest( request.getRequest() );
        message.setCallback( buildResponseApi( "tryAccessResponse" ), MEAN.REST );
        return message;
    }

    public String startAccess( String sessionId ) {
        log.log( Level.INFO, "startAccess at {0} ", System.currentTimeMillis() );
        StartAccessMessage message = buildStartAccessMessage( sessionId );
        if( ucs.sendMessageToCH( message ).isDelivered() ) {
            unanswered.put( message.getMessageId(), message );
            messageStorage.addMessage( message );
            return message.getMessageId();
        } else {
            log.log( Level.WARNING, IS_MSG_DELIVERED_TO_DESTINATION, message.isDelivered() );
            throw Throwables.propagate( new IllegalAccessException( UNABLE_TO_DELIVER_MESSSAGE_TO_UCS ) );
        }
    }

    public StartAccessMessage buildStartAccessMessage( String sessionId ) {
        StartAccessMessage message = new StartAccessMessage( pep.getId(), pep.getBaseUri() );
        message.setSessionId( sessionId );
        message.setCallback( buildResponseApi( "startAccessResponse" ), MEAN.REST );
        return message;
    }

    public String endAccess( String sessionId ) {
        EndAccessMessage message = buildEndAccessMessage( sessionId, buildResponseApi( "endAccessResponse" ) );
        log.log( Level.INFO, "endAccess at {0} ", System.currentTimeMillis() );
        if( ucs.sendMessageToCH( message ).isDelivered() ) {
            unanswered.put( message.getMessageId(), message );
            messageStorage.addMessage( message );
            return message.getMessageId();
        } else {
            log.log( Level.INFO, IS_MSG_DELIVERED_TO_DESTINATION, message.isDelivered() );
            throw Throwables.propagate( new IllegalAccessException( UNABLE_TO_DELIVER_MESSSAGE_TO_UCS ) );
        }
    }

    public EndAccessMessage buildEndAccessMessage( String sessionId, String responseInterface ) {
        EndAccessMessage message = new EndAccessMessage( pep.getId(), pep.getBaseUri() );
        message.setSessionId( sessionId );
        message.setCallback( responseInterface, MEAN.REST );
        return message;
    }

    @Override
    @Async
    public Message onGoingEvaluation( ReevaluationResponse message ) {
        responses.put( message.getMessageId(), message );
        messageStorage.addMessage( message );
        log.log( Level.INFO, "onGoingEvaluation at {0} ", System.currentTimeMillis() );

        if( pep.getRevokeType().equals( "HARD" ) ) {
            log.log( Level.INFO, "endAcces sent at {0} ", System.currentTimeMillis() );

            EndAccessMessage endAccess = buildEndAccessMessage( message.getPDPEvaluation().getSessionId(), null );
            if( ucs.sendMessageToCH( endAccess ).isDelivered() ) {
                unanswered.put( endAccess.getMessageId(), endAccess );
                messageStorage.addMessage( endAccess );
            } else {
                log.log( Level.INFO, IS_MSG_DELIVERED_TO_DESTINATION, message.isDelivered() );
                throw Throwables.propagate( new IllegalAccessException( UNABLE_TO_DELIVER_MESSSAGE_TO_UCS ) );
            }
        } else {
            // generic case to cater for multiple scenarios, e.g. pause/resume/pause/end etc...
            if( message.getPDPEvaluation().isDecision( DecisionType.PERMIT ) ) {
                log.info( "RESUME EXECUTION" );
            } else if( message.getPDPEvaluation().isDecision( DecisionType.DENY ) ) {
                log.info( "STOP EXECUTION" );
            }
        }
        message.setMotivation( "OK" );
        return message;
    }

    @Override
    @Async
    public String receiveResponse( Message message ) {
        try {
            responses.put( message.getMessageId(), message );
            unanswered.remove( message.getMessageId() );
            messageStorage.addMessage( message );
            return handleResponse( message );
        } catch( Exception e ) { // NOSONAR
            log.severe( "Error occured while evaluating the response: " + e.getLocalizedMessage() ); // NOSONAR
            throw Throwables.propagate( e );
        }
    }

    private String handleResponse( Message message ) {
        String response;
        if( message instanceof TryAccessResponse ) {
            response = handleTryAccessResponse( (TryAccessResponse) message );
        } else if( message instanceof EvaluatedResponse ) {
            response = ( (EvaluatedResponse) message ).getPDPEvaluation().getResult();
        } else {
            throw new IllegalArgumentException( "INVALID MESSAGE: " + message.toString() );
        }
        log.log( Level.INFO, "Evaluation {0} ", response );
        return response;
    }

    /**
     * Function that handles a tryAccessResponse
     *
     * @param response the response received by the UCS
     * @return a String stating the result of the evaluation or the ID of the startaccess message
     */
    private String handleTryAccessResponse( TryAccessResponse response ) {
        if( response.getPDPEvaluation().isDecision( DecisionType.PERMIT ) ) {
            return startAccess( response.getSessionId() );
        }
        return response.getPDPEvaluation().getResult();
    }

    private final String buildResponseApi( String name ) {
        try {
            return new URL( new URL( pep.getBaseUri() ), name ).toString();
        } catch( MalformedURLException e ) {
            return null;
        }
    }

    private String buildOnGoingEvaluationInterface() {
        return buildResponseApi( pep.getApiStatusChanged() );
    }

    public void end( String sessionId ) {
        log.log( Level.INFO, "endAccess sent at {0} ", System.currentTimeMillis() );
        endAccess( sessionId );
    }

    public ConcurrentMap<String, Message> getResponses() {
        return responses;
    }

    public ConcurrentMap<String, Message> getUnanswered() {
        return unanswered;
    }

    /**
     * Retrieves the sessionId assigned in the tryAccessResponse
     * @param messageId the messageId assigned in the tryAccess request
     * @return an optional containing either the sessionId either nothing
     */
    public Optional<String> getSessionIdInTryAccess( String messageId ) {
        Reject.ifBlank( messageId );
        Optional<Message> message = getMessageFromId( messageId );
        if( message.isPresent() ) {
            TryAccessResponse response = (TryAccessResponse) message.get();
            return Optional.ofNullable( response.getSessionId() );
        }
        return Optional.empty();
    }

    /**
     * Retrieves the evaluation from the returned messageId
     * @param messageId the messageId assigned to that evaluation
     * @return an optional containing either the required evaluation or an empty one
     */
    public Optional<String> getEvaluationResult( String messageId ) {
        Reject.ifBlank( messageId );
        Optional<Message> optional = getMessageFromId( messageId );
        if( optional.isPresent() ) {
            Message message = optional.get();
            if( message instanceof EvaluatedResponse ) {
                String result = ( (EvaluatedResponse) message ).getPDPEvaluation().getResult();
                return Optional.of( result );
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves a message in the responses map
     * @param messageId the messageid assigned in the evaluation
     * @return an optional containing the message or nothing
     */
    private Optional<Message> getMessageFromId( String messageId ) {
        if( !responses.containsKey( messageId ) ) {
            return Optional.empty();
        }
        return Optional.of( responses.get( messageId ) );
    }

    public MessageStorageInterface getMessageStorage() {
        return messageStorage;
    }

    public MessagesPerSession getMessagesPerSession() {
        return messageStorage;
    }

    public void setMessageStorage( MessageStorage messageStorage ) {
        this.messageStorage = messageStorage;
    }

}
