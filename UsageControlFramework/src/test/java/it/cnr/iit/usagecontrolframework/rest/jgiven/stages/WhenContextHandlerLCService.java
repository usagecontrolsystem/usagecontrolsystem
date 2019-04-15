package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.usagecontrolframework.entry.UsageControlFramework;
import it.cnr.iit.usagecontrolframework.rest.UCSRestOperation;

@JGivenStage
public class WhenContextHandlerLCService extends Stage<WhenContextHandlerLCService> {

    @ProvidedScenarioState
    UsageControlFramework usageControlFramework;

    @ExpectedScenarioState
    UCSConfiguration ucsConfiguration;

    @ExpectedScenarioState
    Message message;

    public WhenContextHandlerLCService() {
        usageControlFramework = new UsageControlFramework();
    }

    public WhenContextHandlerLCService context_handler_service_$_is_executed( UCSRestOperation restOperation ) {
        assertNotNull( usageControlFramework );
        performOperation( restOperation );
        return self();
    }

    private void performOperation( UCSRestOperation restOperation ) {
        switch( restOperation ) {
            case TRY_ACCESS:
                assertNotNull( message );
                usageControlFramework.tryAccess( (TryAccessMessage) message );
                break;
            default:
                fail( "Unknown restOperation" );
                break;
        }
    }
}
