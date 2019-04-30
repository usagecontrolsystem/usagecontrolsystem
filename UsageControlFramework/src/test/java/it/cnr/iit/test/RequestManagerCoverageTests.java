package it.cnr.iit.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.usagecontrolframework.configuration.UCFProperties;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;

@ActiveProfiles( "test" )
@SpringBootTest
@RunWith( SpringRunner.class )
public class RequestManagerCoverageTests extends UCFBaseTests {

    @Autowired
    private UCFProperties ucfProp;

    private UCSConfiguration ucsConfiguration;

    @PostConstruct
    private void init() throws URISyntaxException, IOException, JAXBException {
        log.info( "Init tests" );
        ucsConfiguration = getUCSConfiguration( conf.getUcsConfigFile() );
    }

    @Before
    public void setUp() {
        log.info( "setUp >>>>>>>>>>>>>>>>>>" );
        // nothing to do for now
    }

    @Test( expected = NullPointerException.class )
    public void requestManagerCoverageTestShouldFail()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( null, null );
        requestManager.sendMessageToCH( null );
        requestManager.sendMessageToOutside( null );
    }

    @Test( expected = NullPointerException.class )
    public void requestManagerCoverageTest()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( ucfProp, ucsConfiguration );
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
        RequestManagerLC requestManager = getRequestManager( ucfProp, ucsConfiguration );
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
            buildTryAccessResponse( "a", "a", "a" ),
            buildStartAccessResponse( "a", "a", "a" ),
            buildEndAccessResponse( "a", "a", "a" ),
            buildReevaluationResponse( "a", "a", "a" ),
            buildPipChMessage( "a", "a", "a" ),
            buildTryAccessMessage( "a", "a", "a", "a" ),
            buildStartAccessMessage( "a", "a", "a" ),
            buildEndAccessMessage( "a", "a", "a" ),
            buildReevaluationMessage( "a", "a", "a" )
        };

        for( Message message : messages ) {
            requestManager.sendMessageToCH( message );
            requestManager.sendMessageToOutside( message );
        }
    }

}
