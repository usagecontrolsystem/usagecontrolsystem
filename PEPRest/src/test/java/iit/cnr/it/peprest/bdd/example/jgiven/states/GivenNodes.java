package iit.cnr.it.peprest.bdd.example.jgiven.states;

import static org.mockito.Matchers.any;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.proxy.ProxyRequestManager;

public class GivenNodes extends Stage<GivenNodes> {

    @ProvidedScenarioState
    String originNode;
    @ProvidedScenarioState
    String destinationNode;
    
    @Mock
    private ProxyRequestManager proxyRequestManager;
    
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
}