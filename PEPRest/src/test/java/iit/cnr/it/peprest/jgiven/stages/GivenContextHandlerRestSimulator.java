package iit.cnr.it.peprest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioRule;

import iit.cnr.it.peprest.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.utility.Utility;

public class GivenContextHandlerRestSimulator extends Stage<GivenContextHandlerRestSimulator> {

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( getPort() );

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

    @ProvidedScenarioState
    Configuration configuration;

    @ProvidedScenarioState
    String sessionId;

	private ResponseDefinitionBuilder aResponse;
	private MappingBuilder post;

    @BeforeScenario
    public void init() {
    	loadConfiguration();
    }

	private void loadConfiguration() {
		if (configuration == null){
			configuration = Utility.retrieveConfiguration("conf.xml", Configuration.class);
		}
	}

	private String getHost(){
		if (configuration == null){
			loadConfiguration();
		}
		return configuration.getRMConf().getIp();
	}

    private int getPort() {
		if (configuration == null){
			loadConfiguration();
		}
		return Integer.parseInt(configuration.getRMConf().getPort());
	}

    public GivenContextHandlerRestSimulator a_test_configuration_for_request_with_policy() {
		loadConfiguration();
		return self();
    }

    public GivenContextHandlerRestSimulator a_mocked_context_handler_for_$( @Quoted String operationUri ) {
    	wireMockContextHandler = new WireMock(getHost(), getPort());
		post = post(urlPathMatching(operationUri));
    	return self();
    }

    public GivenContextHandlerRestSimulator a_success_response_status_$( @Quoted int status ) {
    	assertNotNull("context handler is not initialised", post);
    	aResponse = aResponse()
    			.withStatus(status)
    			.withHeader("Content-Type", "application/json");
    	wireMockContextHandler.register( post.willReturn(aResponse));
    	return self();
    }

    public GivenContextHandlerRestSimulator a_fault_response( ) {
    	assertNotNull("context handler is not initialised", post);
    	aResponse = aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE);
    	wireMockContextHandler.register( post.willReturn(aResponse));
    	return self();
    }

    public GivenContextHandlerRestSimulator a_test_session_id() {
    	sessionId = UUID.randomUUID().toString();
    	return self();
    }
}