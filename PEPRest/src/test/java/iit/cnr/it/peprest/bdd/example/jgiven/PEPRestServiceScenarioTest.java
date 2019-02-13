package iit.cnr.it.peprest.bdd.example.jgiven;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.tngtech.jgiven.junit.ScenarioTest;

import iit.cnr.it.peprest.bdd.example.jgiven.states.GivenNodes;
import iit.cnr.it.peprest.bdd.example.jgiven.states.ThenMessage;
import iit.cnr.it.peprest.bdd.example.jgiven.states.WhenTransmit;

public class PEPRestServiceScenarioTest extends ScenarioTest<GivenNodes, WhenTransmit, ThenMessage> {
    
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(
	//		options().port(Integer.parseInt(PORT))
	// Set the root of the filesystem WireMock will look under for files and mappings
	//		.usingFilesUnderDirectory("D:\\git")
	// Set a path within the classpath as the filesystem root
	//		.usingFilesUnderClasspath("src/test/resources")
			 );
	
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

	    when().tryAccess_executed();

	    then().message_is_put_in_unanswered_queue()
	    	.and().message_id_in_unanswered_queue_matches_the_sent_one();
	}
	
	@Test
	public void a_tryAccess_message_can_be_delivered_to_UCS(){
 	   stubFor(post(urlPathMatching("/tryAccess"))
			   .willReturn(aResponse()
			   .withStatus(200)
			   .withHeader("Content-Type", "application/json")
			   .withBody("\"testing-library\": \"WireMock\"")));
	    given().a_test_configuration_for_request_with_policy()
	            .and().a_mocked_context_handler_for_tryAccess()
	            .with().configuration_to_respond_success();

	    when().tryAccess_executed();

	    then().message_is_put_in_unanswered_queue()
	    	.and().message_id_in_unanswered_queue_matches_the_sent_one()
	    	.and().mocked_context_handler_is_called();
	}
}
