package it.cnr.iit.ucscore.coverage;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.requestmanager.RequestManager;
import it.cnr.iit.ucscore.coverage.properties.TestProperties;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;

@SpringBootTest
@DirtiesContext( classMode = ClassMode.BEFORE_CLASS )
@EnableAutoConfiguration
@ComponentScan( basePackages = { "it.cnr.iit" } )
@ContextConfiguration( classes = { TestProperties.class } )
@RunWith( SpringRunner.class )
public class RequestManagerCoverageTests extends UCSRestBaseTests {

    private String policy;
    private String request;

    @PostConstruct
    private void init() throws URISyntaxException, IOException, JAXBException {
        log.info( "Init tests " );
        Thread.interrupted(); // Avoid a nasty exception
        policy = FileUtility.readFileAbsPath( testProperties.getPolicyFile() );
        request = FileUtility.readFileAbsPath( testProperties.getRequestFile() );
    }

    @Test( expected = PreconditionException.class )
    public void requestManagerCoverageTestShouldFail()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManager requestManager = getRequestManager();
        requestManager.sendMessageToCH( null );
        requestManager.sendReevaluation( null );
    }

    @Test
    public void requestManagerCoverageTestFull()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        RequestManager requestManager = getRequestManager();
        requestManager.setContextHandler( getMockedContextHandlerInterface() );
        requestManager.setPEPMap( getMockedPEPMap( "a", "" ) );
        testRequestManager( requestManager );
    }

    public void testRequestManager( RequestManager requestManager ) throws URISyntaxException, IOException {
        Message[] messages = {
            buildTryAccessResponse( "a", "a", "a" ),
            buildStartAccessResponse( "a", "a", "a" ),
            buildEndAccessResponse( "a", "a", "a" ),
            buildPipChMessage( "a", "a", "a" ),
            buildTryAccessMessage( "a", "a", policy, request ),
            buildStartAccessMessage( "a", "a", "a" ),
            buildEndAccessMessage( "a", "a", "a" ),
            buildReevaluationMessage( "a", "a", "a" )
        };

        for( Message message : messages ) {
            requestManager.sendMessageToCH( message );
        }
    }

}
