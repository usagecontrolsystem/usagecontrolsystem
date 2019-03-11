package iit.cnr.it.peprest.integration;

import static iit.cnr.it.peprest.PEPRestOperation.START_ACCESS;
import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS;
import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS_RESPONSE;

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

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@SpringBootTest( classes = { MockServletContext.class, PEPRestTestContext.class } )
@WebAppConfiguration
@JGivenConfiguration( PEPRestJGivenConfiguration.class )
public class PEPRestServiceScenarioIntegrationTest
        extends SpringRuleScenarioTest<GivenContextHandlerRestSimulator, WhenPEPRestCommunication, ThenMessage> {

    @ScenarioStage
    GivenMessage givenMessage;

    @Test
    public void startEvaluation_flow() {
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( TRY_ACCESS.getOperationUri() )
            .with().a_success_response_status_$( HttpStatus.SC_OK );

        when().startEvaluation_is_executed();

        then().a_$_message_is_sent_to_context_handler( TRY_ACCESS )
            .and().a_message_id_is_returned()
            .and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( TRY_ACCESS.getOperationUri() );

        when().messageStatus_for_the_returned_messageId_is_executed();

        then().a_message_body_is_returned();
        // .and().the_message_body_contains_the_message_id_of_the_sent_tryAccess_message();

        givenMessage.given().a_TryAccessResponse_request_with_$_decision( DecisionType.PERMIT );
        given().and().a_mocked_context_handler_for_$( START_ACCESS.getOperationUri() )
            .with().a_success_response_status_$( HttpStatus.SC_OK );

        when().receiveResponse_is_executed_for_$( TRY_ACCESS_RESPONSE.getOperationUri() );

        then().a_$_message_is_sent_to_context_handler( START_ACCESS )
            .and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( START_ACCESS.getOperationUri() );

    }
}
