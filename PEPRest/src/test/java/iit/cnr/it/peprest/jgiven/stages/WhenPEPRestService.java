package iit.cnr.it.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.peprest.PEPRest;

public class WhenPEPRestService extends Stage<WhenPEPRestService> {

    @ProvidedScenarioState
    PEPRest pepRest;
    
    @ProvidedScenarioState
	String tryAccessMsgId;    
    
    @ProvidedScenarioState
    String sessionId;
    
    public WhenPEPRestService() {
    	 pepRest = new PEPRest();
    }
    
    public WhenPEPRestService PEPRest_service_tryAccess_is_executed() {
        assertNotNull( pepRest );
        tryAccessMsgId = pepRest.tryAccess();
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
