package iit.cnr.it.peprest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PDPResponse;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponseContent;
import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml.core.schema.wd_17.ResultType;

@WebMvcTest(value = PEPRestCommunication.class, secure = false)
public class PEPRestCommunicationTest extends PEPRestAbstractTest {

	private static final String SESSION_ID_01 = "SessionId_01";
	private static final String HOST="localhost:";	
	private static final String PORT="8081";

	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
			options().port(Integer.parseInt(PORT))

	// Set the root of the filesystem WireMock will look under for files and mappings
	//		.usingFilesUnderDirectory("D:\\git")

	// Set a path within the classpath as the filesystem root
	//		.usingFilesUnderClasspath("src/test/resources")
			 );
	@MockBean
	private PEPRest pepRest;

	
   @Override
   @Before
   public void setUp() {
      super.setUp();
   }

   @Test
   public void wireMockGetExampleTest() throws ClientProtocolException, IOException {
	   stubFor(get(urlPathMatching("/ucsinterface/.*"))
			   .willReturn(aResponse()
			   .withStatus(200)
			   .withHeader("Content-Type", "application/json")
			   .withBody("\"testing-library\": \"WireMock\"")));
      
       	HttpResponse httpResponse = sendGetRequestAndReceiveResponse();
       	String stringResponse = convertHttpResponseToString(httpResponse);
  
       	verify(getRequestedFor(urlEqualTo("/ucsinterface/wiremock")));
       	assertEquals(200, httpResponse.getStatusLine().getStatusCode());
       	assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
       	assertEquals("\"testing-library\": \"WireMock\"", stringResponse);
   }

   @Test
   public void wireMockPostExampleTest() throws ClientProtocolException, IOException {
	   stubFor(post(urlPathMatching("/ucsinterface/.*"))
			   .willReturn(aResponse()
			   .withStatus(200)
			   .withHeader("Content-Type", "application/json")
			   .withBody("\"testing-library\": \"WireMock\"")));
      
       	HttpResponse httpResponse = sendPostRequestAndReceiveResponse();
       	String stringResponse = convertHttpResponseToString(httpResponse);
  
       	verify(postRequestedFor(urlEqualTo("/ucsinterface/wiremock")));
       	assertEquals(200, httpResponse.getStatusLine().getStatusCode());
       	assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
       	assertEquals("\"testing-library\": \"WireMock\"", stringResponse);
   }
   
   @Test
   public void wireMockPostExampleWithBodyFileTest() throws ClientProtocolException, IOException {
	   stubFor(post(urlPathMatching("/ucsinterface/.*"))
			   .willReturn(aResponse()
			   .withStatus(200)
			   .withHeader("Content-Type", "application/json")
			   .withBodyFile("/wiremock_response.json")	));
	   
       	HttpResponse httpResponse = sendPostRequestAndReceiveResponse();
       	String stringResponse = convertHttpResponseToString(httpResponse);
  
       	verify(postRequestedFor(urlPathMatching("/ucsinterface/wiremock"))
       		  .withHeader("Content-Type", equalTo("application/json")));
       	assertEquals(200, httpResponse.getStatusLine().getStatusCode());
       	String expectedResponse = "{\r\n" + 
       			"    \"testing-library\": \"WireMock\"\r\n" + 
       			"}";
		assertEquals(expectedResponse, 
    		   convertResponseToString(new Scanner(stringResponse)));
   }
   
	private HttpResponse sendGetRequestAndReceiveResponse() throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet("http://"+HOST+PORT+"/ucsinterface/wiremock");
		request.addHeader("Content-Type", "application/json");
		HttpResponse httpResponse = httpClient.execute(request);
		return httpResponse;
	}
	private HttpResponse sendPostRequestAndReceiveResponse() throws IOException, ClientProtocolException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost request = new HttpPost("http://"+HOST+PORT+"/ucsinterface/wiremock");
		request.addHeader("Content-Type", "application/json");
		HttpResponse httpResponse = httpClient.execute(request);
		return httpResponse;
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

	@Test
	public void performHealthCheck() throws Exception {
	   String uri = "/isAlive";
	   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
	      .accept(MediaType.ALL_VALUE)).andReturn();
	   
	   int status = mvcResult.getResponse().getStatus();
	   assertEquals(200, status);
	   String content = mvcResult.getResponse().getContentAsString();
	   assertNotNull(content);
	}
	
	//@Test
	public void tryAccessResponseWithDeny() throws Exception {
		doCallRealMethod().when(pepRest).receiveResponse(Mockito.any(Message.class));
		
		Message message = new Message("source", "destination", SESSION_ID_01);
		message.setPurpose(PURPOSE.TRYACCESS);
		String jsonMessage = new Gson().toJson(message);

	   String uri = "/tryAccessResponse";
	   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
			   .contentType(MediaType.TEXT_PLAIN_VALUE).content(jsonMessage)).andReturn();
	   
	   int status = mvcResult.getResponse().getStatus();
	   assertEquals(200, status);
	   String content = mvcResult.getResponse().getContentAsString();
	   assertNotNull(content);
	}
	
	@Test
	public void performSendWithDenyResponse() throws Exception {
		doCallRealMethod().when(pepRest).run();

		when(pepRest.tryAccess()).thenReturn(SESSION_ID_01);
		
		TryAccessResponse tryAccessResponse = buildTryAccessResponseDeny();		
		when(pepRest.waitForResponse(SESSION_ID_01)).thenReturn(tryAccessResponse);		
		
		when(pepRest.startAccess(anyString())).thenReturn(SESSION_ID_01);

	   String uri = "/send";
	   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
	   
	   int status = mvcResult.getResponse().getStatus();
	   assertEquals(200, status);
	   String content = mvcResult.getResponse().getContentAsString();
	   assertNotNull(content);
	}

	@Test
	public void finish() throws Exception {
		doCallRealMethod().when(pepRest).end(SESSION_ID_01);
		when(pepRest.endAccess(SESSION_ID_01)).thenReturn(SESSION_ID_01);
		when(pepRest.waitForResponse(SESSION_ID_01)).thenReturn(new EndAccessResponse(SESSION_ID_01));

	   String uri = "/finish";
	   MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
			   .contentType(MediaType.TEXT_PLAIN_VALUE).content(SESSION_ID_01)).andReturn();
	   
	   int status = mvcResult.getResponse().getStatus();
	   assertEquals(200, status);
	   String content = mvcResult.getResponse().getContentAsString();
	   assertNotNull(content);
	}
	
	private TryAccessResponse buildTryAccessResponseDeny() {
		PDPResponse pdpEvaluation = buildPDPResponse(DecisionType.DENY);
		TryAccessResponseContent content = new TryAccessResponseContent();
		content.setPDPEvaluation(pdpEvaluation);
		TryAccessResponse tryAccessResponse = new TryAccessResponse(SESSION_ID_01);
		tryAccessResponse.setContent(content);
		return tryAccessResponse;
	}

	@SuppressWarnings("unused")
	private StartAccessResponse buildStartAccessResponsePermit() {
		PDPResponse pdpEvaluation = buildPDPResponse(DecisionType.PERMIT);
		StartAccessResponse startAccessResponse = new StartAccessResponse(SESSION_ID_01);
		startAccessResponse.setResponse(pdpEvaluation);
		return startAccessResponse;
	}
	
	private PDPResponse buildPDPResponse(DecisionType decision) {
		ResultType resultType = new ResultType();
		resultType.setDecision(decision);		
		ResponseType responseType = new ResponseType();
		responseType.setResult(Arrays.asList(resultType));
		PDPResponse pdpResponse = new PDPResponse();
		pdpResponse.setResponseType(responseType);
		pdpResponse.setInitialized(true);
		return pdpResponse;
	}
}
