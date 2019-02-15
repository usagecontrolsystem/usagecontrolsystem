package iit.cnr.it.peprest.bdd.example.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static iit.cnr.it.peprest.bdd.example.jgiven.PEPRestServiceScenarioTest.PORT;
import static org.mockito.Matchers.any;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioRule;

import iit.cnr.it.peprest.bdd.example.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.peprest.proxy.ProxyRequestManager;

public class GivenNodes extends Stage<GivenNodes> {

    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;

    @Mock
    private ProxyRequestManager proxyRequestManager;

    @ScenarioRule
    MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( PORT );

    @ProvidedScenarioState
    WireMock wireMockContextHandler;

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

    public GivenNodes a_mocked_context_handler_for_tryAccess_at_$_on_port_$(String host, int port) {
    	wireMockContextHandler = new WireMock(host, port);
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