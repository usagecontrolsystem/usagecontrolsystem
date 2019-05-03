package it.cnr.iit.usagecontrolframework.rest;

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
    public void a_tryAccess_request_is_replied_with_tryAccessResponse_containg_Permit_decision() {
        given().a_$_request( TRY_ACCESS );
        givenPEPRestSimulator.and().a_mocked_PEPRest_for_$( TRY_ACCESS_RESPONSE.getOperationUri() )
            .and().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( TRY_ACCESS.getOperationUri() );

        then().an_entry_for_session_with_status_$_is_persisted( TRY_STATUS )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                TRY_ACCESS_RESPONSE.getOperationUri(), DECISION_PERMIT );
    }

    @Test
    public void a_startAccess_request_preceeding_tryAccess_with_Permit_is_replied_with_startAccessResponse_containing_Permit_decision() {

        a_tryAccess_request_is_replied_with_tryAccessResponse_containg_Permit_decision();

        given().a_$_request( START_ACCESS );
        givenPEPRestSimulator.and().a_mocked_PEPRest_for_$( START_ACCESS_RESPONSE.getOperationUri() )
            .and().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( START_ACCESS.getOperationUri() );

        then().the_session_entry_status_is_updated_to_$( START_STATUS )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                START_ACCESS_RESPONSE.getOperationUri(), DECISION_PERMIT );
    }
}
