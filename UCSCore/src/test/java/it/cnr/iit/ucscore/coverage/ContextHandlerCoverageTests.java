package it.cnr.iit.ucscore.coverage;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.contexthandler.ContextHandler;
import it.cnr.iit.ucs.exceptions.EndAccessException;
import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.ucs.exceptions.StartAccessException;
import it.cnr.iit.ucs.message.attributechange.AttributeChangeMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucscore.coverage.properties.TestProperties;
import it.cnr.iit.ucsrest.properties.UCSRestProperties;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;
import it.cnr.iit.xacml.Category;
import it.cnr.iit.xacml.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@SpringBootTest
@DirtiesContext( classMode = ClassMode.BEFORE_EACH_TEST_METHOD )
@EnableAutoConfiguration
@ComponentScan( basePackages = { "it.cnr.iit" } )
@ContextConfiguration( classes = { UCSRestProperties.class, TestProperties.class } )
@RunWith( SpringRunner.class )
public class ContextHandlerCoverageTests extends UCSRestBaseTests {

    @Autowired
    private UCSProperties properties;

    private String policy;
    private String request;

    @PostConstruct
    private void init() throws URISyntaxException, IOException, JAXBException {
        log.info( "Init tests " );
        Thread.interrupted(); // Avoid a nasty exception
        policy = FileUtility.readFileAsString( testProperties.getPolicyFile() );
        request = FileUtility.readFileAsString( testProperties.getRequestFile() );
    }

    @Test( expected = PreconditionException.class )
    public void contextHandlerTryAccessShouldFail() throws PreconditionException, PolicyException, RequestException {
        ContextHandler contextHandler = getContextHandler( properties );
        getPIPS( properties );
        initContextHandler( contextHandler );
        // set the pdp response to return deny
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        contextHandler.startMonitoringThread();
        contextHandler.tryAccess( null );
        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerStartAccess() throws StartAccessException, Exception {
        ContextHandler contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, STATUS.TRY.name() ) );
        // this line makes the start access to take the deny path
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.startAccess( startAccessMessage );
        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerEndAccess() throws EndAccessException, Exception {
        ContextHandler contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, STATUS.START.name() ) );
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.endAccess( endAccessMessage );
        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerFullFlow() throws StartAccessException, EndAccessException, Exception {
        ContextHandler contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        /* tryAccess */
        TryAccessMessage tryAccessMessage = buildTryAccessMessage( testProperties.getPepId(), properties.getCore().getUri(), policy,
            request );
        contextHandler.tryAccess( tryAccessMessage );

        /* startAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, STATUS.TRY.name() ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.startAccess( startAccessMessage );

        /* reevaluate */
        contextHandler.reevaluate( getMockedSessionInterface( policy, request, STATUS.START.name() ) );

        AttributeChangeMessage PipChmessage = buildPipChMessage( testProperties.getSessionId(), "a", "a" );
        PipChmessage.addAttribute( getNewAttribute( "virus", Category.ENVIRONMENT, DataType.INTEGER, "1" ) );
        contextHandler.attributeChanged( PipChmessage );

        /* endAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, STATUS.START.name() ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.endAccess( endAccessMessage );

        contextHandler.stopMonitoringThread();
    }

}
