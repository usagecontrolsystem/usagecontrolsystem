package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.sessionmanagerdesktop.SessionManagerDesktop;
import it.cnr.iit.ucs.constants.RestOperation;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucsinterface.message.MEAN;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import it.cnr.iit.usagecontrolframework.rest.UCFTestContext;

@JGivenStage
public class GivenMessage extends Stage<GivenMessage> {

    private String policy;
    private String request;

    @Autowired
    UCSProperties properties;

    @Autowired
    UCFTestContext testContext;

    @ProvidedScenarioState
    Message message;

    @ProvidedScenarioState
    SessionManagerDesktop sessionManager;

    @ExpectedScenarioState
    String sessionId;

    PepProperties pepProps;

    @BeforeScenario
    public void init() throws URISyntaxException, IOException, JAXBException {

        policy = readResourceFileAsString( testContext.getPolicyFile() );
        request = readResourceFileAsString( testContext.getRequestFile() );

        pepProps = properties.getPepList().get( Integer.parseInt( testContext.getPepId() ) );

        startSessionManager();
    }

    private void startSessionManager() {
        try {
            sessionManager = new SessionManagerDesktop( properties.getSessionManager() );
        } catch( Exception e ) {
            e.printStackTrace();
        }
        sessionManager.start();
    }

    @AfterScenario
    public void tear() {
        sessionManager.stop();
    }

    public GivenMessage a_$_request( @Quoted RestOperation restOperation ) {
        assertNotNull( pepProps );
        switch( restOperation ) {
            case TRY_ACCESS:
                message = buildTryAccessMessage();
                break;
            case START_ACCESS:
                message = buildStartAccessMessage();
                break;
            default:
                fail( "Unknown Message Type to build." );
                break;
        }
        return self();
    }

    public StartAccessMessage buildStartAccessMessage() {
        assertNotNull( sessionId );
        StartAccessMessage startAccessMessage = new StartAccessMessage( pepProps.getId(), pepProps.getBaseUri() );
        startAccessMessage.setSessionId( sessionId );
        startAccessMessage.setCallback( buildResponseInterface( pepProps, "startAccessResponse" ), MEAN.REST );
        return startAccessMessage;
    }

    private TryAccessMessage buildTryAccessMessage() {
        TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder( pepProps.getId(), pepProps.getBaseUri() );
        tryAccessBuilder.setPepUri( buildOnGoingEvaluationInterface( pepProps ) )
            .setPolicy( policy ).setRequest( request );

        TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
        tryAccessMessage.setCallback( buildResponseInterface( pepProps, "tryAccessResponse" ), MEAN.REST );
        return tryAccessMessage;
    }

    private final String buildResponseInterface( PepProperties pepProps, String name ) {
        StringBuilder response = new StringBuilder();
        response.append( pepProps.getBaseUri() );
        if( !( pepProps.getBaseUri().endsWith( "/" ) || name.startsWith( "/" ) ) ) {
            response.append( "/" );
        }
        response.append( name );
        return response.toString();
    }

    private String buildOnGoingEvaluationInterface( PepProperties pepProps ) {
        return buildResponseInterface( pepProps, "onGoingEvaluation" );
    }

    protected String readResourceFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }
}
