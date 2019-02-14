package iit.cnr.it.peprest.bdd.example.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.mockito.Matchers.any;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;

import iit.cnr.it.peprest.bdd.example.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.peprest.proxy.ProxyRequestManager;

public class GivenNodes extends Stage<GivenNodes> {

	private static final String HOST="localhost";
	private static final int PORT= 8080;

    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;

    @Mock
    private ProxyRequestManager proxyRequestManager;

    @ScenarioRule
    public final MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( PORT );

    @ProvidedScenarioState( resolution = Resolution.NAME )
    public WireMock wireMockContextHandler = new WireMock(HOST, PORT);

    @BeforeScenario
    public void init() {
    	MockitoAnnotations.initMocks(this);
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
    	//TODO: read config from src/test/resources
    	return self();
    }

    public GivenNodes a_mocked_proxy_request_manager() {
    	Mockito.when(proxyRequestManager.sendMessageToCH(any())).thenReturn(null);
    	return self();
    }

    public GivenNodes a_mocked_context_handler_for_tryAccess() {
    	wireMockContextHandler.register( post(urlPathMatching("/tryAccess"))
     		   .willReturn(aResponse()
     		   .withStatus(200)
     		   .withHeader("Content-Type", "application/json")));
    	return self();
    }

    public GivenNodes configuration_to_respond_success() {
//    	assertNotNull("context handler is not initialised", willReturn);
//    	responseActions.andExpect(method(HttpMethod.POST));
//    	responseActions.andRespond(withSuccess());
    	return self();
    }

}