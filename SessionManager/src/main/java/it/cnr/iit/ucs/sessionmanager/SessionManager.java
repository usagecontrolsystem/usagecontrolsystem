/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
package it.cnr.iit.ucs.sessionmanager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.google.common.base.Throwables;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import it.cnr.iit.ucs.properties.components.SessionManagerProperties;
import it.cnr.iit.ucs.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucs.sessionmanager.SessionAttributes;
import it.cnr.iit.ucs.sessionmanager.SessionAttributesBuilder;
import it.cnr.iit.ucs.sessionmanager.SessionInterface;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.ucs.sessionmanager.OnGoingAttribute.COLUMN;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * Creates, updates, deletes and retrieves Sessions by waiting for Context
 * Handler Requests. It exploits OrmLite DAL to manage different relational
 * databases.
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra and Alessandro Rosetti
 */
public final class SessionManager implements SessionManagerInterface {

    private static Logger log = Logger.getLogger( SessionManager.class.getName() );

    private static final String MSG_ERR_SQL = "Error in SQL query : {0}";

    // url to connect to the database
    private String databaseURL;
    private ConnectionSource connection;
    // dao to perform operations on the session table
    private Dao<Session, String> sessionDao;
    // dao to perform operations on the attributes table
    private Dao<OnGoingAttribute, String> attributesDao;

    private volatile boolean initialized = false;

    /**
     * Constructor
     *
     * @param databaseURL
     *          URL of the database where data will be stored/retrieved
     */
    protected SessionManager( String databaseURL ) {
        this.databaseURL = databaseURL;
        sessionDao = null;
        attributesDao = null;
    }

    public SessionManager( SessionManagerProperties properties ) {
        Reject.ifNull( properties );
        Reject.ifNull( properties.getDbUri() );
        databaseURL = properties.getDbUri();
        sessionDao = null;
        attributesDao = null;
        initialized = true;
    }

    /**
     * Starts the connection to the database and creates the tables if not exist
     *
     * @return true if Session Manager starts properly, false otherwise
     */
    @Override
    public Boolean start() {
        isInitialized();
        try {
            connection = new JdbcConnectionSource( databaseURL );
            sessionDao = DaoManager.createDao( connection, Session.class );
            attributesDao = DaoManager.createDao( connection, OnGoingAttribute.class );

            TableUtils.createTableIfNotExists( connection, Session.class );
            TableUtils.createTableIfNotExists( connection, OnGoingAttribute.class );
        } catch( SQLException e ) {
            log.severe( e.getMessage() );
            initialized = false;
            throw new IllegalStateException( "SessionManager not in a valid state anymore" );
        }
        return true;
    }

    /**
     * Stops the connection to the database
     *
     * @return true if Session Manager stops properly, false otherwise
     */
    @Override
    public Boolean stop() {
        if( !isInitialized() ) {
            Throwables.propagate( new IllegalStateException( "SessionManager was not correctly initialized" ) );
        }
        try {
            connection.close();
        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            initialized = false;
            throw new IllegalStateException( "SessionManager not in a valid state anymore" );
        }
        return true;
    }

    /**
     * Updates the status of a certain session
     *
     * @param sessionId
     *          session ID
     * @param status
     *          new session status
     * @return true if the new status is properly updated in th DB row, false
     *         otherwise
     */
    @Override
    public Boolean updateEntry( String sessionId, String status ) {
        Reject.ifBlank( status );
        Reject.ifBlank( sessionId );
        try {
            Session s = sessionDao.queryForId( sessionId );
            s.setStatus( status );
            sessionDao.update( s );
        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            return false;
        }
        return true;
    }

    /**
     * Deletes a certain session
     *
     * @param sessionId
     *          ID of the session to be deleted in the DB
     * @return true if the Session is properly deleted in the DB, false otherwise
     */
    @Override
    public Boolean deleteEntry( String sessionId ) {
        validStateAndArguments( sessionId );
        try {
            ForeignCollection<OnGoingAttribute> a = sessionDao.queryForId( sessionId )
                .getOnGoingAttributesAsForeign();
            for( OnGoingAttribute attribute : a ) {
                attributesDao.deleteById( attribute.getId() );
            }
            a.clear();
            sessionDao.deleteById( sessionId );
        } catch( SQLException ex ) {
            return false;
        }
        return true;
    }

    /**
     * Creates an entry for a session that only has attributes related to the
     * subject
     *
     * @param sessionId
     *          the id of the session
     * @param policySet
     *          the policy set used in the evaluation
     * @param originalRequest
     *          the original request
     * @param onGoingAttributesForSubject
     *          the ongoing attributes for the subject
     * @param status
     *          the status of the request
     * @param pepURI
     *          the uri of the pep
     * @param myIP
     *          the ip of the contexthandler that has evaluated the request
     * @param subjectName
     *          the name of the subject to which the attributes are related
     * @return true if everything goes ok, false otherwise
     */
    @Override
    public Boolean createEntryForSubject( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForSubject,
            String status, String pepURI, String myIP, String subjectName ) {
        validStateAndArguments( sessionId, policySet, originalRequest, onGoingAttributesForSubject, status, pepURI, myIP, subjectName );
        return createEntry( new SessionAttributesBuilder().setSessionId( sessionId ).setPolicySet( policySet )
            .setOriginalRequest( originalRequest ).setOnGoingAttributesForSubject( onGoingAttributesForSubject ).setStatus( status )
            .setPepURI( pepURI ).setMyIP( myIP ).setSubjectName( subjectName ).build() );
    }

    /**
     * Creates an entry for a session that only has attributes related to the
     * object
     *
     * @param sessionId
     *          the id of the session
     * @param policySet
     *          the policy set used in the evaluation
     * @param originalRequest
     *          the original request
     * @param onGoingAttributesForResource
     *          the on going attributes for the object
     * @param status
     *          the status of the request
     * @param pepURI
     *          the uri of the pep
     * @param myIP
     *          the ip of the contexthandler that has evaluated the request
     * @param resourceName
     *          the name of the resource to which the attrbiutes are related
     * @return true if everything goes fine, false otherwise
     */
    @Override
    public Boolean createEntryForResource( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForResource,
            String status, String pepURI, String myIP, String resourceName ) {
        validStateAndArguments( sessionId, policySet, originalRequest, onGoingAttributesForResource, status, pepURI, myIP, resourceName );
        return createEntry( new SessionAttributesBuilder().setSessionId( sessionId ).setPolicySet( policySet )
            .setOriginalRequest( originalRequest ).setOnGoingAttributesForResource( onGoingAttributesForResource ).setStatus( status )
            .setPepURI( pepURI ).setMyIP( myIP ).setResourceName( resourceName ).build() );
    }

    /**
     * Creates an entry for a session in which there are on going attributes for
     * the action only
     *
     * @param sessionId
     *          the id of the session
     * @param policySet
     *          the policy set used for evaluation
     * @param originalRequest
     *          the original request
     * @param onGoingAttributesForAction
     *          the on going attributes related to the action
     * @param status
     *          the status of the session
     * @param pepURI
     *          the uri of the pep
     * @param myIP
     *          the ip of the contexthandler that has evaluated the request
     * @param actionName
     *          the name of the action to which the attributes are related
     * @return
     */
    @Override
    public Boolean createEntryForAction( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForAction,
            String status, String pepURI, String myIP, String actionName ) {
        validStateAndArguments( sessionId, status, policySet, originalRequest, onGoingAttributesForAction, pepURI, myIP, actionName );
        return createEntry( new SessionAttributesBuilder().setSessionId( sessionId ).setPolicySet( policySet )
            .setOriginalRequest( originalRequest ).setOnGoingAttributesForAction( onGoingAttributesForAction ).setStatus( status )
            .setPepURI( pepURI ).setMyIP( myIP ).setActionName( actionName ).build() );
    }

    /**
     * Creates an entry for a session in which there are on going attributes for
     * the environment only
     *
     * @param sessionId
     *          the id of the session
     * @param policySet
     *          the policy set used for evaluation
     * @param originalRequest
     *          the original request
     * @param onGoingAttributesForEnvironment
     *          the on going attributes related to the environment
     * @param status
     *          the status of the session
     * @param pepURI
     *          the uri of hte pep
     * @param myIP
     *          the ip of the contexthandler that has evaluated the request
     * @return true if everything goes fine, false otherwise
     */
    @Override
    public Boolean createEntryForEnvironment( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForEnvironment,
            String status, String pepURI, String myIP ) {
        validStateAndArguments( sessionId, policySet, originalRequest, onGoingAttributesForEnvironment, status, pepURI, myIP );
        return createEntry( new SessionAttributesBuilder().setSessionId( sessionId ).setPolicySet( policySet )
            .setOriginalRequest( originalRequest ).setOnGoingAttributesForEnvironment( onGoingAttributesForEnvironment ).setStatus( status )
            .setPepURI( pepURI ).setMyIP( myIP ).build() );
    }

    /**
     * Creates an entry for a session. This is the general function, in this case
     * the request has ongoingattributes related to the subject, the
     * object/resource, the action and the environment
     * @param resourceName
     *          the name of the object to which the attributes are related
     *
     * @return true if everything goes fine, false otherwise
     */
    @Override
    public Boolean createEntry( SessionAttributes parameterObject ) {
        Reject.ifNull( parameterObject );
        try {
            Session s = prepareSession( parameterObject );

            if( sessionDao.idExists( parameterObject.getSessionId() ) ) {
                log.severe( "ID already exists" );
                return false;
            }
            sessionDao.create( s );
            Session sessionResult = sessionDao.queryForId( parameterObject.getSessionId() );
            ForeignCollection<OnGoingAttribute> attributes = sessionResult
                .getOnGoingAttributesAsForeign();
            if( parameterObject.getOnGoingAttributesForSubject() != null ) {
                for( String attr : parameterObject.getOnGoingAttributesForSubject() ) {
                    OnGoingAttribute a = OnGoingAttribute.createOnGoingAttribute( attr, parameterObject.getSubjectName(), COLUMN.SUBJECT );
                    attributes.add( a );
                }
            }
            if( parameterObject.getOnGoingAttributesForResource() != null ) {
                for( String attr : parameterObject.getOnGoingAttributesForResource() ) {
                    OnGoingAttribute a = OnGoingAttribute.createOnGoingAttribute( attr, parameterObject.getResourceName(),
                        COLUMN.RESOURCE );
                    attributes.add( a );
                }
            }
            if( parameterObject.getOnGoingAttributesForAction() != null ) {
                for( String attr : parameterObject.getOnGoingAttributesForAction() ) {
                    OnGoingAttribute a = OnGoingAttribute.createOnGoingAttribute( attr, parameterObject.getActionName(), COLUMN.ACTION );
                    attributes.add( a );
                }
            }
            if( parameterObject.getOnGoingAttributesForEnvironment() != null ) {
                for( String attr : parameterObject.getOnGoingAttributesForEnvironment() ) {
                    OnGoingAttribute a = OnGoingAttribute.createOnGoingAttribute( attr, "", COLUMN.ENVIRONMENT );
                    attributes.add( a );
                }
            }
        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            return false;
        }
        return true;
    }

    private Session prepareSession( SessionAttributes parameterObject ) {
        return new Session( parameterObject.getSessionId(), parameterObject.getPolicySet(), parameterObject.getOriginalRequest(),
            parameterObject.getStatus(),
            parameterObject.getPepURI(), parameterObject.getMyIP() );
    }

    /**
     * Retrieves the list of sessions interested by that attribute id
     *
     * @param attributeId
     *          the attribute id in which sessions are interested
     * @return the list of sessions interested in that attribute id
     */
    @Override
    public List<SessionInterface> getSessionsForAttribute( String attributeId ) {
        validStateAndArguments( attributeId );
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).query();

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves the list of sessions that have that attributeid and that subject
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attributeid related to different
     * subject
     *
     * @param subjectName
     *          the name of the subject
     * @param attributeId
     *          the attribute id
     * @return the list of sessions interested by the couple
     */
    @Override
    public List<SessionInterface> getSessionsForSubjectAttributes(
            String subjectName, String attributeId ) {
        validStateAndArguments( subjectName, attributeId );
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .isNotNull( OnGoingAttribute.SUBJECTNAME_FIELD ).and()
                .isNull( OnGoingAttribute.RESOURCENAME_FIELD ).and()
                .isNull( OnGoingAttribute.ACTIONNAME_FIELD ).and()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).and()
                .eq( OnGoingAttribute.SUBJECTNAME_FIELD, subjectName ).query();

            if( attributes == null ) {
                return new ArrayList<>();
            }

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves the list of sessions that have that attributeid and that object
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attribute id related the a
     * different object
     *
     * @param resourceName
     *          the name of the object
     * @param attributeId
     *          the attribute id
     * @return the list of sessions interested by the couple
     */
    @Override
    public List<SessionInterface> getSessionsForResourceAttributes(
            String resourceName, String attributeId ) {
        validStateAndArguments( resourceName, attributeId );
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .isNull( OnGoingAttribute.SUBJECTNAME_FIELD ).and()
                .isNotNull( OnGoingAttribute.RESOURCENAME_FIELD ).and()
                .isNull( OnGoingAttribute.ACTIONNAME_FIELD ).and()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).and()
                .eq( OnGoingAttribute.RESOURCENAME_FIELD, resourceName ).query();

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
        }
        return new ArrayList<>();
    }

    /**
     * Retrieves the list of sessions that have that attributeid and that action
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attribute related to different
     * actions.
     *
     * @param actionName
     *          the name of the action
     * @param attributeId
     *          the id of the attribute
     * @return the list of sessions interested by the couple
     */
    @Override
    public List<SessionInterface> getSessionsForActionAttributes(
            String actionName, String attributeId ) {
        validStateAndArguments( actionName, attributeId );
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .isNull( OnGoingAttribute.SUBJECTNAME_FIELD ).and()
                .isNull( OnGoingAttribute.RESOURCENAME_FIELD ).and()
                .isNotNull( OnGoingAttribute.ACTIONNAME_FIELD ).and()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).and()
                .eq( OnGoingAttribute.ACTIONNAME_FIELD, actionName ).query();

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
        }
        return new ArrayList<>();
    }

    /**
     * Retrieve the session that is identified by the specified session id
     *
     * @param sessionId
     *          the id of the session in which we're interested
     * @return the object implementing the Session interface
     */
    @Override
    public Optional<SessionInterface> getSessionForId( String sessionId ) {
        validStateAndArguments( sessionId );
        try {
            return Optional.ofNullable( sessionDao.queryForId( sessionId ) );
        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            return Optional.empty();
        }
    }

    /**
     * Retrieves the list of sessions that are sharing the same status
     *
     * @param status
     *          the status in which we're interested in
     * @return the list of session interface that have that status
     */
    @Override
    public List<SessionInterface> getSessionsForStatus( String status ) {
        validStateAndArguments( status );
        try {
            QueryBuilder<Session, String> qbSessions = sessionDao.queryBuilder();
            List<Session> list = qbSessions.where()
                .eq( Session.STATUS_FIELD_NAME, status ).query();
            List<SessionInterface> returnList = new ArrayList<>( list.size() );
            for( Session session : list ) {
                returnList.add( session );
            }
            return returnList;
        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            return new ArrayList<>();
        }
    }

    /**
     * Retrieve the list of sessions related to that attributeName
     *
     * @param attributeId
     *          attribute name related to the environment in which we're
     *          interested into
     * @return the list of sessions
     */
    @Override
    public List<SessionInterface> getSessionsForEnvironmentAttributes(
            String attributeId ) {
        validStateAndArguments( attributeId );
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .isNull( OnGoingAttribute.SUBJECTNAME_FIELD ).and()
                .isNull( OnGoingAttribute.RESOURCENAME_FIELD ).and()
                .isNull( OnGoingAttribute.ACTIONNAME_FIELD ).and()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).query();

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
        }
        return new ArrayList<>();
    }

    @Override
    public List<OnGoingAttributesInterface> getOnGoingAttributes( String sessionId ) {
        validStateAndArguments( sessionId );
        try {
            Session session = sessionDao.queryForId( sessionId );
            return session.getOnGoingAttribute();
        } catch( Exception e ) {
            log.severe( String.format( MSG_ERR_SQL, e.getMessage() ) );
            return new ArrayList<>();
        }
    }

    @Override
    public STATUS checkSession( String sessionId, Attribute attribute ) {
        return null;
    }

    @Override
    public boolean insertSession( SessionInterface session, Attribute attribute ) {
        return false;
    }

    @Override
    public boolean stopSession( SessionInterface session ) {
        return false;
    }

    private void validStateAndArguments( Object... objects ) {
        isInitialized();
        checkObjectsNotNull( objects );
    }

    @Override
    public boolean isInitialized() {
        Reject.ifFalse( initialized, "SessionManager was not correctly initialized" );
        return initialized;
    }

    private boolean checkObjectsNotNull( Object... objects ) {
        if( objects == null ) {
            return false;
        }
        for( Object object : objects ) {
            if( object == null ) {
                return false;
            }
            if( object instanceof String ) {
                Reject.ifBlank( (String) object );
            }
        }
        return true;
    }
}
