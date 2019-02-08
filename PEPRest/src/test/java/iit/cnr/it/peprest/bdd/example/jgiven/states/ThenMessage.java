package iit.cnr.it.peprest.bdd.example.jgiven.states;

import static org.junit.Assert.*;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import iit.cnr.it.peprest.bdd.example.jgiven.Message;

public class ThenMessage extends Stage<ThenMessage> {

	@ExpectedScenarioState
	Message message;

	public void the_resulting_message_should_be_valid_for_transmission_from_Device_to_UCS() {
	    assertNotNull(message);
	    assertTrue(message.isOriginNode("Device"));
	    assertTrue(message.isDestinationNode("UCS"));
	}
}
