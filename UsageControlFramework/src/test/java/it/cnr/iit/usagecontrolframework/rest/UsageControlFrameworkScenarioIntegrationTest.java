package it.cnr.iit.usagecontrolframework.rest;

import static it.cnr.iit.usagecontrolframework.rest.UCFRestOperation.TRY_ACCESS;
import static it.cnr.iit.usagecontrolframework.rest.UCFRestOperation.TRY_ACCESS_RESPONSE;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import it.cnr.iit.usagecontrolframework.rest.jgiven.rules.UCFRestJGivenConfiguration;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.GivenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.GivenPEPRestSimulator;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.ThenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.WhenUCFRestController;

@SpringBootTest( classes = { MockServletContext.class, UCFTestContext.class } )
@WebAppConfiguration
@JGivenConfiguration( UCFRestJGivenConfiguration.class )
public class UsageControlFrameworkScenarioIntegrationTest
        extends SpringRuleScenarioTest<GivenMessage, WhenUCFRestController, ThenMessage> {

    @ScenarioStage
    GivenPEPRestSimulator givenPEPRestSimulator;

    @Test
    public void a_tryAccess_request_is_replied_with_PERMIT() {
        given().a_TryAccess_request();
        givenPEPRestSimulator.and().a_mocked_PEPRest_for_$( TRY_ACCESS_RESPONSE.getOperationUri() )
            .and().a_success_response_status_code_of_$( HttpStatus.SC_OK );

        when().the_UCF_is_executed_for_$( TRY_ACCESS.getOperationUri() );

        then().the_asynch_post_request_for_$_was_received_by_PEPRest( TRY_ACCESS_RESPONSE.getOperationUri() );
    }
}
