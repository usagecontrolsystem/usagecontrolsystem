package iit.cnr.it.peprest.integration;


import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import iit.cnr.it.peprest.jgiven.rules.PEPRestJGivenConfiguration;
import iit.cnr.it.peprest.jgiven.stages.GivenContextHandlerRestSimulator;
import iit.cnr.it.peprest.jgiven.stages.GivenMessage;
import iit.cnr.it.peprest.jgiven.stages.ThenMessage;
import iit.cnr.it.peprest.jgiven.stages.WhenPEPRestCommunication;

@SpringBootTest(classes = { MockServletContext.class, PEPRestTestContext.class })
@WebAppConfiguration
@JGivenConfiguration( PEPRestJGivenConfiguration.class )
public class PEPRestServiceScenarioIntegrationTest
	extends SpringRuleScenarioTest<GivenContextHandlerRestSimulator, WhenPEPRestCommunication, ThenMessage> {

	@ScenarioStage
	GivenMessage givenMessage;

    @Test
	public void try_access_flow(){
	    given().a_test_configuration_for_request_with_policy()
	    	.with().a_test_session_id()
	    	.and().a_mocked_context_handler_for_$(TRY_ACCESS.getOperationUri())
	    	.with().a_success_response_status_$(HttpStatus.SC_OK);

	    when().start_evaluation_is_executed();

	    then().a_message_id_is_received()
//	    	.and().the_message_id_in_the_unanswered_queue_matches_the_one_sent()
	    	.and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler(TRY_ACCESS.getOperationUri());
	}
}
