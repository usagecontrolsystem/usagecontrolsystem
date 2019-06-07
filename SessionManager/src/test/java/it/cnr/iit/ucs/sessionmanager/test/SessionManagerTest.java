package it.cnr.iit.ucs.sessionmanager.test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.properties.components.SessionManagerProperties;
import it.cnr.iit.ucs.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucs.sessionmanager.SessionAttributesBuilder;
import it.cnr.iit.ucs.sessionmanager.SessionInterface;
import it.cnr.iit.ucs.sessionmanager.SessionManager;

@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@ActiveProfiles( "test" )
@RunWith( SpringRunner.class )
@SpringBootTest
@SpringBootConfiguration
public class SessionManagerTest {

    private static final Logger log = Logger.getLogger( SessionManagerTest.class.getName() );

    @Value( "${ucs.session-manager.name}" )
    private String className;

    @Value( "${ucs.session-manager.communication}" )
    private String communicationType;

    @Value( "${ucs.session-manager.db-uri}" )
    private String dbUri;

    @Value( "${session.sessionid}" )
    private String sessionId;

    @Value( "${session.policy}" )
    private String policy;

    @Value( "${session.request}" )
    private String request;

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

    private SessionManager sessionManager;

    @Before
    public void init() {

        SessionManagerProperties sessionManagerProperties = new SessionManagerProperties() {

            @Override
            public String getName() {
                return className;
            }

            @Override
            public String getDbUri() {
                return dbUri;
            }
        };

        sessionManager = new SessionManager( sessionManagerProperties );
        sessionManager.start();
    }

    @Test
    public void testDBConnection() throws Exception {
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy )
            .setOriginalRequest( request ).setStatus( STATUS.TRY.name() )
            .setPepURI( pepuri ).setMyIP( myip ).setActionName( action ).setSubjectName( subject ).setResourceName( resource ).build() );
        Assert.assertEquals( true, status );
        SessionInterface sessionInterface = sessionManager.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        log.info( sessionInterface.toString() );
    }

    @Test
    public void testSameSessionId() throws Exception {
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy )
            .setOriginalRequest( request ).setStatus( STATUS.TRY.name() )
            .setPepURI( pepuri ).setMyIP( myip ).setActionName( action ).setSubjectName( subject ).setResourceName( resource ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy )
            .setOriginalRequest( request ).setStatus( STATUS.TRY.name() )
            .setPepURI( pepuri ).setMyIP( myip ).setActionName( action ).setSubjectName( subject ).setResourceName( resource ).build() );
        Assert.assertEquals( false, status );
    }

    @Test
    public void testOnGoingAttributesPerSubject() throws Exception {
        log.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
        String[] attributesPerSubject = new String[] { "role" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForSubject( Arrays.asList( attributesPerSubject ) ).setMyIP( myip ).setPepURI( pepuri )
            .setSubjectName( subject ).setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.updateEntry( sessionId, STATUS.START.name() );
        Assert.assertEquals( true, status );
        SessionInterface sessionInterface = sessionManager.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), STATUS.START.name() );
        sessionInterface = sessionManager.getSessionsForSubjectAttributes( subject, "role" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        sessionInterface = sessionManager.getSessionsForAttribute( "role" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testOnGoingAttributesPerAction() throws Exception {
        log.info( "*******TESTING OGA PER ACTION: " + action + "****" );
        String[] attributesPerAction = new String[] { "action" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForAction( Arrays.asList( attributesPerAction ) ).setMyIP( myip ).setPepURI( pepuri )
            .setActionName( action ).setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.updateEntry( sessionId, STATUS.START.name() );
        Assert.assertEquals( true, status );
        SessionInterface sessionInterface = sessionManager.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), STATUS.START.name() );
        sessionInterface = sessionManager.getSessionsForActionAttributes( action, "action" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        log.info( "*******END TESTING OGA PER ACTION****" );
    }

    @Test
    public void testOnGoingAttributesPerResource() throws Exception {
        log.info( "*******TESTING OGA PER RESOURCE: " + resource + "****" );
        String[] attributesPerResource = new String[] { "resource" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForResource( Arrays.asList( attributesPerResource ) ).setMyIP( myip ).setPepURI( pepuri )
            .setResourceName( resource ).setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.updateEntry( sessionId, STATUS.START.name() );
        Assert.assertEquals( true, status );
        SessionInterface sessionInterface = sessionManager.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), STATUS.START.name() );
        sessionInterface = sessionManager.getSessionsForResourceAttributes( resource, "resource" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        log.info( "*******END TESTING OGA PER RESOURCE****" );
    }

    @Test
    public void testOnGoingAttributesPerEnvironment() throws Exception {
        log.info( "*******TESTING OGA PER ENVIRONMENT****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForEnvironment( Arrays.asList( attributesPerEnvironment ) ).setMyIP( myip ).setPepURI( pepuri )
            .setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.updateEntry( sessionId, STATUS.START.name() );
        Assert.assertEquals( true, status );
        SessionInterface sessionInterface = sessionManager.getSessionForId( sessionId ).orElse( null );
        Assert.assertNotNull( sessionInterface );
        Assert.assertEquals( sessionInterface.getStatus(), STATUS.START.name() );
        sessionInterface = sessionManager.getSessionsForEnvironmentAttributes( "temperature" ).get( 0 );
        assertTrue( sessionInterface.getId().equals( sessionId ) );
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testGetSession() throws Exception {
        log.info( "*******TESTING GET SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForEnvironment( Arrays.asList( attributesPerEnvironment ) ).setMyIP( myip ).setPepURI( pepuri )
            .setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        List<SessionInterface> sessions = sessionManager.getSessionsForStatus( STATUS.TRY.name() );
        Assert.assertTrue( sessions.size() > 0 );
        sessions = sessionManager.getSessionsForStatus( STATUS.START.name() );
        Assert.assertTrue( sessions == null || sessions.size() == 0 );
        log.info( "*******END TESTING GET SESSION****" );
    }

    @Test
    public void testDeleteSession() throws Exception {
        log.info( "*******TESTING DELETE SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForEnvironment( Arrays.asList( attributesPerEnvironment ) ).setMyIP( myip ).setPepURI( pepuri )
            .setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        status = sessionManager.deleteEntry( sessionId );
        Assert.assertTrue( status );
        sessionManager.stop();
        log.info( "*******END TESTING DELETE SESSION****" );
    }

    @Test
    public void testGetOnGoingAttributes() throws Exception {
        log.info( "*******TESTING GET On going Attributes****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForEnvironment( Arrays.asList( attributesPerEnvironment ) ).setMyIP( myip ).setPepURI( pepuri )
            .setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        List<OnGoingAttributesInterface> attributes = sessionManager.getOnGoingAttributes( sessionId );
        Assert.assertTrue( attributes.size() > 0 );
        for( OnGoingAttributesInterface attribute : attributes ) {
            Assert.assertTrue( attribute.getId() != null );
            Assert.assertTrue( attribute.getId().length() > 0 );
        }
        attributes = sessionManager.getOnGoingAttributes( STATUS.START.name() );
        Assert.assertTrue( attributes == null || attributes.size() == 0 );
        log.info( "*******END TESTING GET On going Attributes****" );
    }

    @Test
    public void failureTestOnGoingAttributesPerSubject() throws Exception {
        log.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
        String[] attributesPerSubject = new String[] { "role" };
        boolean status = sessionManager.createEntry( new SessionAttributesBuilder().setSessionId( sessionId )
            .setPolicySet( policy ).setOriginalRequest( request )
            .setOnGoingAttributesForSubject( Arrays.asList( attributesPerSubject ) ).setMyIP( myip ).setPepURI( pepuri )
            .setSubjectName( subject ).setStatus( STATUS.TRY.name() ).build() );
        Assert.assertEquals( true, status );
        List<SessionInterface> list = null;
        try {
            status = sessionManager.updateEntry( null, STATUS.START.name() );
        } catch( Exception e ) {
            status = false;
        }
        status = sessionManager.updateEntry( sessionId, STATUS.START.name() );
        list = sessionManager.getSessionsForSubjectAttributes( subject, "dasda" );
        Assert.assertEquals( 0, list.size() );
        Optional<SessionInterface> sessionInterface = sessionManager.getSessionForId( "dasdsa" );
        Assert.assertFalse( sessionInterface.isPresent() );
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

}
