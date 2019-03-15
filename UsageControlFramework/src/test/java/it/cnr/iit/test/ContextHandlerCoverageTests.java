package it.cnr.iit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.ucs.configuration.xmlclasses.UCFConfiguration;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@ActiveProfiles( "test" )
@SpringBootTest
@RunWith( SpringRunner.class )
public class ContextHandlerCoverageTests extends UCFBaseTests {
    private UCFConfiguration ucsConfiguration;
    private String policy;
    private String request;

    @PostConstruct
    private void init() throws URISyntaxException, IOException, JAXBException {
        LOGGER.info( "Init tests " );
        ucsConfiguration = getUCSConfiguration( conf.getUcsConfigFile() );
        policy = readResourceFileAsString( conf.getPolicyFile() );
        request = readResourceFileAsString( conf.getPolicyFile() );
    }

    @Before
    public void setUp() {
        LOGGER.info( "setUp >>>>>>>>>>>>>>>>>>" );
        // nothing to do for now
    }

    @Test
    public void contextHandlerConfigurationShouldFail() throws JAXBException, URISyntaxException, IOException {
        ContextHandlerLC contextHandler = getContextHandler( ucsConfiguration );
        contextHandler.verify();
        assertFalse( contextHandler.startMonitoringThread() );
        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerTryAccessShouldFail() throws JAXBException, URISyntaxException, IOException {
        ContextHandlerLC contextHandler = getContextHandler( ucsConfiguration );
        initContextHandler( contextHandler );
        // set the pdp response to return deny
        contextHandler.setPdpInterface( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        contextHandler.verify();
        assertTrue( contextHandler.startMonitoringThread() );

        /* tryAccess */
        contextHandler.tryAccess( null );

        contextHandler.stopMonitoringThread();
    }

    // @Test(expected = RevokeException.class)
    public void contextHandlerStartAccessShouldFail() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( ucsConfiguration, policy, request );

        /* tryAccess */
        TryAccessMessage tryAccessMessage = buildTryAccessMessage( conf.getPepId(), conf.getUcsUri(), policy, request );
        contextHandler.tryAccess( tryAccessMessage );

        /* startAccess */
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( conf.getSessionId(), policy, request, ContextHandlerInterface.TRY_STATUS ) );
        // this line makes the start access to take the deny path
        contextHandler.setPdpInterface( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( conf.getSessionId(), "", "" );
        contextHandler.startAccess( startAccessMessage );

        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerEndAccessShouldFail() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( ucsConfiguration, policy, request );

        /* tryAccess */
        TryAccessMessage tryAccessMessage = buildTryAccessMessage( conf.getPepId(), conf.getUcsUri(), policy, request );
        contextHandler.tryAccess( tryAccessMessage );

        /* startAccess */
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( conf.getSessionId(), policy, request, ContextHandlerInterface.TRY_STATUS ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( conf.getSessionId(), "", "" );
        contextHandler.startAccess( startAccessMessage );

        /* endAccess */
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( conf.getSessionId(), policy, request, ContextHandlerInterface.START_STATUS ) );
        contextHandler.setPdpInterface( getMockedPDP( getMockedPDPEvaluation( DecisionType.DENY ) ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( conf.getSessionId(), "", "" );
        contextHandler.endAccess( endAccessMessage );

        contextHandler.stopMonitoringThread();
    }

    @Test
    public void contextHandlerFullFlow() throws JAXBException, URISyntaxException, IOException, Exception {
        ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized( ucsConfiguration, policy, request );

        /* tryAccess */
        TryAccessMessage tryAccessMessage = buildTryAccessMessage( conf.getPepId(), conf.getUcsUri(), policy, request );
        contextHandler.tryAccess( tryAccessMessage );

        /* startAccess */
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( conf.getSessionId(), policy, request, ContextHandlerInterface.TRY_STATUS ) );
        StartAccessMessage startAccessMessage = buildStartAccessMessage( conf.getSessionId(), "", "" );
        contextHandler.startAccess( startAccessMessage );

        /* reevaluate */
        ReevaluationMessage reevaluationMessage = buildReevaluationMessage( conf.getSessionId(), "", "" );
        reevaluationMessage.setSession( getMockedSessionInterface( policy, request, ContextHandlerInterface.START_STATUS ) );
        contextHandler.reevaluate( reevaluationMessage );

        MessagePipCh messagePipCh = buildPipChMessage( conf.getSessionId(), "", "" );
        messagePipCh.addAttribute( getNewAttribute( "virus", Category.ENVIRONMENT, DataType.INTEGER, "1" ) );
        contextHandler.attributeChanged( messagePipCh );

        /* endAccess */
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( conf.getSessionId(), policy, request, ContextHandlerInterface.START_STATUS ) );
        EndAccessMessage endAccessMessage = buildEndAccessMessage( conf.getSessionId(), "", "" );
        contextHandler.endAccess( endAccessMessage );

        contextHandler.stopMonitoringThread();
    }

}
