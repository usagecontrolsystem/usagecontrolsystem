package it.cnr.iit.peprest.integration;

import static it.cnr.iit.peprest.PEPRestOperation.END_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.ON_GOING_RESPONSE;
import static it.cnr.iit.peprest.PEPRestOperation.START_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.START_ACCESS_RESPONSE;
import static it.cnr.iit.peprest.PEPRestOperation.TRY_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.TRY_ACCESS_RESPONSE;
import static it.cnr.iit.peprest.messagetrack.STATUS.ENDACCESS_SENT;
import static it.cnr.iit.peprest.messagetrack.STATUS.STARTACCESS_PERMIT;
import static it.cnr.iit.peprest.messagetrack.STATUS.STARTACCESS_SENT;
import static it.cnr.iit.peprest.messagetrack.STATUS.TRYACCESS_DENY;
import static it.cnr.iit.peprest.messagetrack.STATUS.TRYACCESS_SENT;
import static oasis.names.tc.xacml.core.schema.wd_17.DecisionType.DENY;
import static oasis.names.tc.xacml.core.schema.wd_17.DecisionType.PERMIT;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import it.cnr.iit.peprest.jgiven.stages.GivenContextHandlerRestSimulator;
import it.cnr.iit.peprest.jgiven.stages.GivenMessage;
import it.cnr.iit.peprest.jgiven.stages.ThenMessage;
import it.cnr.iit.peprest.jgiven.stages.WhenPEPRestCommunication;
import it.cnr.iit.ucs.testing.jgiven.rules.UCSRestJGivenConfiguration;

@SpringBootTest( classes = { MockServletContext.class, PEPRestTestContext.class } )
@WebAppConfiguration
@DirtiesContext( classMode = ClassMode.BEFORE_EACH_TEST_METHOD )
@JGivenConfiguration( UCSRestJGivenConfiguration.class )
public class PEPRestServiceScenarioIntegrationTest
        extends SpringRuleScenarioTest<GivenContextHandlerRestSimulator, WhenPEPRestCommunication, ThenMessage> {

    @ScenarioStage
    GivenMessage givenMessage;

    @Test
    public void a_startEvaluation_flow_with_try_and_start_access_messages_in_status_permit_succeeds_but_reevaluation_with_deny_ends_access() {
        // step 1 - post to PEP a startEvaluation request
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( TRY_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_PEP_startEvaluation_is_executed();

        then().a_$_message_is_sent_to_context_handler( TRY_ACCESS )
            .and().a_message_id_is_returned()
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( TRY_ACCESS.getOperationUri() )
            .and().the_message_is_in_$_status( TRYACCESS_SENT );

        // step 2 - post to PEP a TryAccessResponse with permit
        givenMessage.given().a_TryAccessResponse_request_with_$_decision( PERMIT );
        given().and().a_mocked_context_handler_for_$( START_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_PEP_receiveResponse_is_executed_for_$( TRY_ACCESS_RESPONSE.getOperationUri() );

        then().a_$_message_is_sent_to_context_handler( START_ACCESS )
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( START_ACCESS.getOperationUri() )
            .and().the_message_is_in_$_status( STARTACCESS_SENT );

        // step 3 - post to PEP StartAccessResponse with permit
        givenMessage.given().a_StartAccessResponse_request_with_$_decision( PERMIT )
            .with().an_associated_messageId( 1 );

        when().the_PEP_receiveResponse_is_executed_for_$( START_ACCESS_RESPONSE.getOperationUri() );

        then().the_message_is_in_$_status( STARTACCESS_PERMIT );

        // step 4 - post to PEP ReevaluationResponse with deny
        givenMessage.given().a_ReevaluationResponse_request_with_$_decision( DENY );
        given().and().a_mocked_context_handler_for_$( END_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_PEP_receiveResponse_is_executed_for_$( ON_GOING_RESPONSE.getOperationUri() );

        then().a_$_message_is_sent_to_context_handler( END_ACCESS )
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( END_ACCESS.getOperationUri() )
            .and().the_message_is_in_$_status( ENDACCESS_SENT );
    }

    @Test
    public void a_startEvaluation_flow_ends_when_try_access_response_has_status_deny() {
        // step 1 - post to PEP a startEvaluation request
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( TRY_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_PEP_startEvaluation_is_executed();

        then().a_$_message_is_sent_to_context_handler( TRY_ACCESS )
            .and().a_message_id_is_returned()
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( TRY_ACCESS.getOperationUri() )
            .and().the_message_is_in_$_status( TRYACCESS_SENT );

        // step 2 - post to PEP a TryAccessResponse with deny
        givenMessage.given().a_TryAccessResponse_request_with_$_decision( DENY );
        given().and().a_mocked_context_handler_for_$( START_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_PEP_receiveResponse_is_executed_for_$( TRY_ACCESS_RESPONSE.getOperationUri() );

        then().a_$_message_is_NOT_sent_to_context_handler( START_ACCESS )
            .and().the_asynch_post_request_for_$_is_NOT_received_by_context_handler( START_ACCESS.getOperationUri() )
            .and().the_message_is_in_$_status( TRYACCESS_DENY );
    }
}
