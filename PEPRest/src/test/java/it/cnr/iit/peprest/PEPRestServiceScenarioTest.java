package it.cnr.iit.peprest;

import static it.cnr.iit.peprest.PEPRestOperation.END_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.END_ACCESS_RESPONSE;
import static it.cnr.iit.peprest.PEPRestOperation.ON_GOING_RESPONSE;
import static it.cnr.iit.peprest.PEPRestOperation.START_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.START_ACCESS_RESPONSE;
import static it.cnr.iit.peprest.PEPRestOperation.TRY_ACCESS;
import static it.cnr.iit.peprest.PEPRestOperation.TRY_ACCESS_RESPONSE;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringScenarioTest;

import it.cnr.iit.peprest.integration.PEPRestTestContext;
import it.cnr.iit.peprest.jgiven.stages.GivenContextHandlerRestSimulator;
import it.cnr.iit.peprest.jgiven.stages.GivenMessage;
import it.cnr.iit.peprest.jgiven.stages.ThenMessage;
import it.cnr.iit.peprest.jgiven.stages.WhenPEPRestService;
import it.cnr.iit.usagecontrolframework.rest.jgiven.rules.UCSRestJGivenConfiguration;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@RunWith( DataProviderRunner.class )
@SpringBootTest( classes = { MockServletContext.class, PEPRestTestContext.class } )
@WebAppConfiguration
//@JGivenConfiguration( PEPRestJGivenConfiguration.class )
@JGivenConfiguration( UCSRestJGivenConfiguration.class )
public class PEPRestServiceScenarioTest
        extends SpringScenarioTest<GivenContextHandlerRestSimulator, WhenPEPRestService, ThenMessage> {

    @ScenarioStage
    GivenMessage givenMessage;

    @DataProvider
    public static Object[][] dataPepRestOperations() {
        return new Object[][] {
            { TRY_ACCESS },
            { START_ACCESS },
            { END_ACCESS },
        };
    }

    @DataProvider
    public static Object[][] dataPepRestResponseOperations() {
        return new Object[][] {
            { TRY_ACCESS_RESPONSE },
            { START_ACCESS_RESPONSE },
            { END_ACCESS_RESPONSE },
        };
    }

    @Test
    @UseDataProvider( "dataPepRestOperations" )
    public void an_access_message_can_be_delivered_to_UCS( PEPRestOperation restOperation ) {
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( restOperation.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().PEPRest_service_$_is_executed( restOperation );

        then().the_$_message_is_put_in_the_unanswered_queue( restOperation )
            .and().the_message_id_in_the_unanswered_queue_matches_the_one_sent()
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( restOperation.getOperationUri() );
    }

    @Test
    @UseDataProvider( "dataPepRestOperations" )
    public void ignore_access_message_delivered_to_UCS_if_fault_response_is_received( PEPRestOperation restOperation ) {
        given().a_test_configuration_for_request_with_policy()
            .with().a_test_session_id()
            .and().a_mocked_context_handler_for_$( restOperation.getOperationUri() )
            .with().a_fault_response();

        when().PEPRest_service_$_execution_fails( restOperation );

        then().an_illegal_access_exception_is_thrown()
            .and().the_Message_is_not_placed_into_the_unanswered_queue()
            .but().the_asynch_post_request_for_$_was_received_by_context_handler( restOperation.getOperationUri() );
    }

    @Test
    public void a_response_message_for_an_on_going_evalutaion_can_be_delivered_to_UCS() {
        givenMessage.given().a_ReevaluationResponse_request_with_$_decision( DecisionType.PERMIT );
        given().and().a_test_configuration_for_request_with_policy()
            .and().a_mocked_context_handler_for_$( END_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().PEPRest_service_$_is_executed( ON_GOING_RESPONSE );

        then().the_$_message_is_put_in_the_unanswered_queue( END_ACCESS )
            .and().the_Message_motivation_is_OK()
            .and().the_message_id_in_the_unanswered_queue_matches_the_one_sent()
            .and().the_asynch_post_request_for_$_was_received_by_context_handler( END_ACCESS.getOperationUri() );
    }

    @Test
    public void a_response_message_fails_to_be_sent_to_context_handler_is_ignored() {
        givenMessage.given().a_ReevaluationResponse_request_with_$_decision( DecisionType.PERMIT );
        given().and().a_test_configuration_for_request_with_policy()
            .and().a_mocked_context_handler_for_$( END_ACCESS.getOperationUri() )
            .with().a_fault_response();

        when().PEPRest_service_$_execution_fails( ON_GOING_RESPONSE );

        then().the_Message_motivation_is_NOT_OK()
            .and().the_Message_is_not_placed_into_the_unanswered_queue()
            .but().the_asynch_post_request_for_$_was_received_by_context_handler( END_ACCESS.getOperationUri() );
    }

    @Test
    @UseDataProvider( "dataPepRestResponseOperations" )
    public void a_response_is_delivered_from_UCS( PEPRestOperation restOperation ) {

        givenMessage.given().create_permit_response_for_$( restOperation );
        given().and().a_mocked_context_handler_for_$( START_ACCESS.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().PEPRest_service_receive_response_is_executed( restOperation );

        then().the_message_is_put_in_the_responses_queue()
            .and().the_session_id_is_not_null( restOperation, givenMessage.getMessageId() )
            .and().the_evaluation_result_is_permit( givenMessage.getMessageId() );
    }

}
