package it.cnr.iit.ucsrest.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucsrest.properties.UCSRestProperties;
import it.cnr.iit.ucsrest.rest.UCSRestController;

@JGivenStage
public class WhenUCSRestController extends Stage<WhenUCSRestController> {

    @ProvidedScenarioState
    String messageBody;

    @ProvidedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    Message message;

    @ExpectedScenarioState
    UCSRestProperties configuration;

    @ExpectedScenarioState
    String sessionId;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    UCSRestController ucsRestController;

    MockMvc mvc;

    @Before
    @BeforeStage
    public void setUp() {
        mvc = MockMvcBuilders.standaloneSetup( ucsRestController ).build();
    }

    public WhenUCSRestController the_UCF_is_executed_for_$( @Quoted String operationUri ) {
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
            .andExpect( MockMvcResultMatchers.status().isOk() )
            .andReturn();
        assertNull( "POST to " + uri + " failed ", mvcResult.getResolvedException() );
        return mvcResult.getResponse();
    }
}
