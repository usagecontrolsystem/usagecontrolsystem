package it.cnr.iit.sessionmanagerdesktop.test;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.sessionmanagerdesktop.SessionManagerDesktop;
import it.cnr.iit.ucs.configuration.session_manager.SessionManagerProperties;
import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.utility.JsonUtility;

@EnableConfigurationProperties
@TestPropertySource( properties = "application-test.properties" )
@ActiveProfiles( "test" )
@RunWith( SpringRunner.class )
@SpringBootTest
@SpringBootConfiguration
public class SessionManagerTest {

    private static final Logger LOGGER = Logger.getLogger( SessionManagerTest.class.getName() );

    @Value( "${conf}" )
    private String conf;

    @Value( "${session.sessionid}" )
    private String sessionId;

    @Value( "${session.policy}" )
    private String policy;

    @Value( "${session.request}" )
    private String request;

    @Value( "${session.status.try}" )
    private String tryStatus;

    @Value( "${session.status.start}" )
    private String startStatus;

    @Value( "${session.status.revoke}" )
    private String revokeStatus;

    @Value( "${session.status.end}" )
    private String endStatus;

    @Value( "${session.pepuri}" )
    private String pepuri;

    @Value( "${session.myip}" )
    private String myip;

    @Value( "${session.subject}" )
    private String subject;

    @Value( "${session.resource}" )
    private String resource;

    @Value( "${session.action}" )
    private String action;

    @Value( "${conf.failing}" )
    private String failingConf;

    @InjectMocks
    private SessionManagerDesktop sessionManagerDesktop;
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks( this );
    }

    @Before
    public void init() {
        LOGGER.info( conf );
        try {
            SessionManagerProperties properties = JsonUtility.loadObjectFromJsonString( conf, SessionManagerProperties.class ).get();
            sessionManagerDesktop = new SessionManagerDesktop( properties );
            // TODO do a test for failing conf
            // properties = JAXBUtility.unmarshalToObject( XMLSessionManager.class, failingConf );
        } catch( Exception e ) {
            e.printStackTrace();
        }
        sessionManagerDesktop.start();
    }

    public SessionManagerTest() {
        /*
         * System.out.println(conf); try { //sessionManagerDesktop = new
         * SessionManagerDesktop(JAXBUtility.unmarshalToObject(XMLSessionManager.class, conf)); } catch (JAXBException
         * e) { // TODO Auto-generated catch block e.printStackTrace(); }
         */
    }

    @Test
    public void testMockDBConnection() throws Exception {
        // init();
        Mockito.when( mockConnection.createStatement() ).thenReturn( mockStatement );
        Mockito.when( mockConnection.createStatement().executeUpdate( Matchers.any() ) ).thenReturn( 1 );
        boolean status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null,
            tryStatus, pepuri, myip, subject, action, resource );
        Assert.assertEquals( status, true );
        // Mockito.verify(mockConnection.createStatement(), Mockito.times(1));
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        LOGGER.info( sessionInterface.toString() );
    }

    @Test
    public void testSameSessionId() throws Exception {
        Mockito.when( mockConnection.createStatement() ).thenReturn( mockStatement );
        Mockito.when( mockConnection.createStatement().executeUpdate( Matchers.any() ) ).thenReturn( 1 );
        boolean status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null,
            tryStatus, pepuri, myip, subject, action, resource );
        Assert.assertEquals( status, true );
        Mockito.when( mockConnection.createStatement() ).thenReturn( mockStatement );
        Mockito.when( mockConnection.createStatement().executeUpdate( Matchers.any() ) ).thenReturn( 1 );
        status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null, tryStatus,
            pepuri, myip, subject, resource, action );
        Assert.assertEquals( status, false );
    }

    @Test
    public void testOnGoingAttributesPerSubject() throws Exception {
        LOGGER.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
        String[] attributesPerSubject = new String[] { "role" };
        boolean status = sessionManagerDesktop.createEntryForSubject( sessionId, policy, request,
            Arrays.asList( attributesPerSubject ), tryStatus, pepuri, myip, subject );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.updateEntry( sessionId, startStatus );
        Assert.assertEquals( status, true );
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), startStatus );
        sessionInterface = sessionManagerDesktop.getSessionsForSubjectAttributes( subject, "role" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        sessionInterface = sessionManagerDesktop.getSessionsForAttribute( "role" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        LOGGER.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testOnGoingAttributesPerAction() throws Exception {
        LOGGER.info( "*******TESTING OGA PER ACTION: " + action + "****" );
        String[] attributesPerAction = new String[] { "action" };
        boolean status = sessionManagerDesktop.createEntryForAction( sessionId, policy, request,
            Arrays.asList( attributesPerAction ), tryStatus, pepuri, myip, action );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.updateEntry( sessionId, startStatus );
        Assert.assertEquals( status, true );
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), startStatus );
        sessionInterface = sessionManagerDesktop.getSessionsForActionAttributes( action, "action" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        LOGGER.info( "*******END TESTING OGA PER ACTION****" );
    }

    @Test
    public void testOnGoingAttributesPerResource() throws Exception {
        LOGGER.info( "*******TESTING OGA PER RESOURCE: " + resource + "****" );
        String[] attributesPerResource = new String[] { "resource" };
        boolean status = sessionManagerDesktop.createEntryForResource( sessionId, policy, request,
            Arrays.asList( attributesPerResource ), tryStatus, pepuri, myip, resource );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.updateEntry( sessionId, startStatus );
        Assert.assertEquals( status, true );
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), startStatus );
        sessionInterface = sessionManagerDesktop.getSessionsForResourceAttributes( resource, "resource" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        LOGGER.info( "*******END TESTING OGA PER RESOURCE****" );
    }

    @Test
    public void testOnGoingAttributesPerEnvironment() throws Exception {
        LOGGER.info( "*******TESTING OGA PER ENVIRONMENT****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.updateEntry( sessionId, startStatus );
        Assert.assertEquals( status, true );
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).orElse( null );
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), startStatus );
        sessionInterface = sessionManagerDesktop.getSessionsForEnvironmentAttributes( "temperature" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        LOGGER.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testGetSession() throws Exception {
        LOGGER.info( "*******TESTING GET SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        List<SessionInterface> sessions = sessionManagerDesktop.getSessionsForStatus( tryStatus );
        Assert.assertTrue( sessions.size() > 0 );
        sessions = sessionManagerDesktop.getSessionsForStatus( startStatus );
        Assert.assertTrue( sessions == null || sessions.size() == 0 );
        LOGGER.info( "*******END TESTING GET SESSION****" );
    }

    @Test
    public void testDeleteSession() throws Exception {
        LOGGER.info( "*******TESTING DELETE SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.deleteEntry( sessionId );
        Assert.assertTrue( status );
        sessionManagerDesktop.stop();
        LOGGER.info( "*******END TESTING DELETE SESSION****" );
    }

    @Test
    public void testGetOnGoingAttributes() throws Exception {
        LOGGER.info( "*******TESTING GET On going Attributes****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        List<OnGoingAttributesInterface> attributes = sessionManagerDesktop.getOnGoingAttributes( sessionId );
        Assert.assertTrue( attributes.size() > 0 );
        attributes = sessionManagerDesktop.getOnGoingAttributes( startStatus );
        Assert.assertTrue( attributes == null || attributes.size() == 0 );
        LOGGER.info( "*******END TESTING GET On going Attributes****" );
    }

    @Test
    public void failureTestOnGoingAttributesPerSubject() throws Exception {
        LOGGER.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
        String[] attributesPerSubject = new String[] { "role" };
        boolean status = sessionManagerDesktop.createEntryForSubject( sessionId, policy, request,
            Arrays.asList( attributesPerSubject ), tryStatus, pepuri, myip, subject );
        Assert.assertEquals( status, true );
        List<SessionInterface> list = null;
        try {
            status = sessionManagerDesktop.updateEntry( null, startStatus );
        } catch( IllegalArgumentException e ) {
            status = false;
        }
        status = sessionManagerDesktop.updateEntry( sessionId, startStatus );
        list = sessionManagerDesktop.getSessionsForSubjectAttributes( subject, "dasda" );
        Assert.assertEquals( list.size(), 0 );
        Optional<SessionInterface> sessionInterface = sessionManagerDesktop.getSessionForId( "dasdsa" );
        Assert.assertFalse( sessionInterface.isPresent() );
        LOGGER.info( "*******END TESTING OGA PER SUBJECT****" );
    }

}
