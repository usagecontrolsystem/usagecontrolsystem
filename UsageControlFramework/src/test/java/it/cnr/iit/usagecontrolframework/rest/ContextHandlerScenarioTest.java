package it.cnr.iit.usagecontrolframework.rest;

import static it.cnr.iit.usagecontrolframework.rest.UCSRestOperation.TRY_ACCESS;
import static it.cnr.iit.usagecontrolframework.rest.UCSRestOperation.TRY_ACCESS_RESPONSE;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.web.WebAppConfiguration;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

import it.cnr.iit.usagecontrolframework.rest.jgiven.CHRestJGivenConfiguration;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.GivenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.ThenMessage;
import it.cnr.iit.usagecontrolframework.rest.jgiven.stages.WhenContextHandlerLCService;

@Ignore
@SpringBootTest( classes = { MockServletContext.class, CHRestTestContext.class } )
@WebAppConfiguration
@JGivenConfiguration( CHRestJGivenConfiguration.class )
public class ContextHandlerScenarioTest
        extends SpringRuleScenarioTest<GivenMessage, WhenContextHandlerLCService, ThenMessage> {

    @Test
    public void a_tryAccess_request_is_replied_with_PERMIT() {
        given().a_TryAccess_request();
        when().context_handler_service_$_is_executed( TRY_ACCESS );
        then().the_asynch_post_request_for_$_was_received_by_context_handler( TRY_ACCESS_RESPONSE.getOperationUri() );
    }
}
