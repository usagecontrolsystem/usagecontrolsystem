package it.cnr.iit.peprest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.peprest.configuration.UCSProperties;
import it.cnr.iit.peprest.jgiven.rules.MockedHttpServiceTestRule;

@JGivenStage
public class GivenContextHandlerRestSimulator extends Stage<GivenContextHandlerRestSimulator> {

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule();

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

    @ProvidedScenarioState
    @Autowired
    UCSProperties ucs;

    @ProvidedScenarioState
    String sessionId;

    private ResponseDefinitionBuilder aResponse;
    private MappingBuilder post;

    @BeforeScenario
    public void init() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        // nothing
    }

    private Optional<URI> getUCSUri() {
        URI uri;
        try {
            return Optional.of( new URI( ucs.getBaseUri() ) );
        } catch( URISyntaxException e ) {

        }
        return Optional.empty();
    }

    public GivenContextHandlerRestSimulator a_test_configuration_for_request_with_policy() {
        loadConfiguration();
        return self();
    }

    public GivenContextHandlerRestSimulator a_mocked_context_handler_for_$( @Quoted String operationUri ) {
        URI uri = getUCSUri().get();

        wireMockContextHandler = new WireMock( uri.getHost(), uri.getPort() );
        post = post( urlPathMatching( operationUri ) );
        return self();
    }

    public GivenContextHandlerRestSimulator a_success_response_status_code_of_$( @Quoted int status ) {
        assertNotNull( "context handler is not initialised", post );
        aResponse = aResponse()
            .withStatus( status )
            .withHeader( "Content-Type", "application/json" );
        wireMockContextHandler.register( post.willReturn( aResponse ) );
        return self();
    }

    public GivenContextHandlerRestSimulator a_fault_response() {
        assertNotNull( "context handler is not initialised", post );
        aResponse = aResponse().withFault( Fault.RANDOM_DATA_THEN_CLOSE );
        wireMockContextHandler.register( post.willReturn( aResponse ) );
        return self();
    }

    public GivenContextHandlerRestSimulator a_test_session_id() {
        sessionId = UUID.randomUUID().toString();
        return self();
    }
}