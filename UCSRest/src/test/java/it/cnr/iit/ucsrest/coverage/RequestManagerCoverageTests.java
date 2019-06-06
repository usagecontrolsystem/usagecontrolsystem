package it.cnr.iit.ucsrest.coverage;

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

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucsrest.coverage.properties.TestProperties;
import it.cnr.iit.ucsrest.properties.UCSRestProperties;
import it.cnr.iit.ucsrest.requestmanager.RequestManagerLC;

@SpringBootTest
@DirtiesContext( classMode = ClassMode.BEFORE_CLASS )
@EnableAutoConfiguration
@ComponentScan( basePackages = { "it.cnr.iit" } )
@ContextConfiguration( classes = { UCSRestProperties.class, TestProperties.class } )
@RunWith( SpringRunner.class )
public class RequestManagerCoverageTests extends UCSRestBaseTests {

    @Autowired
    private UCSProperties properties;

    @Test( expected = NullPointerException.class )
    public void requestManagerCoverageTestShouldFail()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( null );
        requestManager.sendMessageToCH( null );
        requestManager.sendReevaluation( null );
    }

    @Test
    public void requestManagerCoverageTestFull()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManagerLC requestManager = getRequestManager( properties );
        requestManager.setContextHandler( getMockedContextHandlerInterface() );
        requestManager.setPEPMap( getMockedPEPMap( "a", "" ) );
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
            requestManager.sendMessageToCH( message );
        }
    }

}
