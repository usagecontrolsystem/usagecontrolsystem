package it.cnr.iit.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

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
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.usagecontrolframework.properties.UCFProperties;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;

@SpringBootTest
@DirtiesContext( classMode = ClassMode.BEFORE_EACH_TEST_METHOD )
@EnableAutoConfiguration
@ComponentScan( basePackages = { "it.cnr.iit" } )
@ContextConfiguration( classes = { UCFProperties.class, TestProperties.class } )
@RunWith( SpringRunner.class )
public class RequestManagerCoverageTests extends UCFBaseTests {

    @Autowired
    private UCSProperties properties;

    @Test( expected = NullPointerException.class )
    public void requestManagerCoverageTestShouldFail()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( null );
        requestManager.sendMessage( null );
        requestManager.sendReevaluation( null );
    }

    @Test
    public void requestManagerCoverageTestFull()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( properties );
        Message message = new Message( "a", "a", "a" );
        requestManager.setInterfaces( getMockedContextHandlerInterface(),
            getMockedPEPMap( "a", "" ) );

        testRequestManager( requestManager );
        message.setUCSDestination();
        testRequestManager( requestManager );
    }

    public void testRequestManager( RequestManagerLC requestManager ) throws URISyntaxException, IOException {
        Message[] messages = {
            buildTryAccessResponse( "a", "a", "a" ),
            buildStartAccessResponse( "a", "a", "a" ),
            buildEndAccessResponse( "a", "a", "a" ),
            buildPipChMessage( "a", "a", "a" ),
            buildTryAccessMessage( "a", "a", "a", "a" ),
            buildStartAccessMessage( "a", "a", "a" ),
            buildEndAccessMessage( "a", "a", "a" ),
            buildReevaluationMessage( "a", "a", "a" )
        };

        for( Message message : messages ) {
            requestManager.sendMessage( message );
        }
    }

}
