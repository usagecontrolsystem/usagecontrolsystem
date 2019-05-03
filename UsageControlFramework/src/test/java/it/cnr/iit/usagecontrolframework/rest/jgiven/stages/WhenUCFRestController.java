package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.usagecontrolframework.properties.UCFProperties;

@JGivenStage
public class WhenUCFRestController extends Stage<WhenUCFRestController> {

    @ProvidedScenarioState
    String messageBody;

    @ProvidedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    Message message;

    @ExpectedScenarioState
    UCFProperties configuration;

    @ExpectedScenarioState
    String sessionId;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mvc;

    @BeforeStage
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    public WhenUCFRestController the_UCF_is_executed_for_$( @Quoted String operationUri ) {
        try {
            assertNotNull( mvc );
            MockHttpServletResponse mvcResponse = postToUCFRestControllerViaMockMvc( operationUri, message );
            messageBody = mvcResponse.getContentAsString();
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    private MockHttpServletResponse postToUCFRestControllerViaMockMvc( String uri, Message jsonMessage ) throws Exception {
        assertNotNull( uri );
        MvcResult mvcResult = mvc.perform( MockMvcRequestBuilders.post( uri )
            .contentType( MediaType.APPLICATION_JSON_VALUE ).content(
                new ObjectMapper().writeValueAsString( jsonMessage ) ) )
            .andReturn();
        assertNull( "POST to " + uri + " failed ", mvcResult.getResolvedException() );
        return mvcResult.getResponse();
    }
}
