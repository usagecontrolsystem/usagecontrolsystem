package iit.cnr.it.peprest.bdd.example.jgiven;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.tngtech.jgiven.junit.ScenarioTest;

import iit.cnr.it.peprest.bdd.example.jgiven.stages.GivenNodes;
import iit.cnr.it.peprest.bdd.example.jgiven.stages.ThenMessage;
import iit.cnr.it.peprest.bdd.example.jgiven.stages.WhenTransmit;

public class PEPRestServiceScenarioTest extends ScenarioTest<GivenNodes, WhenTransmit, ThenMessage> {

	private static final String TRY_ACCESS = "tryAccess";
	private static final String START_ACCESS = "startAccess";
	private static final String END_ACCESS = "endAccess";
	
	@Test
	public void we_can_transmit_a_message_with_ten_units_from_device_to_UCS(){
	    given().an_origin_node("Device")
	            .and().destination_node("UCS");

	    when().we_declare_the_nodes()
	            .and().insert_message_content(10.0f);

	    then().the_resulting_message_should_be_valid_for_transmission_from_Device_to_UCS();
	}

	@Test
	public void a_tryAccess_message_can_be_prepared_and_proxied_to_UCS(){
	    given().a_test_configuration_for_request_with_policy()
	            .and().a_mocked_proxy_request_manager();

	    when().PEPRest_service_tryAccess_is_executed();

	    then().a_tryAccessMessage_is_put_in_the_unanswered_queue()
	    	.and().the_message_id_in_the_unanswered_queue_matches_the_sent_one();
	}

	@Test
	public void a_tryAccess_message_can_be_delivered_to_UCS(){

	    given().a_test_configuration_for_request_with_policy()
	    	.and().a_mocked_context_handler_for_$(TRY_ACCESS)
	    	.with().a_success_response_status_$(HttpStatus.SC_OK);

	    when().PEPRest_service_tryAccess_is_executed();

	    then().a_tryAccessMessage_is_put_in_the_unanswered_queue()
	    	.and().the_message_id_in_the_unanswered_queue_matches_the_sent_one()
	    	.and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler(TRY_ACCESS);
	}
	
	@Test
	public void a_startAccess_message_can_be_delivered_to_UCS(){

	    given().a_test_session_id()
	    	.and().a_mocked_context_handler_for_$(START_ACCESS)
	    	.with().a_success_response_status_$(HttpStatus.SC_OK);

	    when().PEPRest_service_startAccess_is_executed();

	    then().a_startAccessMessage_is_put_in_the_unanswered_queue()
	    	.and().the_message_id_in_the_unanswered_queue_matches_the_sent_one()
	    	.and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler(START_ACCESS);
	}
	
	@Test
	public void a_endAccess_message_can_be_delivered_to_UCS(){

	    given().a_test_session_id()
	    	.and().a_mocked_context_handler_for_$(END_ACCESS)
	    	.with().a_success_response_status_$(HttpStatus.SC_OK);

	    when().PEPRest_service_endAccess_is_executed();

	    then().a_endAccessMessage_is_put_in_the_unanswered_queue()
	    	.and().the_message_id_in_the_unanswered_queue_matches_the_sent_one()
	    	.and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler(END_ACCESS);
	}
}
