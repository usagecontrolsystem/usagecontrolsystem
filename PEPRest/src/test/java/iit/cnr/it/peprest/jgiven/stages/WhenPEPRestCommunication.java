package iit.cnr.it.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

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

    @ProvidedScenarioState
    MockHttpServletResponse mvcResponse;

	MockMvc mvc;

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
}
