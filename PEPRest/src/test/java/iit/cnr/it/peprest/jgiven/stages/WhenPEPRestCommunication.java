package iit.cnr.it.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.ucsinterface.message.Message;

public class WhenPEPRestCommunication extends Stage<WhenPEPRestCommunication> {

    @ProvidedScenarioState
	String messageId;

    @ProvidedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Message message;

    @ExpectedScenarioState
    Configuration configuration;
    
    @ProvidedScenarioState
    MockHttpServletResponse mvcResponse;

    @ExpectedScenarioState
	MockMvc mvc;
    
	CloseableHttpClient httpClient;

	@ProvidedScenarioState
    HttpResponse httpResponse;
	
	public WhenPEPRestCommunication(  ) {
	    //httpClient = HttpClients.createDefault();
	    //mvc = MockMvcBuilders.standaloneSetup(new Starter()).build();
    }
	
    public WhenPEPRestCommunication start_evaluation_is_executed(@Hidden MockMvc mvc) {
    	try {
    		assertNotNull(mvc);
    		this.mvc = mvc;
    		mvcResponse = postToPEPRestcommunicationViaMockMvc("/startEvaluation");
    		messageId = mvcResponse.getContentAsString();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return self();
	}

	private MockHttpServletResponse postToPEPRestcommunicationViaMockMvc(String uri) throws Exception {
		assertNotNull(uri);
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		return mvcResult.getResponse();
	}
	
    public WhenPEPRestCommunication start_evaluation_is_posted() {
        try {
            assertNotNull(httpClient);
            httpResponse = sendPostRequestAndReceiveResponse("/startEvaluation");
            messageId = convertHttpResponseToString(httpResponse);
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
        return self();
    }
	   
//    private HttpResponse sendGetRequestAndReceiveResponse(String uri) throws IOException, ClientProtocolException {
//        HttpGet request = new HttpGet("http://"+getPEPHost()+":"+getPEPPort()+uri);
//        request.addHeader("Content-Type", "application/json");
//        return httpClient.execute(request);
//    }
    
    private HttpResponse sendPostRequestAndReceiveResponse(String uri) throws IOException, ClientProtocolException {
        HttpPost request = new HttpPost("http://"+getPEPHost()+":"+getPEPPort()+uri);
        request.addHeader("Content-Type", "application/json");
        return httpClient.execute(request);
    }
   
    private String convertHttpResponseToString(HttpResponse httpResponse) throws UnsupportedOperationException, IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertResponseToString(new Scanner(inputStream, "UTF-8"));
    }

    private String convertResponseToString(Scanner scanner) {
        String string = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return string;
    }
    
    private String getPEPHost(){
        return configuration.getPepConf().getIp();
    }

    private String getPEPPort() {
        return configuration.getPepConf().getPort();
    }
}
