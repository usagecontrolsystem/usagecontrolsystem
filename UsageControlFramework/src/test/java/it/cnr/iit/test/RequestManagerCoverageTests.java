package it.cnr.iit.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import iit.cnr.it.ucsinterface.message.Message;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;

@ActiveProfiles( "test" )
@SpringBootTest
@RunWith( SpringRunner.class )
public class RequestManagerCoverageTests extends UCFBaseTests {
    private Configuration ucsConfiguration;

    @PostConstruct
    private void init() throws JAXBException, URISyntaxException, IOException {
        LOGGER.info( "Init tests" );
        ucsConfiguration = getUCSConfiguration( conf.getUcsConfigFile() );
    }

    @Before
    public void setUp() {
        LOGGER.info( "setUp >>>>>>>>>>>>>>>>>>" );
        // nothing to do for now
    }

    // TODO catch exception when it is refactored to do that
    @Test
    public void requestManagerCoverageTestShouldFail()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( null );
        requestManager.sendMessageToCH( null );
        requestManager.sendMessageToOutside( null );
    }

    @Test
    public void requestManagerCoverageTest()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( ucsConfiguration.getRm() );
        requestManager.setInterfaces( getMockedContextHandlerInterface(),
            getMockedPEPMap( "", "" ),
            getMockedNodeInterface(),
            getMockedForwardingQueueToRMInterface( null ) );

        testRequestManager( requestManager );
    }

    @Test
    public void requestManagerCoverageTest2()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( ucsConfiguration.getRm() );
        Message message = new Message( "", "" );
        requestManager.setInterfaces( getMockedContextHandlerInterface(),
            getMockedPEPMap( "", "" ),
            getMockedNodeInterface(),
            getMockedForwardingQueueToRMInterface( message ) );

        testRequestManager( requestManager );
        message.setDestinationType();
        testRequestManager( requestManager );

    }

    public void testRequestManager( RequestManagerLC requestManager ) throws URISyntaxException, IOException {
        Message[] messages = {
            buildTryAccessResponse( "", "", "" ),
            buildStartAccessResponse( "", "", "" ),
            buildEndAccessResponse( "", "", "" ),
            buildReevaluationResponse( "", "", "" ),
            buildPipChMessage( "", "", "" ),
            buildTryAccessMessage( "", "", "", "" ),
            buildStartAccessMessage( "", "", "" ),
            buildEndAccessMessage( "", "", "" ),
            buildReevaluationMessage( "", "", "" ),
            null
        };

        for( Message message : messages ) {
            requestManager.sendMessageToCH( message );
            requestManager.sendMessageToOutside( message );
        }
    }

}
