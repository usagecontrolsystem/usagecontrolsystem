package it.cnr.iit.peprest.jgiven.stages;

import static it.cnr.iit.peprest.PEPRestOperation.FLOW_STATUS;
import static it.cnr.iit.peprest.PEPRestOperation.START_EVALUATION;
import static org.junit.Assert.assertNotNull;
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

@JGivenStage
public class WhenPEPRestCommunication extends Stage<WhenPEPRestCommunication> {

    @ProvidedScenarioState
    String messageId;

    @ProvidedScenarioState
    String messageBody;

    @ProvidedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Message message;

    @ProvidedScenarioState
    MockHttpServletResponse mvcResponse;

    @Autowired
    WebApplicationContext webApplicationContext;

    MockMvc mvc;

    @BeforeStage
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup( webApplicationContext ).build();
    }

    public WhenPEPRestCommunication the_PEP_startEvaluation_is_executed() {
        try {
            assertNotNull( mvc );
            mvcResponse = postToPEPRestcommunicationViaMockMvc( START_EVALUATION.getOperationUri() );
            messageId = mvcResponse.getContentAsString();
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    public WhenPEPRestCommunication the_PEP_messageStatus_for_tryAccess_is_executed() {
        try {
            assertNotNull( mvc );
            MockHttpServletResponse mvcResponse = getFromPEPRestcommunication( FLOW_STATUS.getOperationUri(), "messageId",
                messageId );
            messageBody = mvcResponse.getContentAsString();
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    public WhenPEPRestCommunication the_PEP_receiveResponse_is_executed_for_$( @Quoted String operationUri ) {
        try {
            assertNotNull( mvc );
            MockHttpServletResponse mvcResponse = postToPEPUCScommunication( operationUri, message );
            messageBody = mvcResponse.getContentAsString();
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    private MockHttpServletResponse getFromPEPRestcommunication( String uri, String reqAttrName, String attrValue ) throws Exception {
        assertNotNull( uri );
        MvcResult mvcResult = mvc.perform( MockMvcRequestBuilders.get( uri ).requestAttr( reqAttrName, attrValue ) ).andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse postToPEPRestcommunicationViaMockMvc( String uri ) throws Exception {
        assertNotNull( uri );
        MvcResult mvcResult = mvc.perform( MockMvcRequestBuilders.post( uri ) ).andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse postToPEPUCScommunication( String uri, Message jsonMessage ) throws Exception {
        assertNotNull( uri );
        MvcResult mvcResult = mvc.perform( MockMvcRequestBuilders.post( uri )
            .contentType( MediaType.APPLICATION_JSON_VALUE ).content(
                new ObjectMapper().writeValueAsString( jsonMessage ) ) )
            .andReturn();
        return mvcResult.getResponse();
    }
}
