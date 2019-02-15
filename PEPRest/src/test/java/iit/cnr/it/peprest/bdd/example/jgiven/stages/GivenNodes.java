package iit.cnr.it.peprest.bdd.example.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.Matchers.any;
import static org.junit.Assert.assertNotNull;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioRule;

import iit.cnr.it.peprest.bdd.example.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.peprest.proxy.ProxyRequestManager;
import iit.cnr.it.utility.Utility;

public class GivenNodes extends Stage<GivenNodes> {

    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;

    @Mock
    private ProxyRequestManager proxyRequestManager;

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( getPort() );

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

    @ProvidedScenarioState
    Configuration configuration;
	private ResponseDefinitionBuilder aResponse;
	private MappingBuilder post;

    @BeforeScenario
    public void init() {
    	MockitoAnnotations.initMocks(this);
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

	public GivenNodes an_origin_node(String node) {
        originNode = node;
        return self();
    }

    public GivenNodes destination_node(String node) {
        destinationNode = node;
        return self();
    }

    public GivenNodes a_test_configuration_for_request_with_policy() {
		loadConfiguration();
		return self();
    }

    public GivenNodes a_mocked_proxy_request_manager() {
    	Mockito.when(proxyRequestManager.sendMessageToCH(any())).thenReturn(null);
    	return self();
    }

    public GivenNodes a_mocked_context_handler_for_$( @Quoted String operation ) {
    	wireMockContextHandler = new WireMock(getHost(), getPort());
		post = post(urlPathMatching("/" + operation));
    	return self();
    }

    public GivenNodes a_success_response_status_$( @Quoted int status ) {
    	assertNotNull("context handler is not initialised", post);
    	aResponse = aResponse()
    			.withStatus(status)
    			.withHeader("Content-Type", "application/json");
    	wireMockContextHandler.register( post.willReturn(aResponse));
    	return self();
    }

}