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
import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.ucs.message.EvaluatedMessage;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * This is the PEP using rest
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
@Component
public class PEPRest implements PEPInterface {

    private static final Logger log = Logger.getLogger( PEPRest.class.getName() );

    private static final String INVALID_MESSAGE_ID = "0";

    // map of messages : the key is the id of the message
    private ConcurrentMap<String, Message> unansweredMap = new ConcurrentHashMap<>();
    private ConcurrentMap<String, Message> responsesMap = new ConcurrentHashMap<>();
    private MessageStorage messageStorage = new MessageStorage();

    @Autowired
    private PEPProperties pep;

    @Autowired
    private UCSProxy ucs;

    @Bean
    public PEPProperties getPEPProperties() {
        return new PEPProperties();
    }

    @Bean
    public UCSProxyProperties getUCSProxyProperties() {
        return new UCSProxyProperties();
    }

    @Bean
    public UCSProxy getUCSInterface() {
        return new UCSProxy();
    }

    public String tryAccess() {
        log.log( Level.INFO, "TryAccess at {0} ", System.currentTimeMillis() );
        PolicyWrapper policy;
        RequestWrapper request;
        try {
            policy = PolicyWrapper.build( FileUtility.readFileAsString( pep.getPolicyPath() ) );
            request = RequestWrapper.build( FileUtility.readFileAsString( pep.getRequestPath() ) );
        } catch( PolicyException | RequestException e ) {
            return INVALID_MESSAGE_ID;
        }
        TryAccessMessage message = buildTryAccessMessage( request, policy );
        return handleRequest( message );
    }

    public String startAccess( String sessionId ) {
        log.log( Level.INFO, "StartAccess at {0} ", System.currentTimeMillis() );
        StartAccessMessage message = buildStartAccessMessage( sessionId );
        return handleRequest( message );
    }

    public String endAccess( String sessionId ) {
        log.log( Level.INFO, "EndAccess at {0} ", System.currentTimeMillis() );
        EndAccessMessage message = buildEndAccessMessage( sessionId );
        return handleRequest( message );
    }

    @Override
    @Async
    public Message onGoingEvaluation( ReevaluationResponseMessage message ) {
        log.log( Level.INFO, "OnGoingEvaluation at {0} ", System.currentTimeMillis() );
        Reject.ifNull( message );
        PDPEvaluation evaluation = message.getEvaluation();
        Reject.ifNull( evaluation );
        responsesMap.put( message.getMessageId(), message );
        messageStorage.addMessage( message );
        if( pep.getRevokeType().equals( "HARD" ) ) {
            log.log( Level.INFO, "EndAcces sent at {0} ", System.currentTimeMillis() );
            EndAccessMessage endAccess = buildEndAccessMessage( message.getSessionId(), null );
            handleRequest( endAccess );
        } else {
            // generic case to cater for multiple scenarios, e.g. pause/resume/pause/end etc...
            if( evaluation.isDecision( DecisionType.PERMIT ) ) {
                log.info( "RESUME EXECUTION" );
            } else if( evaluation.isDecision( DecisionType.DENY ) ) {
                log.info( "STOP EXECUTION" );
            }
        }
        return message;
    }

    private TryAccessMessage buildTryAccessMessage( RequestWrapper request, PolicyWrapper policy ) {
        TryAccessMessage message = new TryAccessMessage( pep.getId(), pep.getUri() );
        message.setPepUri( buildResponseApi( pep.getApiStatusChanged() ) );
        message.setPolicy( policy.getPolicy() );
        message.setRequest( request.getRequest() );
        message.setCallback( buildResponseApi( OperationName.TRYACCESSRESPONSE_REST ), CONNECTION.REST );
        return message;
    }

    private StartAccessMessage buildStartAccessMessage( String sessionId ) {
        StartAccessMessage message = new StartAccessMessage( pep.getId(), pep.getUri() );
        message.setSessionId( sessionId );
        message.setCallback( buildResponseApi( OperationName.STARTACCESSRESPONSE_REST ), CONNECTION.REST );
        return message;
    }

    private EndAccessMessage buildEndAccessMessage( String sessionId ) {
        return buildEndAccessMessage( sessionId, buildResponseApi( OperationName.ENDACCESSRESPONSE_REST ) );
    }

    private EndAccessMessage buildEndAccessMessage( String sessionId, String responseInterface ) {
        EndAccessMessage message = new EndAccessMessage( pep.getId(), pep.getUri() );
        message.setSessionId( sessionId );
        message.setCallback( responseInterface, CONNECTION.REST );
        return message;
    }

    private String handleRequest( Message message ) {
        Reject.ifNull( message );
        if( ucs.sendMessage( message ) ) {
            unansweredMap.put( message.getMessageId(), message );
            messageStorage.addMessage( message );
            return message.getMessageId();
        } else {
            throw Throwables.propagate( new IllegalAccessException( "Unable to deliver messsage to UCS" ) );
        }
    }

    @Override
    @Async
    public String receiveResponse( Message message ) {
        Reject.ifNull( message );
        try {
            responsesMap.put( message.getMessageId(), message );
            unansweredMap.remove( message.getMessageId() );
            messageStorage.addMessage( message );
            return handleResponse( message );
        } catch( Exception e ) { // NOSONAR
            log.log( Level.SEVERE, "Error occured while evaluating the response: {0}", e.getMessage() );
            throw Throwables.propagate( e );
        }
    }

    private String handleResponse( Message message ) {
        String response;
        if( message instanceof TryAccessResponseMessage ) {
            response = handleTryAccessResponse( (TryAccessResponseMessage) message );
        } else if( message instanceof EvaluatedMessage ) {
            response = ( (EvaluatedMessage) message ).getEvaluation().getResult();
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
    private String handleTryAccessResponse( TryAccessResponseMessage response ) {
        PDPEvaluation evaluation = response.getEvaluation();
        if( evaluation.isDecision( DecisionType.PERMIT ) ) {
            return startAccess( response.getSessionId() );
        }
        return evaluation.getResult();
    }

    private final String buildResponseApi( String name ) {
        try {
            return new URL( new URL( pep.getUri() ), name ).toString();
        } catch( MalformedURLException e ) {
            return null;
        }
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

    public ConcurrentMap<String, Message> getResponses() {
        return responsesMap;
    }

    public ConcurrentMap<String, Message> getUnanswered() {
        return unansweredMap;
    }
}
