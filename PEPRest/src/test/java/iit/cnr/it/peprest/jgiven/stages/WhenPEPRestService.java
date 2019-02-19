package iit.cnr.it.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.PEPRestServiceScenarioTest.PEPRestOperation;

public class WhenPEPRestService extends Stage<WhenPEPRestService> {

    @ProvidedScenarioState
    PEPRest pepRest;

    @ProvidedScenarioState
	String tryAccessMsgId;

    @ProvidedScenarioState
	String messageId;

    @ExpectedScenarioState
    String sessionId;

    public WhenPEPRestService() {
    	 pepRest = new PEPRest();
    }

    public WhenPEPRestService PEPRest_service_tryAccess_is_executed() {
        assertNotNull( pepRest );
        tryAccessMsgId = pepRest.tryAccess();
        return self();
    }

    public WhenPEPRestService PEPRest_service_$_is_executed(PEPRestOperation restOperation) {
        assertNotNull( pepRest );
        switch (restOperation) {
		case TRY_ACCESS:
			messageId = pepRest.tryAccess();
			break;
		case START_ACCESS:
			assertNotNull( sessionId );
			messageId = pepRest.startAccess(sessionId);
			break;
		case END_ACCESS:
			assertNotNull( sessionId );
			messageId = pepRest.endAccess(sessionId);
			break;
		default:
			fail("Unknown restOperation");
			break;
		}
        return self();
    }

    public WhenPEPRestService PEPRest_service_startAccess_is_executed() {
        assertNotNull( pepRest );
        assertNotNull( sessionId );
        tryAccessMsgId = pepRest.startAccess(sessionId);
        return self();
    }

    public WhenPEPRestService PEPRest_service_endAccess_is_executed() {
        assertNotNull( pepRest );
        assertNotNull( sessionId );
        tryAccessMsgId = pepRest.endAccess(sessionId);
        return self();
    }
}
