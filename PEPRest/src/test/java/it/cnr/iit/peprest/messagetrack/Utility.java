package it.cnr.iit.peprest.messagetrack;

import java.util.Arrays;

import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponseContent;
import it.cnr.iit.ucsinterface.pdp.PDPResponse;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml.core.schema.wd_17.ResultType;

public class Utility {

    protected ReevaluationResponse buildReevaluationResponse( DecisionType decisionType ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        ReevaluationResponse reevaluationResponse = new ReevaluationResponse();
        reevaluationResponse.setPDPEvaluation( pdpEvaluation );
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

    public static TryAccessResponse buildTryAccessResponse( TryAccessMessage message, DecisionType decisionType,
            String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        TryAccessResponseContent content = new TryAccessResponseContent();
        content.setSessionId( sessionId );
        content.setPDPEvaluation( pdpEvaluation );
        TryAccessResponse tryAccessResponse = new TryAccessResponse( message.getMessageId() );
        tryAccessResponse.setTryAccessResponseContent( content );
        return tryAccessResponse;
    }

    public static StartAccessResponse buildStartAccessResponse( StartAccessMessage startAccessMessage,
            DecisionType decisionType, String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        StartAccessResponse startAccessResponse = new StartAccessResponse( startAccessMessage.getMessageId() );
        startAccessResponse.setPDPEvaluation( pdpEvaluation );
        return startAccessResponse;
    }

    public static EndAccessResponse buildEndAccessResponse( EndAccessMessage endAccessMessage,
            DecisionType decisionType, String sessionId ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        EndAccessResponse endAccessResponse = new EndAccessResponse( endAccessMessage.getMessageId() );
        endAccessResponse.setPDPEvaluation( pdpEvaluation );
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

    public static ReevaluationResponse buildReevaluationResponse( String sessionId, DecisionType decisionType ) {
        PDPResponse pdpResponse = buildPDPResponse( decisionType );
        pdpResponse.setSessionId( sessionId );
        ReevaluationResponse reevaluationResponse = new ReevaluationResponse();
        reevaluationResponse.setPDPEvaluation( pdpResponse );
        return reevaluationResponse;
    }

}
