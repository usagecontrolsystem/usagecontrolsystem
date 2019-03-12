package iit.cnr.it.peprest.integration;

import static iit.cnr.it.peprest.PEPRestOperation.END_ACCESS;
import static iit.cnr.it.peprest.PEPRestOperation.ON_GOING_RESPONSE;
import static iit.cnr.it.peprest.PEPRestOperation.START_ACCESS;
import static iit.cnr.it.peprest.PEPRestOperation.START_ACCESS_RESPONSE;
import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS;
import static iit.cnr.it.peprest.PEPRestOperation.TRY_ACCESS_RESPONSE;
import static iit.cnr.it.peprest.messagetrack.STATUS.STARTACCESS_PERMIT;
import static iit.cnr.it.peprest.messagetrack.STATUS.TRYACCESS_PERMIT;
import static iit.cnr.it.peprest.messagetrack.STATUS.TRYACCESS_SENT;

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
    public void a_startEvaluation_flow_with_try_and_start_access_messages_in_status_permit_succeeds() {
        // step 1 - post to PEP a startEvaluation request
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( TRY_ACCESS.getOperationUri() )
            .with().a_success_response_status_$( HttpStatus.SC_OK );

        when().the_PEP_startEvaluation_is_executed();

        then().a_$_message_is_sent_to_context_handler( TRY_ACCESS )
            .and().a_message_id_is_returned()
            .and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( TRY_ACCESS.getOperationUri() );

        // step 1.1 - check tryAccess message status
        when().the_PEP_messageStatus_for_tryAccess_is_executed();
        then().a_message_body_is_returned()
            .and().the_message_body_has_$_status( TRYACCESS_SENT );

        // step 2 - post to PEP a TryAccessResponse with permit
        givenMessage.given().a_TryAccessResponse_request_with_$_decision( DecisionType.PERMIT );
        given().and().a_mocked_context_handler_for_$( START_ACCESS.getOperationUri() )
            .with().a_success_response_status_$( HttpStatus.SC_OK );

        when().the_PEP_receiveResponse_is_executed_for_$( TRY_ACCESS_RESPONSE.getOperationUri() );

        then().a_$_message_is_sent_to_context_handler( START_ACCESS )
            .and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( START_ACCESS.getOperationUri() );

        // step 2.1 - check tryAccess message status is updated
        when().the_PEP_messageStatus_for_tryAccess_is_executed();
        then().a_message_body_is_returned()
            .and().the_message_body_has_$_status( TRYACCESS_PERMIT );

        // step 2.1 - check #of messages in the session (at this stage 2 = 1 for tryAccess, 1 for startAccess)
        when().the_PEP_messagesPerSession_is_executed();
        then().a_message_body_is_returned()
            .and().the_message_body_has_the_$_sent_message_Ids( 2 );

        // step 3 - post to PEP StartAccessResponse with permit
        givenMessage.given().a_StartAccessResponse_request_with_$_decision( DecisionType.PERMIT )
            .with().an_associated_messageId( 1 );

        when().the_PEP_receiveResponse_is_executed_for_$( START_ACCESS_RESPONSE.getOperationUri() );

        then().a_message_body_is_returned()
            // TODO: .and().the_message_body_contains_$_decision( DecisionType.PERMIT );
            .and().the_message_body_has_$_status( STARTACCESS_PERMIT );

        // step 4 - post to PEP ReevaluationResponse with permit
        givenMessage.given().a_ReevaluationResponse_request_with_$_decision( DecisionType.PERMIT )
            .with().an_associated_messageId( 2 );
        given().and().a_mocked_context_handler_for_$( END_ACCESS.getOperationUri() )
            .with().a_success_response_status_$( HttpStatus.SC_OK );

        when().the_PEP_receiveResponse_is_executed_for_$( ON_GOING_RESPONSE.getOperationUri() );

        then().a_message_body_is_returned();
        // TODO: .and().the_message_body_has_$_status( STATUS.ONGOINGACCESS_PERMIT );
        then().a_$_message_is_sent_to_context_handler( END_ACCESS )
            .and().the_asynch_HTTP_POST_request_for_$_was_received_by_context_handler( END_ACCESS.getOperationUri() );
    }
}
