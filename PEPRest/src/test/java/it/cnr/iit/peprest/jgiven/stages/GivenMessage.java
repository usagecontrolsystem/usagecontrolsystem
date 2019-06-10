package it.cnr.iit.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

import it.cnr.iit.ucs.constants.RestOperation;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;
import it.cnr.iit.ucs.pdp.PDPResponse;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml.core.schema.wd_17.ResultType;

public class GivenMessage extends Stage<GivenMessage> {

    @ProvidedScenarioState
    String sessionId;

    @ProvidedScenarioState
    Message message;

    @ExpectedScenarioState
    String messageId;

    @ProvidedScenarioState
    List<String> messageIds;

    @BeforeScenario
    public void init() {
        sessionId = UUID.randomUUID().toString();
    }

    public GivenMessage a_TryAccessResponse_request_with_$_decision( DecisionType decisionType ) {
        message = buildTryAccessResponse( decisionType );
        return self();
    }

    public GivenMessage a_StartAccessResponse_request_with_$_decision( DecisionType decisionType ) {
        message = buildStartAccessResponse( decisionType );
        return self();
    }

    public GivenMessage a_ReevaluationResponse_request_with_$_decision( DecisionType decisionType ) {
        message = buildReevaluationResponse( decisionType );
        return self();
    }

    public GivenMessage an_associated_messageId( @Hidden int index ) {
        assertNotNull( messageIds );
        message.setMessageId( messageIds.get( index ) );
        return self();
    }

    protected StartAccessResponseMessage buildStartAccessResponsePermit() {
        return buildStartAccessResponse( DecisionType.PERMIT );
    }

    protected ReevaluationResponseMessage buildReevaluationResponse( DecisionType decisionType ) {
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        pdpEvaluation.setSessionId( sessionId );
        ReevaluationResponseMessage reevaluationResponse = new ReevaluationResponseMessage( sessionId );
        reevaluationResponse.setEvaluation( pdpEvaluation );
        reevaluationResponse.setMessageId( UUID.randomUUID().toString() );
        return reevaluationResponse;
    }

    protected PDPResponse buildPDPResponse( DecisionType decision ) {
        ResultType resultType = new ResultType();
        resultType.setDecision( decision );
        ResponseType responseType = new ResponseType();
        responseType.setResult( Arrays.asList( resultType ) );
        PDPResponse pdpResponse = new PDPResponse( responseType );
        return pdpResponse;
    }

    public GivenMessage create_permit_response_for_$( RestOperation operation ) {
        switch( operation ) {
            case TRY_ACCESS_RESPONSE:
                message = buildTryAccessResponse( DecisionType.PERMIT );
                break;
            case START_ACCESS_RESPONSE:
                message = buildStartAccessResponse( DecisionType.PERMIT );
                break;
            case END_ACCESS_RESPONSE:
                message = buildEndAccessResponse( DecisionType.PERMIT );
                break;
            default:
                fail( "Unknown operation type in message creation" );
                break;
        }
        return self();
    }

    private TryAccessResponseMessage buildTryAccessResponse( DecisionType decisionType ) {
        if( messageId == null ) {
            messageId = UUID.randomUUID().toString();
        }
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        TryAccessResponseMessage tryAccessResponse = new TryAccessResponseMessage( sessionId );
        tryAccessResponse.setMessageId( messageId );
        tryAccessResponse.setSessionId( sessionId );
        tryAccessResponse.setEvaluation( pdpEvaluation );
        return tryAccessResponse;
    }

    protected StartAccessResponseMessage buildStartAccessResponse( DecisionType decisionType ) {
        if( messageId == null ) {
            messageId = UUID.randomUUID().toString();
        }
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        StartAccessResponseMessage startAccessResponse = new StartAccessResponseMessage( sessionId );
        startAccessResponse.setEvaluation( pdpEvaluation );
        startAccessResponse.setMessageId( messageId );
        return startAccessResponse;
    }

    protected EndAccessResponseMessage buildEndAccessResponse( DecisionType decisionType ) {
        if( messageId == null ) {
            messageId = UUID.randomUUID().toString();
        }
        PDPResponse pdpEvaluation = buildPDPResponse( decisionType );
        EndAccessResponseMessage endAccessResponse = new EndAccessResponseMessage( sessionId );
        endAccessResponse.setEvaluation( pdpEvaluation );
        endAccessResponse.setMessageId( messageId );
        return endAccessResponse;
    }
}
