package it.cnr.iit.peprest.messagetrack;

import java.util.Arrays;
import java.util.UUID;

import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;
import it.cnr.iit.ucs.pdp.PDPResponse;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml.core.schema.wd_17.ResultType;

public class Utility {

    protected ReevaluationResponseMessage buildReevaluationResponse( DecisionType decisionType ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        ReevaluationResponseMessage reevaluationResponse = new ReevaluationResponseMessage();
        reevaluationResponse.setEvaluation( pdpEvaluation );
        return reevaluationResponse;
    }

    private static PDPResponse buildPDPResponse( DecisionType decision ) {
        ResultType resultType = new ResultType();
        resultType.setDecision( decision );
        ResponseType responseType = new ResponseType();
        responseType.setResult( Arrays.asList( resultType ) );
        PDPResponse pdpResponse = new PDPResponse( responseType );
        return pdpResponse;
    }

    public static TryAccessResponseMessage buildTryAccessResponse( TryAccessMessage message, DecisionType decisionType,
            String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        TryAccessResponseMessage tryAccessResponse = new TryAccessResponseMessage( message.getMessageId() );
        tryAccessResponse.setSessionId( sessionId );
        tryAccessResponse.setEvaluation( pdpEvaluation );
        return tryAccessResponse;
    }

    public static StartAccessResponseMessage buildStartAccessResponse( StartAccessMessage startAccessMessage,
            DecisionType decisionType, String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        StartAccessResponseMessage startAccessResponse = new StartAccessResponseMessage( startAccessMessage.getMessageId() );
        startAccessResponse.setEvaluation( pdpEvaluation );
        return startAccessResponse;
    }

    public static EndAccessResponseMessage buildEndAccessResponse( EndAccessMessage endAccessMessage,
            DecisionType decisionType, String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        EndAccessResponseMessage endAccessResponse = new EndAccessResponseMessage( endAccessMessage.getMessageId() );
        endAccessResponse.setEvaluation( pdpEvaluation );
        return endAccessResponse;
    }

    public static TryAccessMessage buildTryAccessMessage() {
        TryAccessMessage tryAccessMessage = new TryAccessMessage();
        return tryAccessMessage;
    }

    public static StartAccessMessage buildStartAccessMessage( String sessionId ) {
        StartAccessMessage startAccessMessage = new StartAccessMessage();
        startAccessMessage.setSessionId( sessionId );
        return startAccessMessage;
    }

    public static EndAccessMessage buildEndAccessMessage( String sessionId ) {
        EndAccessMessage endAccessMessage = new EndAccessMessage();
        endAccessMessage.setSessionId( sessionId );
        return endAccessMessage;
    }

    public static ReevaluationResponseMessage buildReevaluationResponse( String sessionId, DecisionType decisionType ) {
        PDPResponse pdpResponse = buildPDPResponse( decisionType );
        ReevaluationResponseMessage reevaluationResponse = new ReevaluationResponseMessage();
        reevaluationResponse.setSessionId( sessionId );
        reevaluationResponse.setEvaluation( pdpResponse );
        reevaluationResponse.setMessageId( UUID.randomUUID().toString() );
        return reevaluationResponse;
    }

}
