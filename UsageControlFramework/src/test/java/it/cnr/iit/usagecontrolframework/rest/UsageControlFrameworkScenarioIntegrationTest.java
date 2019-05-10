package it.cnr.iit.usagecontrolframework.rest;

import static it.cnr.iit.ucs.constants.RestOperation.END_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.END_ACCESS_RESPONSE;
import static it.cnr.iit.ucs.constants.RestOperation.START_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.START_ACCESS_RESPONSE;
import static it.cnr.iit.ucs.constants.RestOperation.TRY_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.TRY_ACCESS_RESPONSE;
import static it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants.START_STATUS;
import static it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants.TRY_STATUS;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import it.cnr.iit.ucs.constants.OperationNames;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.GivenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.GivenPEPRestSimulator;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.ThenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.WhenUCFRestController;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@SpringBootTest( classes = { MockServletContext.class, UCFTestContext.class } )
public class UsageControlFrameworkScenarioIntegrationTest
        extends SpringRuleScenarioTest<GivenMessage, WhenUCFRestController, ThenMessage> {

    private static final String DECISION_PERMIT = DecisionType.PERMIT.value();

    @ScenarioStage
    GivenPEPRestSimulator givenPEPRestSimulator;

    @Test
    public void a_tryAccess_request_sends_PEP_tryAccessResponse_containg_Permit_decision() {
        given().a_$_request( TRY_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( OperationNames.TRYACCESSRESPONSE_REST )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( TRY_ACCESS.getOperationUri() );

        then().an_entry_for_session_with_status_$_is_persisted( TRY_STATUS )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                OperationNames.TRYACCESSRESPONSE_REST, DECISION_PERMIT );
    }

    @Test
    public void a_startAccess_request_preceeding_tryAccess_with_Permit_sends_PEP_startAccessResponse_containing_Permit_decision() {

        a_tryAccess_request_sends_PEP_tryAccessResponse_containg_Permit_decision();

        given().a_$_request( START_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( START_ACCESS_RESPONSE.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( START_ACCESS.getOperationUri() );

        then().the_session_entry_status_is_updated_to_$( START_STATUS )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                START_ACCESS_RESPONSE.getOperationUri(), DECISION_PERMIT );
    }

    @Test
    public void an_endAccess_request_preceeding_startAccess_sends_PEP_endAccessResponse_and_the_session_is_deleted() {

        a_startAccess_request_preceeding_tryAccess_with_Permit_sends_PEP_startAccessResponse_containing_Permit_decision();

        given().a_$_request( END_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( END_ACCESS_RESPONSE.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( END_ACCESS.getOperationUri() );

        then().the_session_entry_is_deleted()
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                END_ACCESS_RESPONSE.getOperationUri(), DECISION_PERMIT );
    }

    @Test
    public void a_tryAccess_request_sends_PEP_tryAccessResponse_containg_Deny_decision() {
        given().a_policy_that_will_trigger_a_Deny_response()
            .and().a_$_request( TRY_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( TRY_ACCESS_RESPONSE.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( TRY_ACCESS.getOperationUri() );

        then().no_entry_for_session_with_status_$_is_persisted( TRY_STATUS )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                TRY_ACCESS_RESPONSE.getOperationUri(), DecisionType.DENY.toValue() );
    }
}
