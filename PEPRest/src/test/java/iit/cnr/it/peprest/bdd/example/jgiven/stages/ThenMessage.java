package iit.cnr.it.peprest.bdd.example.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.bdd.example.jgiven.Message;
import iit.cnr.it.peprest.configuration.Configuration;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;

public class ThenMessage extends Stage<ThenMessage> {

	@ExpectedScenarioState
	Message message;

	@ExpectedScenarioState
    PEPRest pepRest;

	@ExpectedScenarioState
	String tryAccessMsgId;

	@ExpectedScenarioState
	WireMock wireMockContextHandler;

	@ExpectedScenarioState
    Configuration configuration;

	public void the_resulting_message_should_be_valid_for_transmission_from_Device_to_UCS() {
	    assertNotNull(message);
	    assertTrue(message.isOriginNode("Device"));
	    assertTrue(message.isDestinationNode("UCS"));
	}

	public ThenMessage a_tryAccessMessage_is_put_in_the_unanswered_queue() {
		assertNotNull( pepRest.getUnanswered() );
		assertTrue( pepRest.getUnanswered().size() > 0 );
		assertTrue(	pepRest.getUnanswered().entrySet().stream().findFirst().get().getValue()
				instanceof TryAccessMessage );
		return self();
	}

	public ThenMessage the_message_id_in_the_unanswered_queue_matches_the_sent_one() {
		assertNotNull( pepRest.getUnanswered() );
		assertNotNull( pepRest.getUnanswered().get( tryAccessMsgId ) );
		return self();
	}

	public ThenMessage the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( @Quoted String operation) {
		wireMockContextHandler.verifyThat(postRequestedFor(urlEqualTo("/"+operation))
		        .withHeader("Content-Type", equalTo("application/json")));
		return self();
	}
}
