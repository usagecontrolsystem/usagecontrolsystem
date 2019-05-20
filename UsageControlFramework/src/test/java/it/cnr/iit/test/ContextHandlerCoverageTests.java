package it.cnr.iit.test;

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

import it.cnr.iit.test.properties.TestProperties;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.properties.UCFProperties;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@SpringBootTest
@DirtiesContext( classMode = ClassMode.BEFORE_EACH_TEST_METHOD )
@EnableAutoConfiguration
@ComponentScan( basePackages = { "it.cnr.iit" } )
@ContextConfiguration( classes = { UCFProperties.class, TestProperties.class } )
@RunWith( SpringRunner.class )
public class ContextHandlerCoverageTests extends UCFBaseTests {

    @Autowired
    private UCSProperties properties;

    private String policy;
    private String request;

    @PostConstruct
    private void init() throws URISyntaxException, IOException, JAXBException {
        log.info( "Init tests " );
        policy = Utility.readFileAsString( testProperties.getPolicyFile() );
        request = Utility.readFileAsString( testProperties.getRequestFile() );
    }

    @Test( expected = PreconditionException.class )
    public void contextHandlerConfigurationShouldFail() throws PreconditionException {
        ContextHandlerLC contextHandler = getContextHandler( properties );
        contextHandler.verify();
        contextHandler.startMonitoringThread();
        contextHandler.stopMonitoringThread();
    }

    @Test( expected = PreconditionException.class )
    public void contextHandlerTryAccessShouldFail() throws PreconditionException {
        ContextHandlerLC contextHandler = getContextHandler( properties );
        initContextHandler( contextHandler );
        // set the pdp response to return deny
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        contextHandler.verify();
        contextHandler.startMonitoringThread();

        /* tryAccess */
        contextHandler.tryAccess( null );

        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerStartAccess() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        /* startAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, ContextHandlerConstants.TRY_STATUS ) );
        // this line makes the start access to take the deny path
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.startAccess( startAccessMessage );

        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerEndAccess() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        /* endAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, ContextHandlerConstants.START_STATUS ) );
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.endAccess( endAccessMessage );

        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerFullFlow() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( properties, policy, request );

        /* tryAccess */
        TryAccessMessage tryAccessMessage = buildTryAccessMessage( testProperties.getPepId(), properties.getGeneral().getBaseUri(), policy,
            request );
        contextHandler.tryAccess( tryAccessMessage );

        /* startAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, ContextHandlerConstants.TRY_STATUS ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.startAccess( startAccessMessage );

        /* reevaluate */
        ReevaluationMessage reevaluationMessage = buildReevaluationMessage( testProperties.getSessionId(), "a", "a" );
        reevaluationMessage.setSession( getMockedSessionInterface( policy, request, ContextHandlerConstants.START_STATUS ) );
        contextHandler.reevaluate( reevaluationMessage );

        PipChMessage messagePipCh = buildPipChMessage( testProperties.getSessionId(), "a", "a" );
        messagePipCh.addAttribute( getNewAttribute( "virus", Category.ENVIRONMENT, DataType.INTEGER, "1" ) );
        contextHandler.attributeChanged( messagePipCh );

        /* endAccess */
        contextHandler.setSessionManager(
            getSessionManagerForStatus( testProperties.getSessionId(), policy, request, ContextHandlerConstants.START_STATUS ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( testProperties.getSessionId(), "a", "a" );
        contextHandler.endAccess( endAccessMessage );

        contextHandler.stopMonitoringThread();
    }

}
