package iit.cnr.it.peprest.bdd.example.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioState.Resolution;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.bdd.example.jgiven.Message;

public class ThenMessage extends Stage<ThenMessage> {

	@ExpectedScenarioState
	Message message;

	@ExpectedScenarioState
    PEPRest pepRest;

	@ExpectedScenarioState
	String tryAccessMsgId;

	@ExpectedScenarioState( resolution = Resolution.NAME )
	WireMock wireMockContextHandler;

	public void the_resulting_message_should_be_valid_for_transmission_from_Device_to_UCS() {
	    assertNotNull(message);
	    assertTrue(message.isOriginNode("Device"));
	    assertTrue(message.isDestinationNode("UCS"));
	}

	public ThenMessage message_is_put_in_unanswered_queue() {
		assertNotNull(pepRest.getUnanswered());
		assertTrue(pepRest.getUnanswered().size()>0);
		return self();
	}

	public ThenMessage message_id_in_unanswered_queue_matches_the_sent_one() {
		assertNotNull(pepRest.getUnanswered());
		assertNotNull(pepRest.getUnanswered().get(tryAccessMsgId));
		return self();
	}

	@SuppressWarnings("static-access")
	public ThenMessage mocked_context_handler_is_called() {
		wireMockContextHandler.verify(postRequestedFor(urlEqualTo("/tryAccess"))
		        .withHeader("Content-Type", equalTo("application/json")));
		return self();
	}
}
