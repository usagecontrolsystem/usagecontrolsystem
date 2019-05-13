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

import javax.xml.bind.JAXBException;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.exceptions.RevokeException;
import it.cnr.iit.ucs.exceptions.SessionManagerException;
import it.cnr.iit.ucs.exceptions.WrongOrderException;
import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants;
import it.cnr.iit.ucsinterface.message.Message;
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
import it.cnr.iit.ucsinterface.sessionmanager.CreateEntryParameterBuilder;
import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.policy.PolicyHelper;

import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the class implementing a context-handler with low concurrency.
 * <p>
 * We will provide some different implementations of the context handler, so
 * that the user can pick up the most suitable to its case. This implementation
 * of the context handler works exactly in the same way of the first release.
 * The only difference here is how the changing of the value of an attribute is
 * managed: basically the context handler spawns a thread in charge of
 * monitoring eventual changes in the value of the attributes. This thread stays
 * in a sleeping state unless it is waken up by the calling of a proper function
 * performed by the PIP.
 * </p>
 * <p>
 * This implementation of the context handler can handle a single request per
 * time (as it was for the previous implementation). Hence here we do not have
 * to parse the configuration, because this implementation can handle only a
 * single thread. This single thread is represented by the AttributeMonitor
 * actor which implements the Callable<String> interface. We have chosen this
 * approach because we might be interested in having something to signal us the
 * result of reevaluation. This context handler has as additional parameter a
 * blocking queue that will be used to put the notification received by the
 * various PIPs, since once a notification has been received, all the PIPs will
 * be queried, then this queue MUST contain, unless something changes in the
 * architecture a single element only. Since we may have also remote attributes,
 * that queue may become very big because we don't know how many remote
 * attributes we may need to monitor, in that case the queue will be managed
 * like an hash function where the key will be the session id, so that, for each
 * session, it is possible to have a single attribute that notifies the changes.
 * <br>
 * </p>
 *
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class ContextHandlerLC extends AbstractContextHandler {

    private static final Logger log = Logger.getLogger( ContextHandlerLC.class.getName() );

    private static final String TRYACCESS_POLICY = "pre";
    private static final String STARTACCESS_POLICY = "ongoing";
    private static final String ENDACCESS_POLICY = "post";
    // this is the string that, in an URI separates the PEP from the node address
    public static final String PEP_ID_SEPARATOR = "#";

    // monitors if the value of an attribute changes
    private AttributeMonitor attributeMonitor = new AttributeMonitor();
    // queue in charge of storing the changing in the attributes
    private LinkedTransferQueue<PipChMessage> attributesChanged = new LinkedTransferQueue<>();
    // the thread object in charge of performing reevaluation
    private Thread thread = new Thread( attributeMonitor );
    // boolean variable that states if the thread has to run again or not
    private volatile boolean continueMonitoring = true;

    public ContextHandlerLC( ContextHandlerProperties chProperties ) {
        super( chProperties );
    }

    /**
     * starts the thread in charge of monitoring the changes notified by PIPs
     */
    @Override
    public boolean startMonitoringThread() {
        if( isInitialized() ) {
            thread.start();
            return true;
        }
        return false;
    }

    /**
     * stop the thread in charge of monitoring the changes notified by PIPs
     */
    public void stopMonitoringThread() {
        continueMonitoring = false;
    }

    /**
     * tryaccess method invoked by PEP<br>
     * The following actions are performed:
     * <ol>
     * <li>policy set is extracted from the received one</li>
     * <li>ongoing attributes are retrieved from the policy ongoing</li>
     * <li>subject id and object id, for the received request, are retrieved
     * from the request itself</li>
     * <li>retrieve method of each PIP is invoked</li>
     * <li>request evaluation</li>
     * <li>PDP response is checked</li>
     * </ol>
     *
     * @param message
     *            the message received by the PEP
     *
     */
    @Override
    // TODO use TryAccessMessage(review message classes family) directly as a parameter
    public void tryAccess( Message message ) {
        if( !isInitialized() || message == null ) {
            log.log( Level.SEVERE, "{0} {1} \t {2}",
                new Object[] { "INVALID tryAccess ", isInitialized(), ( message == null ) } );
            throw new IllegalStateException( "Error in tryAccess: " + isInitialized() + "\t" + ( message == null ) );
        }

        log.log( Level.INFO, "[TIME] tryaccess received at {0}", new Object[] { System.currentTimeMillis() } );

        TryAccessMessage tryAccess = (TryAccessMessage) message;

        PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper( tryAccess.getPolicy() );

        // eventual scheduling
        List<Attribute> attributes = policyHelper.getAttributesForCondition( TRYACCESS_POLICY );
        log.log( Level.INFO, "[TIME] tryaccess begin scheduling at {0}", new Object[] { System.currentTimeMillis() } );

        String sessionId = createSessionId();
        String policy = retrievePolicy( tryAccess );
        String request = tryAccess.getRequest();
        // make the request complete before reevaluation
        String requestFull = makeRequestFull( request, attributes, STATUS.TRYACCESS, true );

        log.info( requestFull );
        log.info( "-------------" );
        log.info( policy );

        // perform the evaluation
        StringBuilder policyBuilder = new StringBuilder();
        PDPEvaluation pdpEvaluation = getPdpInterface().evaluate( requestFull, policyBuilder.append( policy ),
            STATUS.TRYACCESS );

        // policy = policyBuilder.toString();

        String pdpResponse = pdpEvaluation.getResult();
        log.log( Level.INFO, "[TIME] tryaccess evaluated at {0} response: {1}", new Object[] { System.currentTimeMillis(), pdpResponse } );

        // if access decision is PERMIT - update SM DB entry
        if( pdpResponse.equalsIgnoreCase( DecisionType.PERMIT.value() ) ) {
            /**
             * If tryAccess was scheduled, then the ip to be stored is the one
             * of the node that has the PEP attached, otherwise it is the URL of
             * this node
             */
            insertInSessionManager( sessionId, policy, request, ContextHandlerConstants.TRY_STATUS,
                tryAccess.isScheduled() ? tryAccess.getPepUri()
                        : uri.getHost() + PEP_ID_SEPARATOR + tryAccess.getSource(),
                policyHelper, tryAccess.isScheduled() ? tryAccess.getSource() : uri.getHost() );

            log.log( Level.INFO, "[TIME] permit tryAccess ends at {0}", new Object[] { System.currentTimeMillis() } );

        }
        getObligationManager().translateObligations( pdpEvaluation, sessionId, ContextHandlerConstants.TRY_STATUS );

        TryAccessResponse tryAccessResponse = new TryAccessResponse( uri.getHost(), tryAccess.getSource(), message.getMessageId() );
        tryAccessResponse.setSessionId( sessionId );
        tryAccessResponse.setPDPEvaluation( pdpEvaluation );
        if( tryAccess.isScheduled() ) {
            tryAccessResponse.setUCSDestination();
        }
        getRequestManagerToChInterface().sendMessageToOutside( tryAccessResponse );
    }

    /**
     * Attempt to make the request full in order to let the PDP evaluate
     * correctly the request.
     *
     * @param request
     *            the request in string format
     * @param attributes
     *            the list of attributes required to evaluate the request
     * @param complete
     *            states if we need to add all the attributes (if true) or only
     *            the local ones, if false
     * @return a String that represents the request itself
     */
    private synchronized String makeRequestFull( String request, List<Attribute> attributes, STATUS status,
            boolean complete ) {
        try {
            RequestType requestType = JAXBUtility.unmarshalToObject( RequestType.class, request );
            // handles all the cases except startaccess
            if( status == STATUS.TRYACCESS || status == STATUS.ENDACCESS || status == STATUS.REVOKE ) {
                requestType = makeRequestFull( requestType, attributes, complete, false );
            }

            if( status == STATUS.STARTACCESS ) {
                requestType = makeRequestFull( requestType, attributes, complete, true );
            }
            return JAXBUtility.marshalToString( RequestType.class, requestType, "Request",
                JAXBUtility.SCHEMA );
        } catch( JAXBException exception ) {
            log.severe( exception.getMessage() );
            return "";
        }
    }

    private RequestType makeRequestFull( RequestType requestType, List<Attribute> attributes,
            boolean complete, boolean isSubscription ) {
        List<Attribute> external = extractExternal( attributes, requestType );

        if( !isSubscription ) {
            pipRegistry.retrieveAll( requestType );
        } else {
            pipRegistry.subscribeAll( requestType );
        }

        if( !complete || !external.isEmpty() ) {
            log.warning( "Policy requires attributes that are not accessible!!" );
            return null;
        }
        return requestType;
    }

    /**
     * From the list of attributes required by the actual policy, extract the
     * ones related to external UCS. At first we try to see if the attribute is
     * managed by one of the PIPs local to the ContextHandler, then,
     * if we cannot find the PIP among the local ones, we check to see if the
     * attribute is already in the request, otherwise the attribute has to be
     * retrieved from a remote PIP.
     *
     * @param attributes
     *            the list of attributes
     * @return the list of attributes without those attributes that can be
     *         retrieved by internal PIPs
     */
    private List<Attribute> extractExternal( List<Attribute> attributes, RequestType request ) {
        List<Attribute> externalAttributes = new ArrayList<>();
        boolean found = false;
        for( Attribute attribute : attributes ) {
            attribute.getAttributeValueMap().clear();
            found = pipRegistry.hasAttribute( attribute ) || findInRequest( attribute, request );
            if( !found ) {
                externalAttributes.add( attribute );
            }
        }
        return externalAttributes;
    }

    private boolean findInRequest( Attribute attribute, RequestType request ) {
        for( AttributesType attributeType : request.getAttributes() ) {
            for( AttributeType att : attributeType.getAttribute() ) {
                if( attribute.getAttributeId().equals( att.getAttributeId() ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * It creates a new simple session id
     *
     * @return session id to associate to the incoming session during the tryaccess
     */
    private synchronized String createSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Retrieves the policy to be used to evaluate the request in string format
     *
     * @param tryAccess
     *            the message received by the context handler
     * @return the string representing the policy
     */
    private String retrievePolicy( TryAccessMessage tryAccess ) {
        String policy = tryAccess.getPolicy();
        if( policy == null ) {
            policy = getPapInterface().retrievePolicy( tryAccess.getPolicyId() );
            if( policy == null ) {
                log.warning( "UNABLE to RETRIEVE the POLICY" );
                throw new IllegalArgumentException( "No policy found with Id: " + tryAccess.getPolicyId() );
            }
        }
        return policy;
    }

    /**
     * this function inserts inside the session manager a new session with its
     * relative on_going_attributes.
     *
     * @param sessionId
     *            the session id
     * @param uxacmlPol
     *            the uxacml policy
     * @param originalRequest
     *            the original request, not the fat one because, whenever we
     *            need to re-evaluate the request we will retrieval from the
     *            various PIPs a fresh value
     * @param status
     *            status of the request
     * @param pepUri
     *            the URI of the PEP in order to allow communication between the
     *            PEP and the context handler
     * @param policyHelper
     *            object representing the policy to be used in the various
     *            evaluations the subject id
     */
    private void insertInSessionManager( String sessionId, String uxacmlPol, String request, final String status,
            String pepUri, PolicyHelper policyHelper, String ip ) {
        try {
            RequestType requestType = JAXBUtility.unmarshalToObject( RequestType.class, request );

            // retrieve the id of ongoing attributes
            CreateEntryParameterBuilder createEntryParameterBuilder = new CreateEntryParameterBuilder();

            List<Attribute> onGoingAttributes = policyHelper.getAttributesForCondition( STARTACCESS_POLICY );
            createEntryParameterBuilder.setOnGoingAttributesForSubject( getAttributesForCategory( onGoingAttributes, Category.SUBJECT ) )
                .setOnGoingAttributesForAction( getAttributesForCategory( onGoingAttributes, Category.ACTION ) )
                .setOnGoingAttributesForResource( getAttributesForCategory( onGoingAttributes, Category.RESOURCE ) )
                .setOnGoingAttributesForEnvironment( getAttributesForCategory( onGoingAttributes, Category.ENVIRONMENT ) );

            createEntryParameterBuilder.setSubjectName( requestType.extractValue( Category.SUBJECT ) )
                .setResourceName( requestType.extractValue( Category.RESOURCE ) )
                .setActionName( requestType.extractValue( Category.ACTION ) );

            createEntryParameterBuilder.setSessionId( sessionId ).setPolicySet( uxacmlPol ).setOriginalRequest( request )
                .setStatus( status ).setPepURI( pepUri ).setMyIP( ip );

            // retrieve the values of attributes in the request

            // insert all the values inside the session manager
            if( !getSessionManagerInterface().createEntry( createEntryParameterBuilder.build() ) ) {
                log.log( Level.SEVERE, "[Context Handler] TryAccess: some error occurred, session {0} has not been stored correctly",
                    sessionId );
            }
        } catch( Exception e ) {
            log.severe( e.getMessage() );
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
     * startaccess method invoked by PEP<br>
     * The following actions are performed:
     * <ol>
     * <li>the session that should be reevaluated is retrieved through the
     * session manager</li>
     * <li>subscribe method of each PIP is invoked</li>
     * <li>request evaluation</li>
     * <li>PDP response is checked</li>
     * <li>in case of "deny" response, the considered session is revoked and the
     * revoke method is invoked</li>
     * </ol>
     *
     * @param sId
     *            : session id of the involved session
     * @return generic string which represents PDP response
     * @throws WrongOrderException
     * @throws SessionManagerException
     * @throws RevokeException
     *
     */
    @Override
    public void startAccess( Message message ) throws Exception {
        // BEGIN parameter checking
        if( !isInitialized() || message == null || !( message instanceof StartAccessMessage ) ) {
            log.log( Level.SEVERE, "Invalid startaccess {0} \t {1}",
                new Object[] { isInitialized(), ( message instanceof StartAccessMessage ) } );
            throw new IllegalStateException( "Invalid startaccess" );
        }
        // END parameter checking
        log.log( Level.INFO, "[TIME] startaccess begins at {0}", new Object[] { System.currentTimeMillis() } );

        StartAccessMessage startAccessMessage = (StartAccessMessage) message;
        String sessionId = startAccessMessage.getSessionId();

        Optional<SessionInterface> optional = getSessionManagerInterface().getSessionForId( sessionId );

        if( !optional.isPresent() ) {
            // throw exception here
            return;
        }
        SessionInterface sessionToReevaluate = optional.get();

        PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper( sessionToReevaluate.getPolicySet() );

        List<Attribute> attributes = policyHelper.getAttributesForCondition( STARTACCESS_POLICY );
        log.log( Level.INFO, "[TIME] startaccess begin scheduling at {0}", new Object[] { System.currentTimeMillis() } );

        StartAccessResponse response = new StartAccessResponse( startAccessMessage.getDestination(),
            startAccessMessage.getSource(), message.getMessageId() );

        // check if there actually is a request to reevaluate for the received
        // session id
        if( !sessionToReevaluate.getStatus().equals( ContextHandlerConstants.TRY_STATUS ) ) {
            // no request to reevaluate(some problem occurred during request and
            // policy retrieving)
            log.log( Level.SEVERE, "startaccess: tryaccess must be performed for session {0}", sessionId );

            throw new WrongOrderException(
                "[Context Handler] Startaccess: tryaccess must be performed yet for session " + sessionId );
        }

        String request = sessionToReevaluate.getOriginalRequest();

        // make the request complete before reevaluation
        String requestFull = makeRequestFull( request, policyHelper.getAttributesForCondition( STARTACCESS_POLICY ),
            STATUS.STARTACCESS, true );

        // perform the evaluation
        PDPEvaluation pdpEvaluation = getPdpInterface().evaluate( requestFull,
            policyHelper.getConditionForEvaluation( STARTACCESS_POLICY ) );

        log.log( Level.INFO, "[TIME] startaccess ends at {0}", new Object[] { System.currentTimeMillis() } );

        response.setPDPEvaluation( pdpEvaluation );

        // PDP returns PERMIT
        if( pdpEvaluation.getResult().equalsIgnoreCase( DecisionType.PERMIT.value() ) ) {

            // obligation
            getObligationManager().translateObligations( pdpEvaluation, sessionId, ContextHandlerConstants.START_STATUS );

            // update session status
            if( !getSessionManagerInterface().updateEntry( sessionId, ContextHandlerConstants.START_STATUS ) ) {
                log.log( Level.WARNING, "[TIME] startaccess session {0} status not updated", sessionId );
            }
            log.log( Level.INFO, "[TIME] PERMIT startaccess ends at {0}", new Object[] { System.currentTimeMillis() } );
        } else {
            getObligationManager().translateObligations( pdpEvaluation, sessionId, ContextHandlerConstants.START_STATUS );

            if( revoke( sessionToReevaluate, attributes ) ) {
                log.log( Level.INFO, "[TIME] access revocation for session {0}", sessionId );

                // delete db entry for session sId
                if( !getSessionManagerInterface().deleteEntry( sessionId ) ) {
                    log.log( Level.SEVERE, "Startaccess: some problem occurred during entry deletion for session {0}", sessionId );
                    throw new SessionManagerException(
                        "[Context Handler] Startaccess: Some problem occurred during entry deletion for session "
                                + sessionId );
                }
            }

            log.log( Level.SEVERE,
                "[Context Handler] Startaccess: Some problem occurred during execution of revokaccess for session {0}", sessionId );
            throw new RevokeException(
                "[Context Handler] Startaccess: Some problem occurred during execution of revokaccess for session "
                        + sessionId );
        }
        if( startAccessMessage.isScheduled() ) {
            response.setUCSDestination();
        }
        getRequestManagerToChInterface().sendMessageToOutside( response );
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

        log.log( Level.INFO, "[TIME] revoke begins at {0}", System.currentTimeMillis() );

        boolean otherSessions = true;

        otherSessions = attributesToUnsubscribe( session.getId(), (ArrayList<Attribute>) attributes );

        if( !otherSessions ) {
            pipRegistry.unsubscribeAll( attributes );
        }

        // database entry for the current must be deleted
        try {
            if( !getSessionManagerInterface().deleteEntry( session.getId() ) ) {
                log.log( Level.SEVERE,
                    "[Context Handler] Endaccess: Some problem occurred during entry deletion for session {0}",
                    session.getId() );
                throw new SessionManagerException(
                    "[Context Handler] Endaccess: Some problem occurred during entry deletion for session "
                            + session.getId() );
            }
        } catch( SessionManagerException sme ) {
            log.severe( sme.getMessage() );
            return false;
        }

        log.log( Level.INFO, "[TIME] revoke ends at {0}", new Object[] { System.currentTimeMillis() } );

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
     *            the json object to be filled by this function
     * @return true if threre are attributes to unsubscribe, false otherwise
     *         <br>
     *
     */
    private boolean attributesToUnsubscribe( String sessionId, ArrayList<Attribute> attributes ) {
        String subjectName = "";
        String resourceName = "";
        String actionName = "";
        boolean otherSessions = true;
        // retrieve on going attributes for both subject and object
        Collection<OnGoingAttributesInterface> onGoingAttributes = getSessionManagerInterface().getOnGoingAttributes( sessionId );
        List<OnGoingAttributesInterface> onGoingAttributesForSubject = new LinkedList<>();
        List<OnGoingAttributesInterface> onGoingAttributesForResource = new LinkedList<>();
        List<OnGoingAttributesInterface> onGoingAttributesForAction = new LinkedList<>();
        List<OnGoingAttributesInterface> onGoingAttributesForEnvironment = new LinkedList<>();

        // build attribute lists for subject, resource, action and environment
        if( onGoingAttributes != null && !onGoingAttributes.isEmpty() ) {
            // fill the correspondent list of ongoingattributes
            for( OnGoingAttributesInterface attribute : onGoingAttributes ) {
                if( attribute.getSubjectName() != null && !attribute.getSubjectName().equals( "null" ) ) {
                    onGoingAttributesForSubject.add( attribute );
                    subjectName = attribute.getSubjectName();
                } else if( attribute.getResourceName() != null && !attribute.getResourceName().equals( "null" ) ) {
                    onGoingAttributesForResource.add( attribute );
                    resourceName = attribute.getResourceName();
                } else if( attribute.getActionName() != null && !attribute.getActionName().equals( "null" ) ) {
                    onGoingAttributesForAction.add( attribute );
                    actionName = attribute.getActionName();
                } else {
                    onGoingAttributesForEnvironment.add( attribute );
                }
            }
        }

        // builds up the JSON object that is needed to perform unsubscribe
        if( onGoingAttributes != null && !onGoingAttributes.isEmpty() ) {
            // ongoingattributes for object
            otherSessions = buildOnGoingAttributes( attributes, resourceName, otherSessions, onGoingAttributesForResource );

            // verify what subject attributes must be unsubscribed
            otherSessions = verifyAttributesToUnsubscribe( attributes, subjectName, otherSessions, onGoingAttributesForSubject );

            // on going attributes for action
            otherSessions = buildOnGoingAttributesForAction( attributes, actionName, otherSessions, onGoingAttributesForAction );

            // on going attributes for environment
            otherSessions = buildOmGoingAttributesForEnvironment( attributes, otherSessions, onGoingAttributesForEnvironment );
        }
        return otherSessions;
    }

    private boolean buildOmGoingAttributesForEnvironment( ArrayList<Attribute> attributes, boolean otherSessions,
            List<OnGoingAttributesInterface> onGoingAttributesForEnvironment ) {
        for( OnGoingAttributesInterface attribute : onGoingAttributesForEnvironment ) {
            List<SessionInterface> tempList = getSessionManagerInterface()
                .getSessionsForEnvironmentAttributes( attribute.getAttributeId() );
            if( tempList == null || tempList.isEmpty() || tempList.size() == 1 ) {
                otherSessions = false;
                Attribute tmpAttribute = new Attribute();
                tmpAttribute.createAttributeId( attribute.getAttributeId() );
                attributes.add( tmpAttribute );
            }
        }
        return otherSessions;
    }

    private boolean buildOnGoingAttributesForAction( ArrayList<Attribute> attributes, String actionName, boolean otherSessions,
            List<OnGoingAttributesInterface> onGoingAttributesForAction ) {
        for( OnGoingAttributesInterface attribute : onGoingAttributesForAction ) {
            List<SessionInterface> tempList = getSessionManagerInterface()
                .getSessionsForActionAttributes( actionName, attribute.getAttributeId() );
            if( tempList == null || tempList.isEmpty() || tempList.size() == 1 ) {
                otherSessions = false;
                Attribute tmpAttribute = new Attribute();
                tmpAttribute.createAttributeId( attribute.getAttributeId() );
                tmpAttribute.setAdditionalInformations( actionName );
                attributes.add( tmpAttribute );
            }
        }
        return otherSessions;
    }

    private boolean verifyAttributesToUnsubscribe( ArrayList<Attribute> attributes, String subjectName, boolean otherSessions,
            List<OnGoingAttributesInterface> onGoingAttributesForSubject ) {
        for( OnGoingAttributesInterface attribute : onGoingAttributesForSubject ) {

            // retrieve all the active sessions which deal with the
            // considered on
            // going attribute
            List<SessionInterface> tempList = getSessionManagerInterface()
                .getSessionsForSubjectAttributes( subjectName, attribute.getAttributeId() );
            // check if there are not any active sessions which deal with
            // the
            // attribute
            if( tempList == null || tempList.isEmpty() || tempList.size() == 1 ) {
                otherSessions = false;
                Attribute tmpAttribute = new Attribute();
                tmpAttribute.createAttributeId( attribute.getAttributeId() );
                tmpAttribute.setAdditionalInformations( subjectName );
                attributes.add( tmpAttribute );
            }
        }
        return otherSessions;
    }

    private boolean buildOnGoingAttributes( ArrayList<Attribute> attributes, String resourceName, boolean otherSessions,
            List<OnGoingAttributesInterface> onGoingAttributesForResource ) {
        for( OnGoingAttributesInterface attribute : onGoingAttributesForResource ) {

            // retrieve all the active sessions which deal with the
            // considered on
            // going attribute
            List<SessionInterface> tempList = getSessionManagerInterface()
                .getSessionsForResourceAttributes( resourceName, attribute.getAttributeId() );
            // check if there are not any active sessions which deal with
            // the
            // attribute
            if( tempList == null || tempList.isEmpty() || tempList.size() == 1 ) {
                otherSessions = false;
                Attribute tmpAttribute = new Attribute();
                tmpAttribute.createAttributeId( attribute.getAttributeId() );
                tmpAttribute.setAdditionalInformations( resourceName );
                attributes.add( tmpAttribute );
            }
        }
        return otherSessions;
    }

    @Override
    public void endAccess( Message message ) {
        // BEGIN parameter checking
        if( !isInitialized() ) {
            log.severe( "CH not initialized correctly" );
            return;
        }
        if( !( message instanceof EndAccessMessage ) ) {
            log.severe( "Invalid message in endaccess" );
            return;
        }
        // END parameter checking
        try {
            EndAccessMessage endAccessMessage = (EndAccessMessage) message;
            String sessionId = endAccessMessage.getSessionId();

            log.log( Level.INFO, "[TIME] endaccess begins at {0}", new Object[] { System.currentTimeMillis() } );

            // check if an entry actually exists in db
            Optional<SessionInterface> optional = getSessionManagerInterface().getSessionForId( endAccessMessage.getSessionId() );

            if( !optional.isPresent() ) {
                // throw exception here
                return;
            }
            SessionInterface sessionToReevaluate = optional.get();

            if( ( !sessionToReevaluate.getStatus().equals( ContextHandlerConstants.START_STATUS )
                    && !sessionToReevaluate.getStatus().equals( ContextHandlerConstants.REVOKE_STATUS ) ) ) {
                // no entry exists for the actual session
                log.log( Level.INFO,
                    "[Context Handler] Endaccess: a tryaccess or startaccess must be performed yet for session {0}"
                            + ", or the related endaccess has already been executed",
                    sessionId );
                throw new WrongOrderException(
                    "[Context Handler] Endaccess: a tryaccess must be performed yet for session " + sessionId
                            + ", or the related endaccess has already been executed" );
            }

            PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper( sessionToReevaluate.getPolicySet() );

            log.log( Level.INFO, "[TIME] endaccess scheduler starts at {0}", new Object[] { System.currentTimeMillis() } );
            List<Attribute> attributes = policyHelper.getAttributesForCondition( ENDACCESS_POLICY );

            String request = sessionToReevaluate.getOriginalRequest();

            // make the request complete before reevaluation
            String requestFull = makeRequestFull( request, policyHelper.getAttributesForCondition( ENDACCESS_POLICY ),
                STATUS.ENDACCESS, true );

            PDPEvaluation pdpEvaluation = getPdpInterface().evaluate( requestFull,
                policyHelper.getConditionForEvaluation( ENDACCESS_POLICY ) );

            log.log( Level.INFO, "[TIME] EndAccess evaluation ends at {0}", System.currentTimeMillis() );

            getObligationManager().translateObligations( pdpEvaluation, sessionId, ContextHandlerConstants.END_STATUS );

            EndAccessResponse response = new EndAccessResponse( endAccessMessage.getDestination(),
                endAccessMessage.getSource(), message.getMessageId() );
            response.setPDPEvaluation( pdpEvaluation );

            if( endAccessMessage.isScheduled() ) {
                response.setUCSDestination();
            }

            // access must be revoked
            if( revoke( sessionToReevaluate, attributes ) ) {
                log.log( Level.INFO, "[TIME] endaccess evaluation with revoke ends at {0}", System.currentTimeMillis() );
            }

            getRequestManagerToChInterface().sendMessageToOutside( response );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
    }

    /**
     * API offered by the context handler to the PIP in case some attribute gets
     * changed
     *
     * @param message
     */
    @Override
    public void attributeChanged( Message message ) {
        log.log( Level.INFO, "Attribute changed received {0}", System.currentTimeMillis() );
        if( !( message instanceof PipChMessage ) ) {
            log.warning( "Invalid message provided" );
        }
        // non blocking insertion in the queue of attributes changed
        attributesChanged.put( (PipChMessage) message );
    }

    /**
     * This class represents the object in charge of performing reevaluation.
     * <p>
     * Basically this thread waits for notifications coming from PIPs, when it
     * receives a notification, it starts reevaluating all the sessions that are
     * interested in that attribute. For this reason this thread will have to
     * accomplish the following tasks:
     * <ol type="i">
     * <li>Retrieve all the sessions that are interested into that attribute. If
     * the attribute contains any additional information (e.g. the name of the
     * subject) obviously check if the additional information stored in the
     * policy is the same.</li>
     * <li>For each session: ask the scheduler if it has to be evaluated locally
     * or by a remote site, in the latter send the request with the values of
     * the local attributes to the remote site.</li>
     * </ol>
     * <br>
     * </p>
     * <p>
     * <b>Implemented behavior: </b> <br>
     * This thread waits on the list named attributesChanged for a message
     * coming from a PIP. When this happens the reevaluation process described
     * above can start. Obviously it has to check if the considered session is
     * already being evaluated, for eample the PEP may ask to terminate a
     * session and in the meanwhile an attribute has changed, but the session
     * has to be evaluated only once.
     * </p>
     *
     * @author antonio
     *
     */
    private final class AttributeMonitor implements Runnable {

        @Override
        public void run() {
            log.info( "Attribute monitor started" );
            while( continueMonitoring ) {
                try {
                    PipChMessage message = attributesChanged.take();
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
                log.info( "reevaluateSessions attrId : " + attribute.getAttributeId() );
                List<SessionInterface> interestedSessions = retrieveSessions( attribute );
                if( interestedSessions == null || interestedSessions.isEmpty() ) {
                    log.info( "There are no sessions to be reevaluated" );
                    return true;
                }
                for( SessionInterface session : interestedSessions ) {
                    reevaluateSession( session, attribute );
                }
                return true;
            } catch( Exception e ) {
                log.severe( "Error in Reevaluate sessions : " + e.getMessage() );
            }
            return false;
        }

        /**
         * Given a certain attribute retrieval, extract from it two basics
         * informations: the attributeId and the eventual additional
         * informations in order to retrieve all the sessions related to that
         * particular attribute Id with that particular additionalInformations.
         * We need also the additional informations in order to perform a
         * pre-filtering, in fact it may happen, for example, that during time
         * the role of a person changes, but we don't want to reevaluate all the
         * sessions that use the attribute role, but only those sessions that
         * are interested in that particular person.
         *
         * @param attr
         *            the attribute retrieval object that represents the
         *            attribute that has changed
         * @return the list of sessions interested
         */
        private List<SessionInterface> retrieveSessions( Attribute attr ) {
            String attrId = attr.getAttributeId();
            String attrAddInfo = attr.getAdditionalInformations();

            switch( attr.getCategory() ) {
                case RESOURCE:
                    return getSessionManagerInterface()
                        .getSessionsForResourceAttributes( attrAddInfo, attrId );
                case SUBJECT:
                    return getSessionManagerInterface()
                        .getSessionsForSubjectAttributes( attrAddInfo, attrId );
                case ACTION:
                    return getSessionManagerInterface()
                        .getSessionsForActionAttributes( attrAddInfo, attrId );
                case ENVIRONMENT:
                    return getSessionManagerInterface()
                        .getSessionsForEnvironmentAttributes( attrId );
                default:
                    log.severe( "Invalid attribute passed" );
                    return new ArrayList<>();
            }
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
         * @return a String representing the answer of the PDP.
         *
         */
        private String reevaluateSession( SessionInterface session, Attribute attribute ) {
            try {
                log.log( Level.INFO, "[TIME] reevaluation begins at {0}", System.currentTimeMillis() );

                PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper( session.getPolicySet() );
                if( getSessionManagerInterface().checkSession( session.getId(),
                    null ) != it.cnr.iit.ucsinterface.sessionmanager.ReevaluationTableInterface.STATUS.IN_REEVALUATION ) {
                    getSessionManagerInterface().insertSession( session, attribute );
                } else {
                    log.info( "Session is already under evaluation" );
                    return null;
                }

                policyHelper.getAttributesForCondition( STARTACCESS_POLICY );
                log.log( Level.INFO, "[TIME] reevaluation scheduler starts at {0}", System.currentTimeMillis() );
                ReevaluationMessage reevaluationMessage = new ReevaluationMessage(
                    uri.getHost(),
                    uri.getHost() );
                reevaluationMessage.setSession( session );
                log.log( Level.INFO, "[TIME] reevaluation starts at {0}", System.currentTimeMillis() );
                reevaluate( reevaluationMessage );
                log.log( Level.INFO, "[TIME] reevaluation ends at {0}", System.currentTimeMillis() );
                getSessionManagerInterface().stopSession( session );

            } catch( Exception e ) {
                log.severe( "Error in PIP retrieve " + e.getMessage() );
            }
            return null;
        }
    }

    @Override
    public synchronized void reevaluate( Message message ) {
        // BEGIN parameter checking
        if( !( message instanceof ReevaluationMessage ) ) {
            log.severe( "Invalid message received for reevaluation" );
            return;
        }
        // END parameter checking

        ReevaluationMessage reevaluationMessage = (ReevaluationMessage) message;

        SessionInterface session = reevaluationMessage.getSession();

        String request = reevaluationMessage.getSession().getOriginalRequest();
        PolicyHelper policyHelper = PolicyHelper.buildPolicyHelper( reevaluationMessage.getSession().getPolicySet() );
        // make the request complete before reevaluation
        String requestFull = makeRequestFull( request, policyHelper.getAttributesForCondition( STARTACCESS_POLICY ),
            STATUS.STARTACCESS, true );

        // perform the evaluation
        PDPEvaluation pdpEvaluation = getPdpInterface().evaluate( requestFull,
            policyHelper.getConditionForEvaluation( STARTACCESS_POLICY ) );

        // obligation
        getObligationManager().translateObligations( pdpEvaluation, reevaluationMessage.getSession().getId(),
            ContextHandlerConstants.START_STATUS );

        log.log( Level.INFO, "[TIME] decision {0} taken at {1}", new Object[] { pdpEvaluation.getResult(), System.currentTimeMillis() } );
        String destination;
        String[] uriSplitted = session.getPEPUri().split( PEP_ID_SEPARATOR );
        destination = session.getPEPUri().split( PEP_ID_SEPARATOR )[0];
        log.log( Level.INFO, "DESTINATION: {0}\t{1}", new Object[] { destination, session.getStatus() } );
        ReevaluationResponse chPepMessage = new ReevaluationResponse( uri.getHost(), destination );
        pdpEvaluation.setSessionId( session.getId() );
        chPepMessage.setPDPEvaluation( pdpEvaluation );
        chPepMessage.setPepId( uriSplitted[uriSplitted.length - 1] );
        getSessionManagerInterface().stopSession( session );
        if( ( session.getStatus().equals( ContextHandlerConstants.START_STATUS )
                || session.getStatus().equals( ContextHandlerConstants.TRY_STATUS ) )
                && pdpEvaluation.getResult().contains( DecisionType.DENY.value() ) ) {
            log.log( Level.INFO, "[TIME] Sending revoke {0}", System.currentTimeMillis() );
            getSessionManagerInterface().updateEntry( session.getId(), ContextHandlerConstants.REVOKE_STATUS );
            getRequestManagerToChInterface().sendMessageToOutside( chPepMessage );
        }

        if( session.getStatus().equals( ContextHandlerConstants.REVOKE_STATUS )
                && pdpEvaluation.getResult().contains( DecisionType.PERMIT.value() ) ) {
            log.log( Level.INFO, "[TIME] Sending resume {0}", System.currentTimeMillis() );
            getSessionManagerInterface().updateEntry( session.getId(), ContextHandlerConstants.START_STATUS );
            getRequestManagerToChInterface().sendMessageToOutside( chPepMessage );
        }
    }
}
