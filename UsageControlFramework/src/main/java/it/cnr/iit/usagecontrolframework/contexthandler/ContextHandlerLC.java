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
package it.cnr.iit.usagecontrolframework.contexthandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.exceptions.RevokeException;
import it.cnr.iit.ucs.exceptions.SessionManagerException;
import it.cnr.iit.ucs.exceptions.WrongOrderException;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucsinterface.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionAttributesBuilder;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.wrappers.PolicyWrapper;
import it.cnr.iit.xacmlutilities.wrappers.RequestWrapper;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * This is the class implementing a context-handler with low concurrency.
 * <p>
 * We will provide some different implementations of the context handler, so
 * that the user can pick up the most suitable to its case.
 * The context handler spawns a thread in charge of  monitoring eventual changes
 * in the value of the attributes. This thread stays  in a sleeping state unless
 * it is waken up by the calling of a proper function performed by the PIP.
 * </p>
 * <p>
 * This implementation of the context handler can handle a single request per
 * time. This single thread is represented by the AttributeMonitor
 * actor which implements the Callable<String> interface.
 * This context handler has a blocking queue that will be used for notifications by the
 * various PIPs, since once a notification has been received, all the PIPs will
 * be queried, then this queue MUST contain, unless something changes in the
 * architecture a single element only.
 * <br>
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class ContextHandlerLC extends AbstractContextHandler {

    private static final Logger log = Logger.getLogger( ContextHandlerLC.class.getName() );

    @Deprecated
    public static final String PEP_ID_SEPARATOR = "#";

    // monitors if the value of an attribute changes
    private AttributeMonitor attributeMonitor;
    // queue in charge of storing the changing in the attributes
    private LinkedTransferQueue<PipChMessage> changedAttributesQueue;

    private Thread monitoringThread;
    private boolean monitoringRunning = true;

    public ContextHandlerLC( ContextHandlerProperties chProperties ) {
        super( chProperties );
        changedAttributesQueue = new LinkedTransferQueue<>();
        attributeMonitor = new AttributeMonitor();
        monitoringThread = new Thread( attributeMonitor );
    }

    /**
     * starts the thread in charge of monitoring the changes notified by PIPs
     */
    @Override
    public void startMonitoringThread() {
        monitoringThread.start();
    }

    /**
     * stop the thread in charge of monitoring the changes notified by PIPs
     */
    @Override
    public void stopMonitoringThread() {
        monitoringRunning = false;
    }

    /**
     * TryAccess method invoked by the PEP<br>
     * @param message
     *            the TryAccessMessage received
     */
    @Override
    public void tryAccess( TryAccessMessage message ) {
        Reject.ifNull( message, "TryAccessMessage is null" );
        Reject.ifNull( message.getPolicy(), "TryAccessMessage is policy null" );
        Reject.ifNull( message.getRequest(), "TryAccessMessage is request null" );

        log.log( Level.INFO, "TryAccess received at {0}", new Object[] { System.currentTimeMillis() } );

        Optional<PolicyWrapper> optPolicy = retrievePolicyWrapper( message );
        Reject.ifAbsent( optPolicy ); // NOSONAR TODO send default deny
        PolicyWrapper policy = optPolicy.get();
        RequestWrapper request = RequestWrapper.build( message.getRequest() );
        RequestWrapper fatRequest = fattenRequest( request, policy, STATUS.TRYACCESS );
        log.info( "TryAccess fattened request contents : \n" + fatRequest.getRequest() );

        // Perform the PDP evaluation
        PDPEvaluation evaluation = getPdp().evaluate( fatRequest.getRequest(), policy, STATUS.TRYACCESS );
        log.log( Level.INFO, "TryAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        String sessionId = generateNewSessionId();
        getObligationManager().translateObligations( evaluation, sessionId, ContextHandlerConstants.TRY_STATUS );

        if( evaluation.isPermit() ) {
            // If access decision is PERMIT create entry in SessionManager
            createNewSession( message, request, policy, sessionId );
        }

        TryAccessResponse tryAccessResponse = buildTryAccessResponse( message, evaluation, sessionId );
        getRequestManager().sendMessageToOutside( tryAccessResponse );
    }

    private TryAccessResponse buildTryAccessResponse( TryAccessMessage message, PDPEvaluation evaluation, String sessionId ) {
        TryAccessResponse response = new TryAccessResponse( uri.getHost(), message.getSource(), message.getMessageId() );
        response.setSessionId( sessionId );
        response.setPDPEvaluation( evaluation );
        if( message.isScheduled() ) {
            response.setUCSDestination();
        }
        return response;
    }

    /**
     * Attempt to fill the request in order to let the PDP evaluate it.
     * @param request
     *            the request
     * @param policy
     *            the policy
     * @param status
     *            the status
     */
    // TODO move to RequestWrapper
    private synchronized RequestWrapper fattenRequest( RequestWrapper request, PolicyWrapper policy, STATUS status ) {
        boolean isSubscription = status == STATUS.STARTACCESS;
        List<Attribute> attributes = policy.getAttributesForCondition( POLICY_CONDITION.fromStatus( status ) );
        RequestWrapper fatRequest = RequestWrapper.build( request.getRequest() ); // TODO clone
        fattenRequest( fatRequest, attributes, isSubscription );
        return fatRequest;
    }

    // TODO move to RequestWrapper
    private void fattenRequest( RequestWrapper request, List<Attribute> attributes, boolean isSubscription ) {
        if( !isSubscription ) {
            getPipRegistry().retrieveAll( request.getRequestType() );
        } else {
            getPipRegistry().subscribeAll( request.getRequestType() );
        }

        request.update();
    }

    /**
     * It creates a new session id
     * @return session id to associate to the incoming session during the tryAccess
     */
    private synchronized String generateNewSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Retrieves the policy to be used to evaluate the request
     *
     * @param message
     *            the message received by the context handler
     * @return an optional hopefully containing the policy
     */
    private Optional<PolicyWrapper> retrievePolicyWrapper( TryAccessMessage message ) {
        String policy = message.getPolicy();

        if( policy == null && message.getPolicyId() != null ) {
            policy = getPap().retrievePolicy( message.getPolicyId() );
        }

        return policy != null ? Optional.of( PolicyWrapper.build( policy ) ) : Optional.empty();
    }

    /**
     * This function creates a new session in the session manager.
     *
     * @param message
     *            the message
     * @param request
     *            the original request, not the fat one because, whenever we
     *            need to re-evaluate the request we will retrieval from the
     *            various PIPs a fresh value
     * @param policy
     *            the policy
     * @param sessionId
     *            the sessionId
     */
    private void createNewSession( TryAccessMessage message, RequestWrapper request, PolicyWrapper policy, String sessionId ) {
        log.log( Level.INFO, "TryAccess creating new session : {0} ", sessionId );

        String pepUri = message.isScheduled() ? message.getPepUri()
                : uri.getHost() + PEP_ID_SEPARATOR + message.getSource();
        String ip = message.isScheduled() ? message.getSource() : uri.getHost();

        // try {
        // retrieve the id of ongoing attributes
        SessionAttributesBuilder sessionAttributeBuilder = new SessionAttributesBuilder();

        List<Attribute> onGoingAttributes = policy.getAttributesForCondition( POLICY_CONDITION.STARTACCESS );
        sessionAttributeBuilder.setOnGoingAttributesForSubject( getAttributesForCategory( onGoingAttributes, Category.SUBJECT ) )
            .setOnGoingAttributesForAction( getAttributesForCategory( onGoingAttributes, Category.ACTION ) )
            .setOnGoingAttributesForResource( getAttributesForCategory( onGoingAttributes, Category.RESOURCE ) )
            .setOnGoingAttributesForEnvironment( getAttributesForCategory( onGoingAttributes, Category.ENVIRONMENT ) );

        sessionAttributeBuilder.setSubjectName( request.getRequestType().extractValue( Category.SUBJECT ) )
            .setResourceName( request.getRequestType().extractValue( Category.RESOURCE ) )
            .setActionName( request.getRequestType().extractValue( Category.ACTION ) );

        sessionAttributeBuilder.setSessionId( sessionId ).setPolicySet( policy.getPolicy() ).setOriginalRequest( request.getRequest() )
            .setStatus( ContextHandlerConstants.TRY_STATUS ).setPepURI( pepUri ).setMyIP( ip );

        // insert all the values inside the session manager
        if( !getSessionManager().createEntry( sessionAttributeBuilder.build() ) ) {
            log.log( Level.SEVERE, "TryAccess: session \"{0}\" has not been stored correctly",
                sessionId );
        }
        // } catch( Exception e ) {
        // log.severe( "TryAccess error creating new session : " + e.getMessage() );
        // }

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
    // TODO move to PolicyWrapper ?
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
     * @param message
     *            the StartAccessMessage
     */
    @Override
    public void startAccess( StartAccessMessage message ) throws WrongOrderException, SessionManagerException, RevokeException {
        Optional<SessionInterface> optSession = getSessionManager().getSessionForId( message.getSessionId() );
        Reject.ifAbsent( optSession, "StartAccess: no session for id " + message.getSessionId() ); // TODO default deny
        SessionInterface session = optSession.get();

        log.log( Level.INFO, "StartAccess begin scheduling at {0}", new Object[] { System.currentTimeMillis() } );

        // Check if the session has the correct status
        // TODO update sessionInterface to handle sesion.isStatus(STATUS)
        if( !session.getStatus().equals( ContextHandlerConstants.TRY_STATUS ) ) {
            log.log( Level.SEVERE, "StartAccess: wrong status for session {0}", message.getSessionId() );
            throw new WrongOrderException( "StartAccess: tryaccess must be performed yet for session " + message.getSessionId() );
        }

        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest() );
        RequestWrapper fatRequest = fattenRequest( request, policy, STATUS.STARTACCESS );

        PDPEvaluation evaluation = getPdp().evaluate( fatRequest.getRequest(), policy.getPolicy( POLICY_CONDITION.STARTACCESS ) );
        log.log( Level.INFO, "StartAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        getObligationManager().translateObligations( evaluation, message.getSessionId(), ContextHandlerConstants.START_STATUS );

        if( evaluation.isPermit() ) {
            if( !getSessionManager().updateEntry( message.getSessionId(), ContextHandlerConstants.START_STATUS ) ) {
                log.log( Level.SEVERE, "StartAccess error, sessionId {0} status update failed", message.getSessionId() );
            }
        } else {
            List<Attribute> attributes = policy.getAttributesForCondition( POLICY_CONDITION.STARTACCESS );
            if( revoke( session, attributes ) && !getSessionManager().deleteEntry( message.getSessionId() ) ) {
                log.log( Level.SEVERE, "StartAccess error, sessionId {0} deletion failed",
                    message.getSessionId() );
            }
        }

        StartAccessResponse response = buildStartAccessResponse( message, evaluation );
        getRequestManager().sendMessageToOutside( response );
    }

    private StartAccessResponse buildStartAccessResponse( StartAccessMessage message, PDPEvaluation evaluation ) {
        StartAccessResponse response = new StartAccessResponse( message.getDestination(), message.getSource(), message.getMessageId() );
        response.setPDPEvaluation( evaluation );
        if( message.isScheduled() ) {
            response.setUCSDestination();
        }
        return response;
    }

    /**
     * This is the code for the revoke. A revoke is always triggered by and
     * EndAccess, in this function, all the attributes are unsubscribed
     *
     * @param session
     *            the session for which the revoke has to occur
     * @return true if everything goes ok, false otherwise
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
     * <p>
     * The first step is to retrieve the list of ongoing attributes, then we
     * have to unsubscribe all those attributes that are not needed anymore.
     * </p>
     *
     * @param sessionId
     *            the id of the session we're revoking
     * @param attributes
     *            the JSON object to be filled by this function
     * @return true if there are attributes to unsubscribe, false otherwise
     *         <br>
     *
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

    @Override
    public void endAccess( EndAccessMessage message ) throws WrongOrderException {
        log.log( Level.INFO, "EndAccess begins at {0}", System.currentTimeMillis() );
        Optional<SessionInterface> optSession = getSessionManager().getSessionForId( message.getSessionId() );
        Reject.ifAbsent( optSession, "EndAccess: no session for id " + message.getSessionId() ); // TODO default deny
        SessionInterface session = optSession.get();

        // try {
        // Check if the session has the correct status
        if( ( !session.getStatus().equals( ContextHandlerConstants.START_STATUS )
                && !session.getStatus().equals( ContextHandlerConstants.REVOKE_STATUS ) ) ) {
            log.log( Level.INFO, "EndAccess: wrong status for session {0}", message.getSessionId() );
            throw new WrongOrderException( "EndAccess: wrong status for session " + message.getSessionId() );
        }

        log.log( Level.INFO, "EndAccess evaluation starts at {0}", System.currentTimeMillis() );

        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest() );
        RequestWrapper fatRequest = fattenRequest( request, policy, STATUS.ENDACCESS );

        PDPEvaluation evaluation = getPdp().evaluate( fatRequest.getRequest(),
            policy.getPolicy( POLICY_CONDITION.ENDACCESS ) );

        log.log( Level.INFO, "EndAccess evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        getObligationManager().translateObligations( evaluation, message.getSessionId(), ContextHandlerConstants.END_STATUS );

        // access must be revoked
        List<Attribute> attributes = policy.getAttributesForCondition( POLICY_CONDITION.ENDACCESS );
        if( revoke( session, attributes ) ) {
            log.log( Level.INFO, "EndAccess evaluation with revoke ends at {0}", System.currentTimeMillis() );
        }

        EndAccessResponse response = buildEndAccessResponse( message, evaluation );
        getRequestManager().sendMessageToOutside( response );
        // } catch( Exception e ) {
        // log.severe( e.getMessage() );
        // }
    }

    private EndAccessResponse buildEndAccessResponse( EndAccessMessage message, PDPEvaluation evaluation ) {
        EndAccessResponse response = new EndAccessResponse( message.getDestination(), message.getSource(), message.getMessageId() );
        response.setPDPEvaluation( evaluation );
        if( message.isScheduled() ) {
            response.setUCSDestination();
        }
        return response;
    }

    /**
     * API offered by the context handler to the PIP in case some attribute gets
     * changed
     *
     * @param message
     */
    @Override
    public void attributeChanged( PipChMessage message ) {
        log.log( Level.INFO, "Attribute changed received {0}", System.currentTimeMillis() );
        // non blocking insertion in the queue of attributes changed
        changedAttributesQueue.put( message );
    }

    /**
     * This class represents the object in charge of performing reevaluation.
     * <p>
     * The thread waits for notifications coming from PIPs, when it
     * receives a notification, it starts reevaluating all the sessions that are
     * interested in that attribute.
     * For this reason this thread will have to:
     * <ol type="i">
     * <li>Retrieve all the sessions that are interested into that attribute. If
     * the attribute contains any additional information (e.g. the name of the
     * subject) obviously check if the additional information stored in the
     * policy is the same.</li>
     * </ol><br>
     * </p>
     * @author Antonio La Marra, Alessandro Rosetti
     *
     */
    private final class AttributeMonitor implements Runnable {

        @Override
        public void run() {
            log.info( "Attribute monitor started" );
            while( monitoringRunning ) {
                try {
                    PipChMessage message = changedAttributesQueue.take();
                    List<Attribute> attributes = message.getAttributes();

                    if( attributes == null ) {
                        log.warning( "Attributes list in the message is null" );
                        continue;
                    }

                    if( !manageChanges( attributes ) ) {
                        log.warning( "Unable to handle all the changes" );
                    }
                } catch( InterruptedException e ) {
                    log.severe( e.getMessage() );
                    Thread.currentThread().interrupt();
                }
            }
        }

        private boolean manageChanges( List<Attribute> attributes ) {
            for( Attribute attribute : attributes ) {
                if( !reevaluateSessions( attribute ) ) {
                    return false;
                }
            }
            return true;
        }

        /**
         * This is the function where the effective reevaluation takes place.
         * <p>
         * The reevaluation process is divided in the following steps:
         * <ol>
         * <li>Retrieve all the required informations [attributeID, eventual
         * additional informations] from the json object passed as
         * parameter</li>
         * <li>From these informations extract the list of interested sessions
         * from the sessionmanager</li>
         * <li>Divide the remote sessions, i.e., the sessions that belong to
         * another context handler to the one this context handler is in charge
         * of reevaluating</li>
         * <ul>
         * <li>Notify all the other context handlers of the changing just
         * occurred</li>
         * <li>Reevaluate the sessions assigned to this context handler</li>
         * </ul>
         * </ol>
         * </p>
         *
         * @param jsonObject
         *            the jsonObject inserted by the PIP which attribute has
         *            changed
         * @return true if everything goes ok, false if some exception occurs
         */
        private boolean reevaluateSessions( Attribute attribute ) {
            try {
                log.info( "reevaluateSessions for  attributeId : " + attribute.getAttributeId() );
                List<SessionInterface> sessionList = getSessionListForCategory( attribute.getCategory(), attribute.getAttributeId(),
                    attribute.getAdditionalInformations() );
                if( sessionList != null ) {
                    for( SessionInterface session : sessionList ) {
                        reevaluateSession( session );
                    }
                }
                return true;
            } catch( Exception e ) {
                log.severe( "Error in Reevaluate sessions : " + e.getMessage() );
            }
            return false;
        }

        /**
         * Reevaluates the request related to the session which attribute has
         * changed.
         * <p>
         * Basically from each session it retrieves the original request and
         * then asks to the PIPs to fill it with their attributes. Once the
         * request is completed then it evaluates it in order to see which is
         * the answer of the PDP.
         * </p>
         * After reevaluation of "local" sessions eventually notify the PEP of
         * the changing happened, basically if the Request is not compliant with
         * the policy anymore.
         *
         * @param session
         *            the session to be reevaluated
         */
        private void reevaluateSession( SessionInterface session ) {
            try {
                log.log( Level.INFO, "Reevaluation begins at {0}", System.currentTimeMillis() );
                ReevaluationMessage reevaluationMessage = new ReevaluationMessage( uri.getHost(), uri.getHost() );
                reevaluationMessage.setSession( session );
                reevaluate( reevaluationMessage );
                log.log( Level.INFO, "Reevaluation ends at {0}", System.currentTimeMillis() );
            } catch( Exception e ) {
                log.severe( "Error in PIP retrieval : " + e.getMessage() );
            }
        }
    }

    @Override
    public synchronized void reevaluate( ReevaluationMessage message ) {
        SessionInterface session = message.getSession(); // TODO check
        PolicyWrapper policy = PolicyWrapper.build( session.getPolicySet() );
        RequestWrapper request = RequestWrapper.build( session.getOriginalRequest() );
        RequestWrapper fatRequest = fattenRequest( request, policy, STATUS.STARTACCESS );

        PDPEvaluation evaluation = getPdp().evaluate( fatRequest.getRequest(), policy.getPolicy( POLICY_CONDITION.STARTACCESS ) );

        getObligationManager().translateObligations( evaluation, message.getSession().getId(),
            ContextHandlerConstants.START_STATUS );

        log.log( Level.INFO, "Reevaluate evaluated at {0} pdp response : {1}",
            new Object[] { System.currentTimeMillis(), evaluation.getResult() } );

        evaluation.setSessionId( session.getId() );
        ReevaluationResponse response = buildReevaluationResponse( evaluation, session.getPEPUri() );

        if( session.getStatus().equals( ContextHandlerConstants.START_STATUS )
                && evaluation.getResult().contains( DecisionType.DENY.value() ) ) {
            log.log( Level.INFO, "Sending revoke {0}", System.currentTimeMillis() );
            getSessionManager().updateEntry( session.getId(), ContextHandlerConstants.REVOKE_STATUS );
            getRequestManager().sendMessageToOutside( response );
        }

        if( session.getStatus().equals( ContextHandlerConstants.REVOKE_STATUS )
                && evaluation.getResult().contains( DecisionType.PERMIT.value() ) ) {
            log.log( Level.INFO, "Sending resume {0}", System.currentTimeMillis() );
            getSessionManager().updateEntry( session.getId(), ContextHandlerConstants.START_STATUS );
            getRequestManager().sendMessageToOutside( response );
        }
    }

    private ReevaluationResponse buildReevaluationResponse( PDPEvaluation evaluation, String pepUri ) {
        String destination;
        destination = pepUri.split( PEP_ID_SEPARATOR )[0];
        ReevaluationResponse response = new ReevaluationResponse( uri.getHost(), destination );
        String[] uriSplitted = pepUri.split( PEP_ID_SEPARATOR );
        response.setPepId( uriSplitted[uriSplitted.length - 1] );
        response.setPDPEvaluation( evaluation );
        return response;
    }
}
