package it.cnr.iit.ucsrest.rest;

import static it.cnr.iit.ucs.constants.RestOperation.END_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.END_ACCESS_RESPONSE;
import static it.cnr.iit.ucs.constants.RestOperation.START_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.START_ACCESS_RESPONSE;
import static it.cnr.iit.ucs.constants.RestOperation.TRY_ACCESS;
import static it.cnr.iit.ucs.constants.RestOperation.TRY_ACCESS_RESPONSE;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import it.cnr.iit.ucs.constants.OperationName;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucsrest.rest.jgiven.stages.GivenMessage;
import it.cnr.iit.ucsrest.rest.jgiven.stages.GivenPEPRestSimulator;
import it.cnr.iit.ucsrest.rest.jgiven.stages.ThenMessage;
import it.cnr.iit.ucsrest.rest.jgiven.stages.WhenUCSRestController;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@EnableAutoConfiguration
@TestPropertySource( properties = "application.properties" )
@EnableConfigurationProperties
@ContextConfiguration( classes = { UCSRestTestContext.class } )
@ComponentScan( basePackages = { "it.cnr.iit" } )
@SpringBootTest( classes = { MockServletContext.class } )
public class UCSRestScenarioIntegrationTest
        extends SpringRuleScenarioTest<GivenMessage, WhenUCSRestController, ThenMessage> {

    private static final String DECISION_PERMIT = DecisionType.PERMIT.value();

    @ScenarioStage
    GivenPEPRestSimulator givenPEPRestSimulator;

    @Test
    public void a_tryAccess_request_sends_PEP_tryAccessResponse_containg_Permit_decision() {
        given().a_$_request( TRY_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( OperationName.TRYACCESSRESPONSE_REST )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( TRY_ACCESS.getOperationUri() );

        then().an_entry_for_session_with_status_$_is_persisted( STATUS.TRY.name() )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                OperationName.TRYACCESSRESPONSE_REST, DECISION_PERMIT );
    }

    @Test
    public void a_startAccess_request_preceeding_tryAccess_with_Permit_sends_PEP_startAccessResponse_containing_Permit_decision() {
        a_tryAccess_request_sends_PEP_tryAccessResponse_containg_Permit_decision();

        given().a_$_request( START_ACCESS );
        givenPEPRestSimulator
            .and().a_mocked_PEPRest_listening_on_$( START_ACCESS_RESPONSE.getOperationUri() )
            .with().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( START_ACCESS.getOperationUri() );

        then().the_session_entry_status_is_updated_to_$( STATUS.START.name() )
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

        then().no_entry_for_session_with_status_$_is_persisted( STATUS.TRY.name() )
            .and().the_asynch_post_request_for_$_with_decision_$_was_received_by_PEPRest(
                TRY_ACCESS_RESPONSE.getOperationUri(), DecisionType.DENY.toValue() );
    }
}
