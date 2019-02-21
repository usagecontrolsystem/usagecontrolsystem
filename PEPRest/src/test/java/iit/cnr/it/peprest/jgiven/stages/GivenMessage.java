package iit.cnr.it.peprest.jgiven.stages;

import java.util.Arrays;
import java.util.UUID;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PDPResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponseContent;
import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml.core.schema.wd_17.ResultType;

public class GivenMessage extends Stage<GivenMessage> {

    @ProvidedScenarioState
    String sessionId;

    @ProvidedScenarioState
    Message message;

    @BeforeScenario
    public void init() {
    	sessionId = UUID.randomUUID().toString();
    }

    public GivenMessage a_ReevaluationResponse_request_with_decision_$(DecisionType decisionType){
    	message = buildReevaluationResponse(decisionType);
    	return self();
    }

	protected TryAccessResponse buildTryAccessResponseDeny() {
		PDPResponse pdpEvaluation = buildPDPResponse(DecisionType.DENY);
		TryAccessResponseContent content = new TryAccessResponseContent();
		content.setPDPEvaluation(pdpEvaluation);
		TryAccessResponse tryAccessResponse = new TryAccessResponse(sessionId);
		tryAccessResponse.setContent(content);
		return tryAccessResponse;
	}

	protected StartAccessResponse buildStartAccessResponsePermit() {
		PDPResponse pdpEvaluation = buildPDPResponse(DecisionType.PERMIT);
		StartAccessResponse startAccessResponse = new StartAccessResponse(sessionId);
		startAccessResponse.setResponse(pdpEvaluation);
		return startAccessResponse;
	}

	protected ReevaluationResponse buildReevaluationResponse(DecisionType decisionType) {
		PDPResponse pdpEvaluation = buildPDPResponse(decisionType);
		ReevaluationResponse reevaluationResponse = new ReevaluationResponse();
		reevaluationResponse.setPDPEvaluation(pdpEvaluation);
		return reevaluationResponse;
	}

	protected PDPResponse buildPDPResponse(DecisionType decision) {
		ResultType resultType = new ResultType();
		resultType.setDecision(decision);
		ResponseType responseType = new ResponseType();
		responseType.setResult(Arrays.asList(resultType));
		PDPResponse pdpResponse = new PDPResponse();
		pdpResponse.setResponseType(responseType);
		pdpResponse.setInitialized(true);
		return pdpResponse;
	}
}
