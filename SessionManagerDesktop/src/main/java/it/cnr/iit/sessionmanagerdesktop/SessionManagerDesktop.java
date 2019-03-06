/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
package it.cnr.iit.sessionmanagerdesktop;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.HsqldbDatabaseType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLSessionManager;
import it.cnr.iit.xacmlutilities.Attribute;

import iit.cnr.it.ucsinterface.sessionmanager.OnGoingAttribute;
import iit.cnr.it.ucsinterface.sessionmanager.Session;
import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;

/**
 * Creates, updates, deletes and retrieves Sessions by waiting for Context
 * Handler Requests. It exploits OrmLite DAL to manage different relational
 * databases.
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
final public class SessionManagerDesktop implements SessionManagerInterface {
    // url to connect to the database
    private String databaseURL;
    private ConnectionSource connection;
    // dao to perform operations on the session table
    private Dao<Session, String> sessionDao;
    // dao to perform operations on the attributes table
    private Dao<OnGoingAttribute, String> attributesDao;

    private Logger LOGGER = Logger
        .getLogger( SessionManagerDesktop.class.getName() );

    private volatile boolean initialized = false;

    /**
     * Constructor
     *
     * @param databaseURL_
     *          URL of the database where data will be stored/retrieved
     */
    protected SessionManagerDesktop( String databaseURL_ ) {
        databaseURL = databaseURL_;
        sessionDao = null;
        attributesDao = null;
    }

    /**
     * Constructor
     *
     * @param databaseURL_
     *          URL of the database where data will be stored/retrieved
     */
    public SessionManagerDesktop( XMLSessionManager xmlSessionManager ) {
        // BEGIN parameter checking
        if( xmlSessionManager == null || xmlSessionManager.getDriver() == null ) {
            return;
        }
        // END parameter checking
        databaseURL = xmlSessionManager.getDriver();
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        // END parameter checking
        try {
            connection = new JdbcConnectionSource( databaseURL );
            sessionDao = DaoManager.createDao( connection, Session.class );
            attributesDao = DaoManager.createDao( connection, OnGoingAttribute.class );

            cleanDbForTestRun(); // FIXME: needs to check state i.e. if test mode etc..

            TableUtils.createTableIfNotExists( connection, Session.class );
            TableUtils.createTableIfNotExists( connection, OnGoingAttribute.class );
        } catch( SQLException ex ) {
            ex.printStackTrace();
            initialized = false;
            return false;
        }
        return true;
    }

    /**
     * This is a work around because ormlite still thinks that
     * hsqldb cannot handle keyword 'if exists' in create
     * so drop the tables first
     *
     * @throws SQLException
     */
    private void cleanDbForTestRun() throws SQLException {
        DatabaseType databaseType = connection.getDatabaseType();
        if( databaseType instanceof HsqldbDatabaseType ) {
            TableUtils.dropTable( connection, Session.class, true );
            TableUtils.dropTable( connection, OnGoingAttribute.class, true );
        }
    }

    /**
     * Stops the connection to the database
     *
     * @return true if Session Manager stops properly, false otherwise
     */
    @Override
    public Boolean stop() {
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        // END parameter checking
        try {

            connection.close();

        } catch( SQLException ex ) {
            ex.printStackTrace();
            initialized = false;
            return false;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() || status == null
                || status.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + sessionId + "\t" + status );
            return false;
        }
        // END parameter checking
        try {
            Session s = sessionDao.queryForId( sessionId );
            s.setStatus( status );
            sessionDao.update( s );
        } catch( SQLException ex ) {
            ex.printStackTrace();
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + sessionId );
            return false;
        }
        // END parameter checking
        try {
            ForeignCollection<OnGoingAttribute> a = sessionDao.queryForId( sessionId )
                .getOnGoingAttributesAsForeign();
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() || status == null
                || status.isEmpty() || policySet == null || policySet.isEmpty()
                || originalRequest == null || originalRequest.isEmpty()
                || onGoingAttributesForSubject == null || pepURI == null
                || pepURI.isEmpty() || myIP == null || myIP.isEmpty()
                || subjectName == null || subjectName.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + sessionId + "\t" + status );
            return false;
        }
        // END parameter checking
        return createEntry( sessionId, policySet, originalRequest,
            onGoingAttributesForSubject, null, null, null, status, pepURI, myIP,
            subjectName, null, null );
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() || status == null
                || status.isEmpty() || policySet == null || policySet.isEmpty()
                || originalRequest == null || originalRequest.isEmpty()
                || onGoingAttributesForResource == null || pepURI == null
                || pepURI.isEmpty() || myIP == null || myIP.isEmpty()
                || resourceName == null || resourceName.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + sessionId + "\t" + status );
            return false;
        }
        // END parameter checking
        return createEntry( sessionId, policySet, originalRequest, null,
            onGoingAttributesForResource, null, null, status, pepURI, myIP, null,
            resourceName, null );
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() || status == null
                || status.isEmpty() || policySet == null || policySet.isEmpty()
                || originalRequest == null || originalRequest.isEmpty()
                || onGoingAttributesForAction == null || pepURI == null
                || pepURI.isEmpty() || myIP == null || myIP.isEmpty()
                || actionName == null || actionName.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + sessionId + "\t" + status );
            return false;
        }
        // END parameter checking
        return createEntry( sessionId, policySet, originalRequest, null, null,
            onGoingAttributesForAction, null, status, pepURI, myIP, null, null,
            actionName );
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return false;
        }
        if( sessionId == null || sessionId.isEmpty() || status == null
                || status.isEmpty() || policySet == null || policySet.isEmpty()
                || originalRequest == null || originalRequest.isEmpty()
                || onGoingAttributesForEnvironment == null || pepURI == null
                || pepURI.isEmpty() || myIP == null || myIP.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + sessionId + "\t" + status );
            return false;
        }
        // END parameter checking
        return createEntry( sessionId, policySet, originalRequest, null, null, null,
            onGoingAttributesForEnvironment, status, pepURI, myIP, null, null,
            null );
    }

    /**
     * Creates an entry for a session. This is the general function, in this case
     * the request has ongoingattributes related to the subject, the
     * object/resource, the action and the environment
     *
     * @param sessionId
     *          the id of the session
     * @param policySet
     *          the policy set used in the evaluation
     * @param originalRequest
     *          the original request
     * @param onGoingAttributesForSubject
     *          the on going attributes related to the subject
     * @param onGoingAttributesForResource
     *          the on going attributes related to the object
     * @param onGoingAttributesForAction
     *          the on going attributes related to the action
     * @param onGoingAttributesForEnvironment
     *          the on going attributes related to the environment
     * @param status
     *          the status of the session
     * @param pepURI
     *          the uri of the pep
     * @param myIP
     *          the ip of the contexthandler that has evaluated the request
     * @param subjectName
     *          the name of the subject to which the attributes are related
     * @param resourceName
     *          the name of the object to which the attributes are related
     * @param actionName
     *          the name of the action to which the attribute are related
     * @return true if everything goes fine, false otherwise
     */
    @Override
    public Boolean createEntry( String sessionId, String policySet,
            String originalRequest, List<String> onGoingAttributesForSubject,
            List<String> onGoingAttributesForResource,
            List<String> onGoingAttributesForAction,
            List<String> onGoingAttributesForEnvironment, String status,
            String pepURI, String myIP, String subjectName, String resourceName,
            String actionName ) {
        try {

            Session s = new Session( sessionId, policySet, originalRequest, status,
                pepURI, myIP );
            // IMPORTANTE
            if( sessionDao.idExists( sessionId ) ) {
                LOGGER.log( Level.SEVERE, "ID already exists" );
                return false;
            }
            sessionDao.create( s );
            Session sessionResult = sessionDao.queryForId( sessionId );
            ForeignCollection<OnGoingAttribute> attributes = sessionResult
                .getOnGoingAttributesAsForeign();
            if( onGoingAttributesForSubject != null ) {
                for( String attr : onGoingAttributesForSubject ) {
                    OnGoingAttribute a = new OnGoingAttribute( attr, subjectName, null,
                        null );
                    a.setId( UUID.randomUUID().toString() );
                    attributes.add( a );
                }
            }
            if( onGoingAttributesForResource != null ) {
                for( String attr : onGoingAttributesForResource ) {
                    OnGoingAttribute a = new OnGoingAttribute( attr, null, resourceName,
                        null );
                    a.setId( UUID.randomUUID().toString() );
                    attributes.add( a );
                }
            }
            if( onGoingAttributesForAction != null ) {
                for( String attr : onGoingAttributesForAction ) {
                    OnGoingAttribute a = new OnGoingAttribute( attr, null, null,
                        actionName );
                    a.setId( UUID.randomUUID().toString() );
                    attributes.add( a );
                }
            }
            if( onGoingAttributesForEnvironment != null ) {
                for( String attr : onGoingAttributesForEnvironment ) {
                    OnGoingAttribute a = new OnGoingAttribute( attr, null, null, null );
                    a.setId( UUID.randomUUID().toString() );
                    attributes.add( a );
                }
            }
        } catch( SQLException ex ) {
            ex.printStackTrace();
            return false;
        }
        return true;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( attributeId == null || attributeId.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + attributeId );
            return null;
        }
        // END parameter checking
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

        } catch( SQLException ex ) {
            ex.printStackTrace();
        }
        return null;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( attributeId == null || attributeId.isEmpty() || subjectName == null
                || subjectName.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + attributeId
                    + "\t" + subjectName );
            return null;
        }
        // END parameter checking
        try {
            QueryBuilder<OnGoingAttribute, String> qbAttributes = attributesDao
                .queryBuilder();
            List<OnGoingAttribute> attributes = qbAttributes.where()
                .isNotNull( OnGoingAttribute.SUBJECTNAME_FIELD ).and()
                .isNull( OnGoingAttribute.RESOURCENAME_FIELD ).and()
                .isNull( OnGoingAttribute.ACTIONNAME_FIELD ).and()
                .eq( OnGoingAttribute.ATTRIBUTEID_FIELD, attributeId ).and()
                .eq( OnGoingAttribute.SUBJECTNAME_FIELD, subjectName ).query();

            List<SessionInterface> sessions = new LinkedList<>();
            for( OnGoingAttribute attr : attributes ) {
                // add the considered session only if its status is equal to 's'
                if( attr.getSession().getStatus().equals( "s" )
                        || attr.getSession().getStatus().equals( "r" ) ) {
                    sessions.add( attr.getSession() );
                }
            }
            return sessions;

        } catch( SQLException ex ) {
            ex.printStackTrace();
        }
        return null;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( attributeId == null || attributeId.isEmpty() || resourceName == null
                || resourceName.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + attributeId
                    + "\t" + resourceName );
            return null;
        }
        // END parameter checking
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

        } catch( SQLException ex ) {
            ex.printStackTrace();
        }
        return null;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( attributeId == null || attributeId.isEmpty() || actionName == null
                || actionName.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + attributeId + "\t" + actionName );
            return null;
        }
        // END parameter checking
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

        } catch( SQLException ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieve the session that is identified by the specified session id
     *
     * @param sessionId
     *          the id of the session in which we're interested
     * @return the object implementing the Session interface
     */
    @Override
    public SessionInterface getSessionForId( String sessionId ) {
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( sessionId == null || sessionId.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + sessionId );
            return null;
        }
        // END parameter checking
        try {
            // select * from sessions where session_id == 'sessionId'
            return sessionDao.queryForId( sessionId );
        } catch( SQLException ex ) {
            return null;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( status == null || status.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + status );
            return null;
        }
        // END parameter checking
        try {
            QueryBuilder<Session, String> qbSessions = sessionDao.queryBuilder();
            List<Session> list = qbSessions.where()
                .eq( Session.STATUS_FIELD_NAME, status ).query();
            List<SessionInterface> returnList = new ArrayList<>( list.size() );
            for( Session session : list ) {
                returnList.add( session );
            }
            return returnList;
        } catch( SQLException ex ) {
            ex.printStackTrace();
            return null;
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
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( attributeId == null || attributeId.isEmpty() ) {
            LOGGER.log( Level.SEVERE,
                "Passed parameters are not valid " + attributeId );
            return null;
        }
        // END parameter checking
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

        } catch( SQLException ex ) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<OnGoingAttribute> getOnGoingAttributes( String sessionId ) {
        // BEGIN parameter checking
        if( initialized == false ) {
            LOGGER.log( Level.SEVERE, "Session Manager was not correctly initialized" );
            return null;
        }
        if( sessionId == null || sessionId.isEmpty() ) {
            LOGGER.log( Level.SEVERE, "Passed parameters are not valid " + sessionId );
            return null;
        }
        // END parameter checking
        try {
            // select * from sessions where session_id == 'sessionId'
            Session session = sessionDao.queryForId( sessionId );
            return session.getOnGoingAttribute();
        } catch( Exception ex ) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public STATUS checkSession( String sessionId, Attribute attribute ) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean insertSession( SessionInterface session, Attribute attribute ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean stopSession( SessionInterface session ) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
