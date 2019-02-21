package iit.cnr.it.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.PEPRest;
import iit.cnr.it.peprest.PEPRestServiceScenarioTest.PEPRestOperation;
import iit.cnr.it.ucsinterface.message.Message;

public class WhenPEPRestService extends Stage<WhenPEPRestService> {

    @ProvidedScenarioState
    PEPRest pepRest;

    @ProvidedScenarioState
	String messageId;

    @ExpectedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Message message;

    public WhenPEPRestService() {
    	 pepRest = new PEPRest();
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
		case ON_GOING_RESPONSE:
			assertNotNull( message );
			message = pepRest.onGoingEvaluation(message);
			messageId = message.getID();
			break;
		default:
			fail("Unknown restOperation");
			break;
		}
        return self();
    }
}
