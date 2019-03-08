package iit.cnr.it.peprest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.annotation.Quoted;

import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.peprest.jgiven.rules.MockedHttpServiceTestRule;
import iit.cnr.it.peprest.messagetrack.CallerResponse;
import iit.cnr.it.utility.Utility;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
@SpringBootConfiguration
public class PEPRestServiceIntegrationTest {

    @Rule
    public MockedHttpServiceTestRule restSimulatorTestRule = new MockedHttpServiceTestRule( getPort() );

    WireMock wireMockContextHandler;
	Configuration configuration;
	MockMvc mvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	private ResponseDefinitionBuilder aResponse;
	private MappingBuilder post;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
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

	@Test
	public void test(){
		a_mocked_context_handler_for_$(TRY_ACCESS.getOperationUri());
    	a_success_response_status_$(HttpStatus.SC_OK);

    	String messageId = start_evaluation_is_executed();
    	assertNotNull(messageId);

    	String message = message_status_is_executed(messageId);
    	assertNotNull(message);

    	try {
			CallerResponse callerResponse = mapFromJson(message, CallerResponse.class);
			assertNotNull(callerResponse.getDerivedMessageId());
			assertEquals(messageId, callerResponse.getDerivedMessageId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

    public String start_evaluation_is_executed() {
    	try {
    		assertNotNull(mvc);
    		MockHttpServletResponse mvcResponse = postToPEPRestcommunication("/startEvaluation");
    		return mvcResponse.getContentAsString();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return null;
	}

	private MockHttpServletResponse postToPEPRestcommunication(String uri) throws Exception {
		assertNotNull(uri);
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
		return mvcResult.getResponse();
	}

    public String message_status_is_executed(String messageId) {
    	try {
    		assertNotNull(mvc);
    		MockHttpServletResponse mvcResponse = getFromPEPRestcommunication("/messageStatus", messageId);
    		return mvcResponse.getContentAsString();
		} catch (Exception e) {
			fail(e.getLocalizedMessage());
		}
		return null;
	}

	private MockHttpServletResponse getFromPEPRestcommunication(String uri, String messageId) throws Exception {
		assertNotNull(uri);
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).requestAttr("messageId", messageId)).andReturn();
		return mvcResult.getResponse();
	}


    public void a_mocked_context_handler_for_$( @Quoted String operationUri ) {
    	wireMockContextHandler = new WireMock(getHost(), getPort());
		post = post(urlPathMatching(operationUri));
    }

    public void a_success_response_status_$( @Quoted int status ) {
    	assertNotNull("context handler is not initialised", post);
    	aResponse = aResponse()
    			.withStatus(status)
    			.withHeader("Content-Type", "application/json");
    	wireMockContextHandler.register( post.willReturn(aResponse));
    }

	protected String mapToJson(Object obj) throws JsonProcessingException {
    	ObjectMapper objectMapper = new ObjectMapper();
    	return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
      throws JsonParseException, JsonMappingException, IOException {

    	ObjectMapper objectMapper = new ObjectMapper();
       return objectMapper.readValue(json, clazz);
    }
}
