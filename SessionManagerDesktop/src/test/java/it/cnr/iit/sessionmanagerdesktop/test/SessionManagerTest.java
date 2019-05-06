package it.cnr.iit.sessionmanagerdesktop.test;

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

import it.cnr.iit.sessionmanagerdesktop.SessionManagerDesktop;
import it.cnr.iit.ucs.properties.components.SessionManagerProperties;
import it.cnr.iit.ucsinterface.sessionmanager.OnGoingAttributesInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;

@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@ActiveProfiles( "test" )
@RunWith( SpringRunner.class )
@SpringBootTest
@SpringBootConfiguration
public class SessionManagerTest {

    private static final Logger log = Logger.getLogger( SessionManagerTest.class.getName() );

    @Value( "${ucf.session-manager.class-name}" )
    private String className;

    @Value( "${ucf.session-manager.communication-type}" )
    private String communicationType;

    @Value( "${ucf.session-manager.type}" )
    private String type;

    @Value( "${ucf.session-manager.db-uri}" )
    private String dbUri;

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

    private SessionManagerDesktop sessionManagerDesktop;

    @Before
    public void init() {

        SessionManagerProperties sessionManagerProperties = new SessionManagerProperties() {

            @Override
            public String getCommunicationType() {
                return communicationType;
            }

            @Override
            public String getClassName() {
                return className;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public String getDbUri() {
                return dbUri;
            }
        };

        sessionManagerDesktop = new SessionManagerDesktop( sessionManagerProperties );
        sessionManagerDesktop.start();
    }

    @Test
    public void testDBConnection() throws Exception {
        boolean status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null,
            tryStatus, pepuri, myip, subject, action, resource );
        Assert.assertEquals( status, true );
        SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId( sessionId ).get();
        Assert.assertNotNull( sessionInterface );
        log.info( sessionInterface.toString() );
    }

    @Test
    public void testSameSessionId() throws Exception {
        boolean status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null,
            tryStatus, pepuri, myip, subject, action, resource );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.createEntry( sessionId, policy, request, null, null, null, null, tryStatus,
            pepuri, myip, subject, resource, action );
        Assert.assertEquals( status, false );
    }

    @Test
    public void testOnGoingAttributesPerSubject() throws Exception {
        log.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
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
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testOnGoingAttributesPerAction() throws Exception {
        log.info( "*******TESTING OGA PER ACTION: " + action + "****" );
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
        log.info( "*******END TESTING OGA PER ACTION****" );
    }

    @Test
    public void testOnGoingAttributesPerResource() throws Exception {
        log.info( "*******TESTING OGA PER RESOURCE: " + resource + "****" );
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
        log.info( "*******END TESTING OGA PER RESOURCE****" );
    }

    @Test
    public void testOnGoingAttributesPerEnvironment() throws Exception {
        log.info( "*******TESTING OGA PER ENVIRONMENT****" );
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
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

    @Test
    public void testGetSession() throws Exception {
        log.info( "*******TESTING GET SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        List<SessionInterface> sessions = sessionManagerDesktop.getSessionsForStatus( tryStatus );
        Assert.assertTrue( sessions.size() > 0 );
        sessions = sessionManagerDesktop.getSessionsForStatus( startStatus );
        Assert.assertTrue( sessions == null || sessions.size() == 0 );
        log.info( "*******END TESTING GET SESSION****" );
    }

    @Test
    public void testDeleteSession() throws Exception {
        log.info( "*******TESTING DELETE SESSIOn****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        status = sessionManagerDesktop.deleteEntry( sessionId );
        Assert.assertTrue( status );
        sessionManagerDesktop.stop();
        log.info( "*******END TESTING DELETE SESSION****" );
    }

    @Test
    public void testGetOnGoingAttributes() throws Exception {
        log.info( "*******TESTING GET On going Attributes****" );
        String[] attributesPerEnvironment = new String[] { "temperature" };
        boolean status = sessionManagerDesktop.createEntryForEnvironment( sessionId, policy, request,
            Arrays.asList( attributesPerEnvironment ), tryStatus, pepuri, myip );
        Assert.assertEquals( status, true );
        List<OnGoingAttributesInterface> attributes = sessionManagerDesktop.getOnGoingAttributes( sessionId );
        Assert.assertTrue( attributes.size() > 0 );
        attributes = sessionManagerDesktop.getOnGoingAttributes( startStatus );
        Assert.assertTrue( attributes == null || attributes.size() == 0 );
        log.info( "*******END TESTING GET On going Attributes****" );
    }

    @Test
    public void failureTestOnGoingAttributesPerSubject() throws Exception {
        log.info( "*******TESTING OGA PER SUBJECT: " + subject + "****" );
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
        log.info( "*******END TESTING OGA PER SUBJECT****" );
    }

}
