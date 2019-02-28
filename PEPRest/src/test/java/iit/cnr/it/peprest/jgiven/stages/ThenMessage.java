package iit.cnr.it.peprest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.PEPRestServiceScenarioTest.PEPRestOperation;
import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;

public class ThenMessage extends Stage<ThenMessage> {

	@ExpectedScenarioState
    PEPRest pepRest;

	@ExpectedScenarioState
	String messageId;

	@ExpectedScenarioState
	WireMock wireMockContextHandler;

	@ExpectedScenarioState
    Configuration configuration;

    @ExpectedScenarioState
    Message message;

    @ExpectedScenarioState
    Exception expectedException;
    
	public ThenMessage the_$_message_is_put_in_the_unanswered_queue(PEPRestOperation restOperation) {
		assertNotNull( pepRest.getUnanswered() );
		assertTrue( pepRest.getUnanswered().size() > 0 );
		Message messageType = pepRest.getUnanswered().entrySet().stream().findFirst().get().getValue();
        switch (restOperation) {
		case TRY_ACCESS:
			assertTrue(	messageType instanceof TryAccessMessage );
			break;
		case START_ACCESS:
			assertTrue(	messageType instanceof StartAccessMessage );
			break;
		case END_ACCESS:
			assertTrue(	messageType instanceof EndAccessMessage );
			break;
		default:
			fail("Unknown message type in unanswered map");
			break;
		}
		return self();
	}

	public ThenMessage the_message_id_in_the_unanswered_queue_matches_the_one_sent() {
		assertNotNull( pepRest.getUnanswered() );
		assertNotNull( pepRest.getUnanswered().get( messageId ) );
		return self();
	}

	public ThenMessage the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( @Quoted String operation ) {
		wireMockContextHandler.verifyThat( postRequestedFor( urlEqualTo(operation) )
		        .withHeader( "Content-Type", equalTo("application/json")) );
		return self();
	}

	public ThenMessage the_Message_is_not_placed_into_the_unanswered_queue() {
		assertNotNull( pepRest.getUnanswered() );
		assertTrue( pepRest.getUnanswered().size() < 1 );
		return self();
	}
	
	public ThenMessage an_illegal_access_exception_is_thrown() {
		assertNotNull( expectedException );
		assertTrue( expectedException.getMessage().contains("IllegalAccessException"));
		return self();
	}
	
	public ThenMessage the_Message_motivation_is_NOT_OK() {
		assertNotNull( message );
		assertNull(message.getMotivation());
		return self();
	}
	
	public ThenMessage the_Message_motivation_is_OK() {
		assertNotNull( message );
		assertEquals("OK", message.getMotivation());
		return self();
	}

	public ThenMessage the_message_is_put_in_the_responses_queue() {
		assertNotNull(pepRest.getResponses());
		assertTrue(pepRest.getResponses().size() > 0);
		return self();
	}

	public ThenMessage the_session_id_is_not_null(PEPRestOperation restOperation, String messageId) {
		if(restOperation == PEPRestOperation.TRY_ACCESS_RESPONSE) {
			assertNotNull(pepRest.getSessionIdInTryAccess(message.getID()).get());
			assertTrue(pepRest.getSessionIdInTryAccess(messageId).get().length() > 0);
		}
		return self();
	}

	public void the_evaluation_result_is_permit(String messageId) {
		assertNotNull(pepRest.getEvaluationResult(messageId).get());
		assertTrue(pepRest.getEvaluationResult(messageId).get().length() > 0);
		assertTrue(pepRest.getEvaluationResult(messageId).get().equals("Permit"));
	}
	
}
