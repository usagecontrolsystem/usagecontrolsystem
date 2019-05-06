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
package it.cnr.iit.usagecontrolframework.proxies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.properties.components.SessionManagerProperties;
import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * This is the proxy to be used to communicate with the session manager.
 * <p>
 * The session manager is basically a database which can be implemented in
 * various forms:
 * <ol>
 * <li>Through an SQL database: in this case the SessionManager is not
 * distributed and it is local to the UCS</li>
 * <li>Through a NoSQL database: in this case the SessionManager is distributed
 * and</li>
 * <li>Through SOCKET: in this case the SessionManager is not in the same JVM of
 * the ContextHandler but offers a socket through which it can receive and send
 * messages</li>
 * <li>Through REST API: in this case the SessionManager is not in the same JVM
 * of the ContextHandler but offers however REST_APi to deal with it</li>
 * </ol>
 * The first two cases are indistinguishable also form the PROXY perspective, it
 * just knows that to deal with the session manager it can use the api.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class ProxySessionManager extends Proxy implements SessionManagerInterface {

    private static final Logger log = Logger.getLogger( ProxySessionManager.class.getName() );

    private SessionManagerProperties properties;
    private SessionManagerInterface sessionManagerInterface;

    private volatile boolean started = false;
    private volatile boolean initialized = false;

    public ProxySessionManager( SessionManagerProperties properties ) {
        Reject.ifNull( properties );
        Reject.ifNull( properties.getCommunicationType() );

        this.properties = properties;

        switch( getConnection() ) {
            case API:
                if( buildLocalSessionManager( properties ) ) {
                    initialized = true;
                }
                break;
            case SOCKET:
            case REST_API:
                log.log( Level.WARNING, CONNECTION.MSG_ERR_UNIMPLEMENTED, properties.getCommunicationType() );
                break;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
                return;
        }
    }

    /**
     * In this case the SessionManager is implemented as a local database. This
     * database can be distributed or not, we don't care, the only thing we care
     * about is that in this case we can use the API of the SessionManager to deal
     * with it.
     *
     * @param properties
     * @return
     */
    private boolean buildLocalSessionManager( SessionManagerProperties properties ) {
        Reject.ifBlank( properties.getClassName() );
        try {
            // TODO UCS-32 NOSONAR
            Constructor<?> constructor = Class.forName( properties.getClassName() )
                .getConstructor( SessionManagerProperties.class );
            sessionManagerInterface = (SessionManagerInterface) constructor
                .newInstance( properties );
            return true;
        } catch( InstantiationException | IllegalAccessException
                | ClassNotFoundException | NoSuchMethodException | SecurityException
                | IllegalArgumentException | InvocationTargetException e ) {
            log.severe( e.getMessage() );
        }
        return false;
    }

    @Override
    public Boolean start() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                started = sessionManagerInterface.start();
                return started;
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
                return false;
        }
    }

    @Override
    public Boolean stop() {
        // BEGIN parameter checking
        if( initialized == false ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                started = !sessionManagerInterface.stop();
                return !started;
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                log.log( Level.SEVERE, CONNECTION.MSG_ERR_INCORRECT, properties.getCommunicationType() );
                return false;
        }
    }

    @Override
    public Boolean createEntryForSubject( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForSubject,
            String status, String pepURI, String myIP, String subjectName ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.createEntryForSubject( sessionId,
                    policySet, originalRequest, onGoingAttributesForSubject, status,
                    pepURI, myIP, subjectName );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.createEntryForSubject( sessionId,
                    policySet, originalRequest, onGoingAttributesForSubject, status,
                    pepURI, myIP, subjectName );
        }
    }

    @Override
    public Boolean createEntryForResource( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForObject,
            String status, String pepURI, String myIP, String objectName ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.createEntryForResource( sessionId,
                    policySet, originalRequest, onGoingAttributesForObject, status,
                    pepURI, myIP, objectName );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                log.severe( "Incorrect communication medium : " + properties.getCommunicationType() );
                return false;
        }
    }

    @Override
    public Boolean createEntry( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForSubject,
            List<String> onGoingAttributesForObject,
            List<String> onGoingAttributesForAction,
            List<String> onGoingAttributesForEnvironment, String status,
            String pepURI, String myIP, String subjectName, String objectName,
            String actionName ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.createEntry( sessionId, policySet,
                    originalRequest, onGoingAttributesForSubject,
                    onGoingAttributesForObject, onGoingAttributesForAction,
                    onGoingAttributesForEnvironment, status, pepURI, myIP, subjectName,
                    objectName, actionName );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.createEntry( sessionId, policySet,
                    originalRequest, onGoingAttributesForSubject,
                    onGoingAttributesForObject, onGoingAttributesForAction,
                    onGoingAttributesForEnvironment, status, pepURI, myIP, subjectName,
                    objectName, actionName );
        }
    }

    @Override
    public Boolean createEntryForAction( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForAction,
            String status, String pepURI, String myIP, String actionName ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.createEntryForAction( sessionId,
                    policySet, originalRequest, onGoingAttributesForAction, status,
                    pepURI, myIP, actionName );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.createEntryForAction( sessionId,
                    policySet, originalRequest, onGoingAttributesForAction, status,
                    pepURI, myIP, actionName );
        }
    }

    @Override
    public Boolean createEntryForEnvironment( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForEnvironment,
            String status, String pepURI, String myIP ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.createEntryForEnvironment( sessionId,
                    policySet, originalRequest, onGoingAttributesForEnvironment, status,
                    pepURI, myIP );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.createEntryForEnvironment( sessionId,
                    policySet, originalRequest, onGoingAttributesForEnvironment, status,
                    pepURI, myIP );
        }
    }

    @Override
    public Boolean updateEntry( String sessionId, String status ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.updateEntry( sessionId, status );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.updateEntry( sessionId, status );
        }
    }

    @Override
    public Boolean deleteEntry( String sessionId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.deleteEntry( sessionId );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.deleteEntry( sessionId );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForAttribute( String attributeId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.getSessionsForAttribute( attributeId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface.getSessionsForAttribute( attributeId );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForSubjectAttributes(
            String subjectName, String attributeId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface
                    .getSessionsForSubjectAttributes( subjectName, attributeId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface
                    .getSessionsForSubjectAttributes( subjectName, attributeId );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForResourceAttributes(
            String objectName, String attributeId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface
                    .getSessionsForResourceAttributes( objectName, attributeId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface
                    .getSessionsForResourceAttributes( objectName, attributeId );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForActionAttributes(
            String actionName, String attributeId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface
                    .getSessionsForActionAttributes( actionName, attributeId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface
                    .getSessionsForActionAttributes( actionName, attributeId );
        }
    }

    @Override
    public Optional<SessionInterface> getSessionForId( String sessionId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return Optional.empty();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.getSessionForId( sessionId );
            case SOCKET:
                return Optional.empty();
            case REST_API:
                return Optional.empty();
            default:
                return sessionManagerInterface.getSessionForId( sessionId );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForStatus( String status ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.getSessionsForStatus( status );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface.getSessionsForStatus( status );
        }
    }

    @Override
    public List<SessionInterface> getSessionsForEnvironmentAttributes(
            String attributeId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface
                    .getSessionsForEnvironmentAttributes( attributeId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface
                    .getSessionsForEnvironmentAttributes( attributeId );
        }
    }

    @Override
    public List<OnGoingAttributesInterface> getOnGoingAttributes( String sessionId ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return new ArrayList<>();
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.getOnGoingAttributes( sessionId );
            case SOCKET:
                // TODO
                return new ArrayList<>();
            case REST_API:
                // TODO
                return new ArrayList<>();
            default:
                return sessionManagerInterface.getOnGoingAttributes( sessionId );
        }
    }

    @Override
    public STATUS checkSession( String sessionId, Attribute attribute ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return null;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.checkSession( sessionId, attribute );
            case SOCKET:
                // TODO
                return null;
            case REST_API:
                // TODO
                return null;
            default:
                return sessionManagerInterface.checkSession( sessionId, attribute );
        }
    }

    @Override
    public boolean insertSession( SessionInterface session, Attribute attribute ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.insertSession( session, attribute );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.insertSession( session, attribute );
        }
    }

    @Override
    public boolean stopSession( SessionInterface session ) {
        // BEGIN parameter checking
        if( !initialized || !started ) {
            return false;
        }
        // END parameter checking

        switch( getConnection() ) {
            case API:
                return sessionManagerInterface.stopSession( session );
            case SOCKET:
                // TODO
                return false;
            case REST_API:
                // TODO
                return false;
            default:
                return sessionManagerInterface.stopSession( session );
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    protected CONNECTION getConnection() {
        return CONNECTION.valueOf( properties.getCommunicationType() );
    }

}
