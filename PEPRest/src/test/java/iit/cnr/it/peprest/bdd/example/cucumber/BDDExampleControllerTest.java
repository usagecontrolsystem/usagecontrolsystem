package iit.cnr.it.peprest.bdd.example.cucumber;

import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class BDDExampleControllerTest extends BDDTestStepDefinitions {

	@Autowired
	BDDExampleController msgController;

	@Given("^that I have a message to send")
	public void i_have_message_to_send() {
		assertEquals("message to send", msgController.poke());
	}

	@When("^I trigger deliver$")
	public void when_i_call_my_message_sender() {
		msgController.sendMsg();
	}

	@Then("^I receive nothing more to send$")
	public void i_receive_no_more_messages_to_send()  {
		assertEquals("nothing more to send", msgController.poke());
	}

}
