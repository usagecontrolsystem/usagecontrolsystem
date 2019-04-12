package it.cnr.iit.sqlmiddleware;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.google.gson.Gson;

import it.cnr.iit.sqlmiddlewareinterface.SQLMiddlewarePIPConfigurationInterface;
import it.cnr.iit.sqlmiddlewareinterface.SQLMiddlewarePIPInterface;

/**
 * This is the class implementing the SQLMiddleware.
 * <p>
 * The SQLMiddleware is a sublayer that helps PIP to interact with all SQL
 * databases. The configuration to interact with the database will depend on the
 * <a href=
 * "https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/cfg/Configuration.html">Configuration</a>
 * class in which the PIP that will interact with the database will put all the
 * specific informations. <br>
 * This sublayer will use the singleton pattern in order to have a single
 * instance of the middleware for database schema.
 * </p>
 * <p>
 * Parameters passed by the PIP to the middleware in order to issue the
 * constructor are:
 * <ul>
 * <li>driver</li>
 * <li>url</li>
 * <li>username</li>
 * <li>password</li>
 * <li>connections</li>
 * </ul>
 * These will be mapped in a MiddlewareConfigurationInterface that will act as a
 * translator between the configuration the PIP configurator inputs and the
 * configuration required by hibernate
 * </p>
 *
 * @author antonio
 *
 */
public final class SQLMiddleware implements SQLMiddlewarePIPInterface {

    private static final Logger log = Logger.getLogger( SQLMiddleware.class.getName() );

    // storage of all the configuration along with all the required middleware.
    private static final ConcurrentHashMap<String, SQLMiddleware> configuration = new ConcurrentHashMap<>();

    private static final String select = "from %s %s";

    private HashMap<String, CacheEntry> cache = new HashMap<>();

    // this is the hibernate configuration
    private Configuration hibernateConfiguration;

    // the session factory
    private SessionFactory sessionFactory;

    private static volatile boolean initialized = false;

    // private Object mutex = new Object();

    /* prevent instantiation */
    private SQLMiddleware() {

    }

    /**
     * Instantiates a new SQLMiddleware IF necessary basing on the passed
     * configuration. Upon being created the just created middleware will be put
     * inside an HashMap using as key the correspondent configuration passed by
     * the PIP
     *
     * @param sqlMiddlewarePIPInterfac
     *          the interface via which the two entities communicate.
     * @return the SQLMiddleware created
     */
    public static SQLMiddleware createMiddleware(
            SQLMiddlewarePIPConfigurationInterface sqlMiddlewarePIPInterface ) {
        // BEGIN parameter checking
        if( sqlMiddlewarePIPInterface == null
                || sqlMiddlewarePIPInterface.toString() == null
                || sqlMiddlewarePIPInterface.toString().isEmpty() ) {
            // TODO throw exception
            return null;
        }
        // END parameter checking

        if( configuration.containsKey( sqlMiddlewarePIPInterface.toString() ) ) {
            configuration
                .get( sqlMiddlewarePIPInterface.toString() ).hibernateConfiguration
                    .addClass( sqlMiddlewarePIPInterface.getClassForTable() );
            return configuration.get( sqlMiddlewarePIPInterface.toString() );
        }

        SQLMiddleware sqlMiddleware = createSQLMiddleware(
            sqlMiddlewarePIPInterface );
        configuration.put( sqlMiddlewarePIPInterface.toString(), sqlMiddleware );

        return sqlMiddleware;
    }

    /**
     * This is the function that effectively creates the middleware by setting up
     * the configuration for it.
     *
     * @param sqlMiddlewarePIPInterface
     *          the interrface via which the two entities communicate
     * @return the SQLMiddleware created in this way
     */
    private static final SQLMiddleware createSQLMiddleware(
            SQLMiddlewarePIPConfigurationInterface sqlMiddlewarePIPInterface ) {
        SQLMiddleware sqlMiddleware = new SQLMiddleware();
        sqlMiddleware.hibernateConfiguration = new Configuration();
        sqlMiddleware.hibernateConfiguration.setProperty(
            HIBERNATE_CONFIGURATIONS.DRIVER_CLASS.getString(),
            sqlMiddlewarePIPInterface.getDriver() );
        sqlMiddleware.hibernateConfiguration.setProperty(
            HIBERNATE_CONFIGURATIONS.PASSWORD.getString(),
            sqlMiddlewarePIPInterface.getPassword() );
        sqlMiddleware.hibernateConfiguration.setProperty(
            HIBERNATE_CONFIGURATIONS.URL.getString(),
            sqlMiddlewarePIPInterface.getURL() );
        sqlMiddleware.hibernateConfiguration.setProperty(
            HIBERNATE_CONFIGURATIONS.USERNAME.getString(),
            sqlMiddlewarePIPInterface.getUsername() );
        sqlMiddleware.hibernateConfiguration.setProperty(
            HIBERNATE_CONFIGURATIONS.POOL_SIZE.getString(),
            sqlMiddlewarePIPInterface.getConnections() );
        sqlMiddleware.hibernateConfiguration
            .setProperty( HIBERNATE_CONFIGURATIONS.DIALECT.getString(), sqlMiddleware
                .getDialectFromDriver( sqlMiddlewarePIPInterface.getDriver() ) );
        log.info(
            sqlMiddleware.hibernateConfiguration.getProperty( "hibernate.dialect" ) );
        sqlMiddleware.hibernateConfiguration
            .addAnnotatedClass( sqlMiddlewarePIPInterface.getClassForTable() );
        sqlMiddleware.firstInitialization();

        return sqlMiddleware;
    }

    /**
     * Retrieves the proper dialect for the driver we're using
     *
     * @param driver
     *          the driver we're using
     * @return the string representing the proper dialect
     */
    private String getDialectFromDriver( String driver ) {
        if( driver.contains( "mysql" ) ) {
            return "org.hibernate.dialect.MySQLDialect";
        }
        return null;
    }

    @Override
    public <T> T performQuerySingleRecord( String tableName, String condition,
            Class<T> returnedClass ) {
        // BEGIN PARAMETER CHECKING
        if( tableName == null || tableName.length() == 0 || condition == null ) {
            log.warning( "error performin query on a single record" );
            // TODO throw exception
            return null;
        }
        if( initialized == false ) {
            firstInitialization();
        }
        // END PARAMETER CHECKING

        String query = String.format( select, tableName, condition );
        log.info( query );
        /*
         * if (cache.containsKey(query)) { if (System.currentTimeMillis() -
         * cache.get(query).getTime() < (10 * 1000)) { CacheEntry cacheEntry =
         * cache.get(query); return cacheEntry.getStored(returnedClass); } }
         */
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<T> result = session.createQuery( query, returnedClass );
        if( result.list().size() == 0 ) {
            session.clear();
            session.close();
            return null;
        } else {
            T object = result.getSingleResult();
            session.clear();
            session.close();
            CacheEntry cacheEntry = new CacheEntry( object );
            cache.put( query, cacheEntry );
            return object;
        }
    }

    /**
     * The aim of this function is to provide the first initialization of the
     * session factory required to build the queries that we want to use on the
     * database
     */
    private synchronized void firstInitialization() {
        sessionFactory = hibernateConfiguration.buildSessionFactory();
        initialized = true;
    }

    @Override
    public <T> List<T> performQueryMultipleRecords( String tableName,
            String condition, Class<T> returnedClass ) {
        // BEGIN PARAMETER CHECKING
        if( tableName == null || tableName.length() == 0 || condition == null ) {
            log.warning( "error performin query on a multiple records" );
            // TODO throw exception
            return null;
        }
        if( initialized == false ) {
            firstInitialization();
        }
        // END PARAMETER CHECKING

        List<T> resultList;
        String query = String.format( select, tableName, condition );
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        Query<T> result = session.createQuery( query, returnedClass );
        resultList = result.list();
        session.clear();
        session.close();
        return resultList;
    }

    @Override
    public <T> void insert( String tableName, T object ) {
        // TODO implement
    }

    private class CacheEntry {
        private String stored;
        private Long time;

        <T> CacheEntry( T stored ) {

            this.stored = new Gson().toJson( stored );
            time = System.currentTimeMillis();
        }

        public <T> T getStored( Class<T> returnedClass ) {
            return new Gson().fromJson( stored, returnedClass );
        }

        public Long getTime() {
            return time;
        }
    }

    @Override
    public String toString() {
        return hibernateConfiguration
            .getProperty( HIBERNATE_CONFIGURATIONS.URL.getString() );
    }
}
