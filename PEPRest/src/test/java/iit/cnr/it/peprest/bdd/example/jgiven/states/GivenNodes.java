package iit.cnr.it.peprest.bdd.example.jgiven.states;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Matchers.any;

import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.proxy.ProxyRequestManager;

public class GivenNodes extends Stage<GivenNodes> {

	private static final String HOST="localhost:";	
	private static final String PORT="8080";
	
    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;
    
    @Mock
    private ProxyRequestManager proxyRequestManager;
    
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
	//		options().port(Integer.parseInt(PORT))
	// Set the root of the filesystem WireMock will look under for files and mappings
	//		.usingFilesUnderDirectory("D:\\git")
	// Set a path within the classpath as the filesystem root
	//		.usingFilesUnderClasspath("src/test/resources")
			 );
    
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
// 	   	ResponseDefinitionBuilder responseDef = aResponse()
//		   .withStatus(200)
//		   .withHeader("Content-Type", "application/json")
//		   .withBody("\"testing-library\": \"WireMock\"");
//		
// 	   	MappingBuilder postMapping = post(urlPathMatching("/tryAccess/"));
//		postMapping.willReturn(responseDef);
//	
//		stubFor(postMapping);
// 	   stubFor(post(urlPathMatching("/tryAccess/.*"))
//			   .willReturn(aResponse()
//			   .withStatus(200)
//			   .withHeader("Content-Type", "application/json")
//			   .withBody("\"testing-library\": \"WireMock\"")));
    	return self();
    }
    
    public GivenNodes configuration_to_respond_success() {
//    	assertNotNull("context handler is not initialised", willReturn);
//    	responseActions.andExpect(method(HttpMethod.POST));
//    	responseActions.andRespond(withSuccess());
    	return self();
    }
    
}