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
package it.cnr.iit.ucsrest.contexthandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.ucs.exceptions.StatusException;
import it.cnr.iit.ucs.message.attributechange.AttributeChangeMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucs.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucs.sessionmanager.SessionAttributesBuilder;
import it.cnr.iit.ucs.sessionmanager.SessionInterface;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacml.Attribute;
import it.cnr.iit.xacml.Category;
import it.cnr.iit.xacml.PolicyTags;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * The context handler coordinates the ucs operations and spawns a thread in charge of  monitoring
 * eventual changes in the value of the attributes.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class ContextHandler extends AbstractContextHandler {

    private static final Logger log = Logger.getLogger( ContextHandler.class.getName() );

    @Deprecated
    public static final String PEP_ID_SEPARATOR = "#";

    private AttributeMonitor attributeMonitor;

    public ContextHandler( ContextHandlerProperties properties ) {
        super( properties );
        attributeMonitor = new AttributeMonitor( this );
    }

    /**
     * TryAccess method invoked by the PEP
     */
    @Override
    public TryAccessResponse tryAccess( TryAccessMessage message ) throws PolicyException, RequestException {
        log.log( Level.INFO, "TryAccess received at {0}", new Object[] { System.currentTimeMillis() } );
        Reject.ifNull( message, "TryAccessMessage is null" );

        PolicyWrapper policy = retrievePolicyWrapper( message );
        RequestWrapper fatRequest = RequestWrapper.build( message.getRequest(), getPipRegistry() );
        fatRequest.fatten( STATUS.TRY );
        log.info( "TryAccess fattened request contents : \n" + fatRequest.getRequest() );

        PDPEvaluation evaluation = getPdp().evaluate( fatRequest, policy, STATUS.TRY );
        Reject.ifNull( evaluation );
        log.log( Level.INFO, "TryAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        String sessionId = generateSessionId();
        evaluation.setSessionId( sessionId );
        getObligationManager().translateObligations( evaluation, STATUS.TRY.name() );

        if( evaluation.isDecision( DecisionType.PERMIT ) ) {
            // If access decision is PERMIT create entry in SessionManager
            RequestWrapper request = RequestWrapper.build( message.getRequest(), getPipRegistry() );
            createSession( message, request, policy, sessionId );
        }

        return buildTryAccessResponse( message, evaluation, sessionId );
    }

    private TryAccessResponse buildTryAccessResponse( TryAccessMessage message, PDPEvaluation evaluation, String sessionId ) {
        TryAccessResponse response = new TryAccessResponse( uri.getHost(), message.getSource(), message.getMessageId() );
        response.setSessionId( sessionId );
        response.setEvaluation( evaluation );
        return response;
    }

    /**
     * Retrieves the policy to be used to evaluate the request
     *
     * @param message
     *            the message received by the context handler
     * @return an optional hopefully containing the policy
     * @throws PolicyException
     */
    private PolicyWrapper retrievePolicyWrapper( TryAccessMessage message ) throws PolicyException {
        String policy = message.getPolicy();
        if( policy == null && message.getPolicyId() != null ) {
            policy = getPap().retrievePolicy( message.getPolicyId() );
        }
        return PolicyWrapper.build( policy );
    }

    /**
     * It creates a new session id
     * @return session id to associate to the incoming session during the tryAccess
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * This function creates a new session in the session manager.
     *
     * @param message
     *            the message
     * @param request
     *            the original request, not the fat one because, whenever we need to re-evaluate
     *            the request we will retrieval from the various PIPs a fresh value
     * @param policy
     *            the policy
     * @param sessionId
     *            the sessionId
     */
    private void createSession( TryAccessMessage message, RequestWrapper request, PolicyWrapper policy, String sessionId ) {
        log.log( Level.INFO, "Creating a new session : {0} ", sessionId );

        String pepUri = uri.getHost() + PEP_ID_SEPARATOR + message.getSource();

        // retrieve the id of ongoing attributes
        SessionAttributesBuilder sessionAttributeBuilder = new SessionAttributesBuilder();

        List<Attribute> onGoingAttributes = policy.getAttributesForCondition( PolicyTags.getCondition( STATUS.START ) );
        sessionAttributeBuilder.setOnGoingAttributesForSubject( getAttributesForCategory( onGoingAttributes, Category.SUBJECT ) )
            .setOnGoingAttributesForAction( getAttributesForCategory( onGoingAttributes, Category.ACTION ) )
            .setOnGoingAttributesForResource( getAttributesForCategory( onGoingAttributes, Category.RESOURCE ) )
            .setOnGoingAttributesForEnvironment( getAttributesForCategory( onGoingAttributes, Category.ENVIRONMENT ) );

        sessionAttributeBuilder.setSubjectName( request.getRequestType().extractValue( Category.SUBJECT ) )
            .setResourceName( request.getRequestType().extractValue( Category.RESOURCE ) )
            .setActionName( request.getRequestType().extractValue( Category.ACTION ) );

        sessionAttributeBuilder.setSessionId( sessionId ).setPolicySet( policy.getPolicy() ).setOriginalRequest( request.getRequest() )
            .setStatus( STATUS.TRY.name() ).setPepURI( pepUri ).setMyIP( uri.getHost() );

        // insert all the values inside the session manager
        if( !getSessionManager().createEntry( sessionAttributeBuilder.build() ) ) {
            log.log( Level.SEVERE, "Session \"{0}\" has not been stored correctly", sessionId );
        }
    }

    /**
     * Retrieves the AttributeIDs of the attributes used for the ongoing
     * evaluation
     *
     * @param onGoingAttributes
     *            the list of attributes used for ongoing evaluation
     * @param category
     *            the category of the attributes
     * @return the list of the string representing the IDs of the attributes
     */
    private List<String> getAttributesForCategory( List<Attribute> onGoingAttributes, Category category ) {
        ArrayList<String> attributeIds = new ArrayList<>();
        for( Attribute attribute : onGoingAttributes ) {
            if( attribute.getCategory() == category ) {
                attributeIds.add( attribute.getAttributeId() );
            }
        }
        if( attributeIds.isEmpty() ) {
            return new ArrayList<>();
        }
        return attributeIds;
    }

    /**
     * startAccess method invoked by PEP
     * @throws PolicyException
     * @throws RequestException
     */
    @Override
    public StartAccessResponse startAccess( StartAccessMessage message )
            throws StatusException, PolicyException, RequestException {
        log.log( Level.INFO, "StartAccess begin scheduling at {0}", System.currentTimeMillis() );

        Optional<SessionInterface> optSession = getSessionManager().getSessionForId( message.getSessionId() );
        Reject.ifAbsent( optSession, "StartAccess: no session for id " + message.getSessionId() );
        SessionInterface session = optSession.get(); // NOSONAR

        // Check if the session has the correct status
        if( !session.isStatus( STATUS.TRY.name() ) ) {
            log.log( Level.SEVERE, "StartAccess: wrong status for session {0}", message.getSessionId() );
            throw new StatusException( "StartAccess: tryaccess must be performed yet for session " + message.getSessionId() );
        }

        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest(), getPipRegistry() );
        request.fatten( STATUS.START );

        PDPEvaluation evaluation = getPdp().evaluate( request, policy, STATUS.START );
        Reject.ifNull( evaluation );
        log.log( Level.INFO, "StartAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        evaluation.setSessionId( message.getSessionId() );
        getObligationManager().translateObligations( evaluation, STATUS.START.name() );

        if( evaluation.isDecision( DecisionType.PERMIT ) ) {
            if( !getSessionManager().updateEntry( message.getSessionId(), STATUS.START.name() ) ) {
                log.log( Level.SEVERE, "StartAccess error, sessionId {0} status update failed", message.getSessionId() );
            }
        } else {
            List<Attribute> attributes = policy.getAttributesForCondition( PolicyTags.getCondition( STATUS.START ) );
            if( revoke( session, attributes ) && !getSessionManager().deleteEntry( message.getSessionId() ) ) {
                log.log( Level.SEVERE, "StartAccess error, sessionId {0} deletion failed",
                    message.getSessionId() );
            }
        }

        return buildStartAccessResponse( message, evaluation );
    }

    private StartAccessResponse buildStartAccessResponse( StartAccessMessage message, PDPEvaluation evaluation ) {
        StartAccessResponse response = new StartAccessResponse( message.getDestination(), message.getSource(), message.getMessageId() );
        response.setEvaluation( evaluation );
        return response;
    }

    /**
     * This is the code for the revoke. A revoke is always triggered by and
     * EndAccess, in this function, all the attributes are un-subscribed.
     */
    private synchronized boolean revoke( SessionInterface session, List<Attribute> attributes ) {
        log.log( Level.INFO, "Revoke begins at {0}", System.currentTimeMillis() );

        boolean otherSessions = attributesToUnsubscribe( session.getId(), (ArrayList<Attribute>) attributes );
        if( !otherSessions ) {
            getPipRegistry().unsubscribeAll( attributes );
        }

        if( !getSessionManager().deleteEntry( session.getId() ) ) {
            log.log( Level.SEVERE, "EndAccess: errors during entry deletion for sessionId {0}", session.getId() );
            return false;
        }

        log.log( Level.INFO, "Revoke ends at {0}", System.currentTimeMillis() );
        return true;
    }

    /**
     * This function checks if there are attributes to be unsubscribed.
     * The first step is to retrieve the list of ongoing attributes, then we
     * have to unsubscribe all those attributes that are not needed anymore.
     * @param sessionId
     *            the id of the session we're revoking
     * @param attributes
     *            the JSON object to be filled by this function
     * @return true if there are attributes to unsubscribe, false otherwise
     */
    private boolean attributesToUnsubscribe( String sessionId, ArrayList<Attribute> attributes ) {
        String subjectName = "";
        String resourceName = "";
        String actionName = "";
        // retrieve on going attributes for both subject and object
        Collection<OnGoingAttributesInterface> onGoingAttributes = getSessionManager().getOnGoingAttributes( sessionId );
        List<OnGoingAttributesInterface> subjectOnGoingAttributesList = new LinkedList<>();
        List<OnGoingAttributesInterface> resourceOnGoingAttributesList = new LinkedList<>();
        List<OnGoingAttributesInterface> actionOnGoingAttributesList = new LinkedList<>();
        List<OnGoingAttributesInterface> environmentOnGoingAttributesList = new LinkedList<>();

        // build attribute lists for subject, resource, action and environment
        if( onGoingAttributes != null && !onGoingAttributes.isEmpty() ) {
            // fill the correspondent list of ongoingAttributes
            for( OnGoingAttributesInterface attribute : onGoingAttributes ) {
                if( attribute.getSubjectName() != null && !attribute.getSubjectName().equals( "null" ) ) {
                    subjectOnGoingAttributesList.add( attribute );
                    subjectName = attribute.getSubjectName();
                } else if( attribute.getResourceName() != null && !attribute.getResourceName().equals( "null" ) ) {
                    resourceOnGoingAttributesList.add( attribute );
                    resourceName = attribute.getResourceName();
                } else if( attribute.getActionName() != null && !attribute.getActionName().equals( "null" ) ) {
                    actionOnGoingAttributesList.add( attribute );
                    actionName = attribute.getActionName();
                } else {
                    environmentOnGoingAttributesList.add( attribute );
                }
            }
        }

        // builds up the JSON object that is needed to perform unsubscribe
        boolean otherSessions = true;
        if( onGoingAttributes != null && !onGoingAttributes.isEmpty() ) {
            otherSessions = buildOnGoingAttributes( Category.RESOURCE, attributes, resourceName, otherSessions,
                resourceOnGoingAttributesList );
            otherSessions = buildOnGoingAttributes( Category.SUBJECT, attributes, subjectName, otherSessions,
                subjectOnGoingAttributesList );
            otherSessions = buildOnGoingAttributes( Category.ACTION, attributes, actionName, otherSessions, actionOnGoingAttributesList );
            otherSessions = buildOnGoingAttributes( Category.ENVIRONMENT, attributes, "", otherSessions,
                environmentOnGoingAttributesList );
        }
        return otherSessions;
    }

    private boolean buildOnGoingAttributes( Category category, ArrayList<Attribute> attributes, String name, boolean otherSessions,
            List<OnGoingAttributesInterface> listOngoingAttributes ) {
        for( OnGoingAttributesInterface attribute : listOngoingAttributes ) {
            List<SessionInterface> sessionList = getSessionListForCategory( category, attribute.getAttributeId(), name );
            if( sessionList == null || sessionList.isEmpty() || sessionList.size() == 1 ) {
                otherSessions = false;
                attributes.add( buildAttribute( attribute, name ) );
            }
        }
        return otherSessions;
    }

    private List<SessionInterface> getSessionListForCategory( Category category, String id, String name ) {
        switch( category ) {
            case ENVIRONMENT:
                return getSessionManager()
                    .getSessionsForEnvironmentAttributes( id );
            case ACTION:
                return getSessionManager()
                    .getSessionsForActionAttributes( name, id );
            case SUBJECT:
                return getSessionManager()
                    .getSessionsForSubjectAttributes( name, id );
            case RESOURCE:
                return getSessionManager()
                    .getSessionsForResourceAttributes( name, id );
            default:
                log.severe( "Invalid attribute passed" );
                return new ArrayList<>();
        }
    }

    private Attribute buildAttribute( OnGoingAttributesInterface ongoingAttribute, String name ) {
        Attribute attribute = new Attribute();
        attribute.setAttributeId( ongoingAttribute.getAttributeId() );
        if( !name.isEmpty() ) {
            attribute.setAdditionalInformations( name );
        }
        return attribute;
    }

    /**
     * endAccess method invoked by PEP
     */
    @Override
    public EndAccessResponse endAccess( EndAccessMessage message ) throws StatusException, RequestException, PolicyException {
        log.log( Level.INFO, "EndAccess begins at {0}", System.currentTimeMillis() );
        Reject.ifNull( message, "EndAccessMessage is null" );

        Optional<SessionInterface> optSession = getSessionManager().getSessionForId( message.getSessionId() );
        Reject.ifAbsent( optSession, "EndAccess: no session for id " + message.getSessionId() );
        SessionInterface session = optSession.get(); // NOSONAR

        // Check if the session has the correct status
        if( !( session.isStatus( STATUS.START.name() )
                || session.isStatus( STATUS.REVOKE.name() ) ) ) {
            log.log( Level.INFO, "EndAccess: wrong status for session {0}", message.getSessionId() );
            throw new StatusException( "EndAccess: wrong status for session " + message.getSessionId() );
        }

        log.log( Level.INFO, "EndAccess evaluation starts at {0}", System.currentTimeMillis() );

        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest(), getPipRegistry() );
        request.fatten( STATUS.END );

        PDPEvaluation evaluation = getPdp().evaluate( request, policy, STATUS.END );
        Reject.ifNull( evaluation );
        log.log( Level.INFO, "EndAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        evaluation.setSessionId( message.getSessionId() );
        getObligationManager().translateObligations( evaluation, STATUS.END.name() );

        // access must be revoked
        if( revoke( session, policy.getAttributesForCondition( PolicyTags.getCondition( STATUS.END ) ) ) ) {
            log.log( Level.INFO, "EndAccess evaluation with revoke ends at {0}", System.currentTimeMillis() );
        }

        return buildEndAccessResponse( message, evaluation );
    }

    private EndAccessResponse buildEndAccessResponse( EndAccessMessage message, PDPEvaluation evaluation ) {
        EndAccessResponse response = new EndAccessResponse( message.getDestination(), message.getSource(), message.getMessageId() );
        response.setEvaluation( evaluation );
        return response;
    }

    /**
     * API offered by the context handler to the PIP in case some attribute changes
     */
    @Override
    public void attributeChanged( AttributeChangeMessage message ) {
        log.log( Level.INFO, "Attribute changed received {0}", System.currentTimeMillis() );
        attributeMonitor.add( message );
    }

    /**
     * This is the function where the effective reevaluation takes place.
     */
    public boolean reevaluateSessions( Attribute attribute ) {
        try {
            log.info( "ReevaluateSessions for  attributeId : " + attribute.getAttributeId() );
            List<SessionInterface> sessionList = getSessionListForCategory( attribute.getCategory(), attribute.getAttributeId(),
                attribute.getAdditionalInformations() );
            if( sessionList != null ) {
                for( SessionInterface session : sessionList ) {
                    reevaluate( session );
                }
            }
            return true;
        } catch( Exception e ) {
            log.severe( "Error in Reevaluate sessions : " + e.getMessage() );
        }
        return false;
    }

    public synchronized void reevaluate( SessionInterface session ) throws PolicyException, RequestException {
        log.log( Level.INFO, "Reevaluation begins at {0}", System.currentTimeMillis() );

        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest(), getPipRegistry() );
        request.fatten( STATUS.START );

        PDPEvaluation evaluation = getPdp().evaluate( request, policy, STATUS.START );
        Reject.ifNull( evaluation );
        evaluation.setSessionId( session.getId() );
        getObligationManager().translateObligations( evaluation, STATUS.START.name() );

        log.log( Level.INFO, "Reevaluate evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        if( session.isStatus( STATUS.START.name() )
                && evaluation.isDecision( DecisionType.DENY ) ) {
            log.log( Level.INFO, "Revoke at {0}", System.currentTimeMillis() );
            getSessionManager().updateEntry( session.getId(), STATUS.REVOKE.name() );

        } else if( session.isStatus( STATUS.REVOKE.name() )
                && evaluation.isDecision( DecisionType.PERMIT ) ) {
            log.log( Level.INFO, "Resume at {0}", System.currentTimeMillis() );
            getSessionManager().updateEntry( session.getId(), STATUS.START.name() );
        } else {
            log.log( Level.INFO, "Reevaluation ends without change at {0}", System.currentTimeMillis() );
            return;
        }

        evaluation.setSessionId( session.getId() );
        ReevaluationResponse response = buildReevaluationResponse( evaluation, session.getPEPUri() );
        getRequestManager().sendReevaluation( response );
        log.log( Level.INFO, "Reevaluation ends changing status at {0}", System.currentTimeMillis() );
    }

    private ReevaluationResponse buildReevaluationResponse( PDPEvaluation evaluation, String dest ) {
        String[] destSplitted = dest.split( PEP_ID_SEPARATOR );
        ReevaluationResponse response = new ReevaluationResponse( uri.getHost(), destSplitted[0] );
        response.setPepId( destSplitted[destSplitted.length - 1] );
        response.setEvaluation( evaluation );
        return response;
    }

    @Override
    public void startMonitoringThread() {
        attributeMonitor.setTheadStatus( true );
    }

    @Override
    public void stopMonitoringThread() {
        attributeMonitor.setTheadStatus( false );
    }
}
