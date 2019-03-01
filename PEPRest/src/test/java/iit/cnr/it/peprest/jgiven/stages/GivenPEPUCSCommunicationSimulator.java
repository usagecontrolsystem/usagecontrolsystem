package iit.cnr.it.peprest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.apache.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioRule;

import iit.cnr.it.peprest.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.utility.Utility;

public class GivenPEPUCSCommunicationSimulator extends Stage<GivenPEPUCSCommunicationSimulator> {

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( getPEPPort() );

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

    @ProvidedScenarioState
    Configuration configuration;

    @ProvidedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Message message;

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

	private String getPEPHost(){
		if (configuration == null){
			loadConfiguration();
		}
		return configuration.getPepConf().getIp();
	}

    private int getPEPPort() {
		if (configuration == null){
			loadConfiguration();
		}
		return Integer.parseInt(configuration.getPepConf().getPort());
	}

    public GivenPEPUCSCommunicationSimulator a_test_configuration_for_request_with_policy() {
		loadConfiguration();
		return self();
    }

    public GivenPEPUCSCommunicationSimulator a_mocked_PEPUCSCommunication_REST_service_for_$( @Quoted String operationUri ) {
    	wireMockContextHandler = new WireMock(getPEPHost(), getPEPPort());
		post = post(urlPathMatching(operationUri));
    	return self();
    }

    public GivenPEPUCSCommunicationSimulator a_successful_try_access_response( ) {
    	assertNotNull("context handler is not initialised", post);
    	assertTrue(message instanceof TryAccessResponse);
    	try {
			aResponse = aResponse()
					.withStatus(HttpStatus.SC_OK)
					.withHeader("Content-Type", "application/json")
					.withBody(
							 new ObjectMapper().writeValueAsString(message) ); //TODO
		} catch (JsonProcessingException e) {
			fail("unable to create a response due to "+e.getLocalizedMessage());
		}
    	wireMockContextHandler.register( post.willReturn(aResponse));
    	return self();
    }

    public GivenPEPUCSCommunicationSimulator a_fault_response( ) {
    	assertNotNull("context handler is not initialised", post);
    	aResponse = aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE);
    	wireMockContextHandler.register( post.willReturn(aResponse));
    	return self();
    }

    public GivenPEPUCSCommunicationSimulator a_test_session_id() {
    	sessionId = UUID.randomUUID().toString();
    	return self();
    }
}