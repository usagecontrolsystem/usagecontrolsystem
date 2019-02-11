package iit.cnr.it.peprest.bdd.example.jgiven.states;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.bdd.example.jgiven.Message;

public class ThenMessage extends Stage<ThenMessage> {

	@ExpectedScenarioState
	Message message;

	@ExpectedScenarioState
    PEPRest pepRest;
    
	@ExpectedScenarioState
	String tryAccessMsgId;
	
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
}
