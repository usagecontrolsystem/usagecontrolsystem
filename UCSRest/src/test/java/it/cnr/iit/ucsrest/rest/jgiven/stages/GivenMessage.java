package it.cnr.iit.ucsrest.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.RestOperation;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.sessionmanager.SessionManager;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.ucsrest.rest.UCSRestTestContext;
import it.cnr.iit.utility.FileUtility;

@JGivenStage
public class GivenMessage extends Stage<GivenMessage> {

    private String policy;
    private String request;

    @Autowired
    UCSProperties properties;

    @Autowired
    UCSRestTestContext testContext;

    @ProvidedScenarioState
    Message message;

    @ProvidedScenarioState
    SessionManagerInterface sessionManager;

    @ExpectedScenarioState
    String sessionId;

    PepProperties pepProps;

    String monitoredResource;

    @BeforeScenario
    public void init() throws URISyntaxException, IOException {
        policy = FileUtility.readFileAsString( testContext.getPolicyFile() );
        request = FileUtility.readFileAsString( testContext.getRequestFile() );

        pepProps = properties.getPepList().get( Integer.parseInt( testContext.getPepId() ) );

        startSessionManager();
    }

    private void startSessionManager() {
        try {
            sessionManager = new SessionManager( properties.getSessionManager() );
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
            case END_ACCESS:
                message = buildEndAccessMessage();
                break;
            default:
                fail( "Unknown Message Type to build." );
                break;
        }
        return self();
    }

    public GivenMessage a_policy_that_will_trigger_a_Deny_response() {
        policy = FileUtility.readFileAsString( testContext.getPolicyFileDeny() );
        return self();
    }

    private Message buildEndAccessMessage() {
        assertNotNull( sessionId );
        EndAccessMessage endAccessMessage = new EndAccessMessage( pepProps.getId(), pepProps.getUri() );
        endAccessMessage.setSessionId( sessionId );
        endAccessMessage.setCallback( buildResponseInterface( pepProps, "endAccessResponse" ), CONNECTION.REST );
        return endAccessMessage;
    }

    private StartAccessMessage buildStartAccessMessage() {
        assertNotNull( sessionId );
        StartAccessMessage startAccessMessage = new StartAccessMessage( pepProps.getId(), pepProps.getUri() );
        startAccessMessage.setSessionId( sessionId );
        startAccessMessage.setCallback( buildResponseInterface( pepProps, "startAccessResponse" ), CONNECTION.REST );
        return startAccessMessage;
    }

    private TryAccessMessage buildTryAccessMessage() {
        TryAccessMessage tryAccessMessage = new TryAccessMessage( pepProps.getId(), pepProps.getUri() );
        tryAccessMessage.setPepUri( buildOnGoingEvaluationInterface( pepProps ) );
        tryAccessMessage.setPolicy( policy );
        tryAccessMessage.setRequest( request );
        tryAccessMessage.setCallback( buildResponseInterface( pepProps, "tryAccessResponse" ), CONNECTION.REST );
        return tryAccessMessage;
    }

    private final String buildResponseInterface( PepProperties pepProps, String name ) {
        StringBuilder response = new StringBuilder();
        response.append( pepProps.getUri() );
        if( !( pepProps.getUri().endsWith( "/" ) || name.startsWith( "/" ) ) ) {
            response.append( "/" );
        }
        response.append( name );
        return response.toString();
    }

    private String buildOnGoingEvaluationInterface( PepProperties pepProps ) {
        return buildResponseInterface( pepProps, "onGoingEvaluation" );
    }

}
