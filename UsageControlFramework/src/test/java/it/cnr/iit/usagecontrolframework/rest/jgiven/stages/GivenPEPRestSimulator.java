package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
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

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.ucs.testing.jgiven.rules.MockedHttpServiceTestRule;
import it.cnr.iit.usagecontrolframework.rest.UCFTestContext;
import it.cnr.iit.utility.JsonUtility;

@JGivenStage
public class GivenPEPRestSimulator extends Stage<GivenPEPRestSimulator> {

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule();

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

    @ProvidedScenarioState
    UCSConfiguration configuration;

    @ProvidedScenarioState
    String sessionId;

    private ResponseDefinitionBuilder aResponse;
    private MappingBuilder post;

    @Autowired
    UCFTestContext conf;

    @BeforeScenario
    public void init() {
        loadConfiguration();
        restSimulatorTestRule.start( getPort() );
    }

    private void loadConfiguration() {
        if( configuration == null ) {
            File confFile = new File( this.getClass().getClassLoader().getResource( "conf.json" ).getFile() );
            Optional<UCSConfiguration> optPEPRestConfiguration = JsonUtility.loadObjectFromJsonFile( confFile,
                UCSConfiguration.class );
            if( optPEPRestConfiguration.isPresent() ) {
                configuration = optPEPRestConfiguration.get();
            }
        }
    }

    private String getHost() {
        if( configuration == null ) {
            loadConfiguration();
        }
        assertNotNull( "PEP address is not declared", configuration.getPepList() );
        assertTrue( "At least one PEP address needs to be declared", !configuration.getPepList().isEmpty() );
        return configuration.getPepList().get( Integer.parseInt( conf.getPepId() ) ).getIp();
    }

    private int getPort() {
        if( configuration == null ) {
            loadConfiguration();
        }
        assertNotNull( "PEP address is not declared", configuration.getPepList() );
        assertTrue( "At least one PEP address needs to be declared", !configuration.getPepList().isEmpty() );
        return Integer.parseInt( configuration.getPepList().get( Integer.parseInt( conf.getPepId() ) ).getPort() );
    }

    public GivenPEPRestSimulator a_test_configuration_for_request_with_policy() {
        loadConfiguration();
        return self();
    }

    public GivenPEPRestSimulator a_mocked_PEPRest_for_$( @Quoted String operationUri ) {
        wireMockContextHandler = new WireMock( getHost(), getPort() );
        post = post( urlPathMatching( operationUri ) );
        return self();
    }

    public GivenPEPRestSimulator a_success_response_status_code_of_$( @Quoted int status ) {
        assertNotNull( "PEP simulator is not initialised", post );
        aResponse = aResponse()
            .withStatus( status )
            .withHeader( "Content-Type", "application/json" );
        wireMockContextHandler.register( post.willReturn( aResponse ) );
        return self();
    }

    public GivenPEPRestSimulator a_fault_response() {
        assertNotNull( "PEP simulator is not initialised", post );
        aResponse = aResponse().withFault( Fault.RANDOM_DATA_THEN_CLOSE );
        wireMockContextHandler.register( post.willReturn( aResponse ) );
        return self();
    }

    public GivenPEPRestSimulator a_test_session_id() {
        sessionId = UUID.randomUUID().toString();
        return self();
    }
}