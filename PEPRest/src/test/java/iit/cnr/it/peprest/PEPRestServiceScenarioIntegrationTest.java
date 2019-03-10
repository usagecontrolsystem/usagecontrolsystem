package iit.cnr.it.peprest;


import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.ScenarioTest;

import iit.cnr.it.peprest.jgiven.stages.GivenContextHandlerRestSimulator;
import iit.cnr.it.peprest.jgiven.stages.GivenMessage;
import iit.cnr.it.peprest.jgiven.stages.ThenMessage;
import iit.cnr.it.peprest.jgiven.stages.WhenPEPRestCommunication;

//@EnableConfigurationProperties
//@TestPropertySource(properties = "application-test.properties")
//@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
@SpringBootConfiguration
public class PEPRestServiceScenarioIntegrationTest
	extends ScenarioTest<GivenContextHandlerRestSimulator, WhenPEPRestCommunication, ThenMessage> {

	@ScenarioStage
	GivenMessage givenMessage;

	@ProvidedScenarioState
	MockMvc mvc;

	@Autowired
	WebApplicationContext webApplicationContext;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

    @Test
	public void try_access_flow(){
	    given().a_test_configuration_for_request_with_policy()
	    	.with().a_test_session_id()
	    	.and().a_mocked_context_handler_for_$(TRY_ACCESS.getOperationUri())
	    	.with().a_success_response_status_$(HttpStatus.SC_OK);

	    when().start_evaluation_is_executed(mvc);
//	    when().start_evaluation_is_posted();

	    then().a_message_id_is_received()
//	    	.and().the_message_id_in_the_unanswered_queue_matches_the_one_sent()
	    	.and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler(TRY_ACCESS.getOperationUri());

	}
}
