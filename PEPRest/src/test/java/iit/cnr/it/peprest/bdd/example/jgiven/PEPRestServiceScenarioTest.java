package iit.cnr.it.peprest.bdd.example.jgiven;

import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;

import iit.cnr.it.peprest.bdd.example.jgiven.states.GivenNodes;
import iit.cnr.it.peprest.bdd.example.jgiven.states.ThenMessage;
import iit.cnr.it.peprest.bdd.example.jgiven.states.WhenTransmit;

public class PEPRestServiceScenarioTest extends ScenarioTest<GivenNodes, WhenTransmit, ThenMessage> {

	@Test
	public void we_can_transmit_a_message_from_device_to_UCS_ten_units(){
	    given().an_origin_node("Device")
	            .and().destination_node("UCS");

	    when().we_declare_the_nodes()
	            .and().insert_message_content(10.0f);

	    then().the_resulting_message_should_be_valid_for_transmission_from_Device_to_UCS();
	}

}
