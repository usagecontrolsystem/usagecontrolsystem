package it.cnr.iit.peprest.messagetrack;

import it.cnr.iit.ucs.message.EvaluatedResponse;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucs.pdp.PDPEvaluation;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * Tracks the status of a response with the list of the evaluation received by the UCS. It also stores the session id.
 * The only information that can be seen by an external component, i.e. a component external to the PEP is the status of
 * the session.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public class MessageInformations {

    private CallerResponse callerResponse;
    private String messageId;
    private PDPEvaluation evaluation;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId( String messageId ) {
        this.messageId = messageId;
    }

    public PDPEvaluation getEvaluationList() {
        return evaluation;
    }

    public void setEvaluationList( PDPEvaluation evaluationList ) {
        evaluation = evaluationList;
    }

    public CallerResponse getCallerResponse() {
        return callerResponse;
    }

    public void setCallerResponse( CallerResponse callerResponse ) {
        this.callerResponse = callerResponse;
    }

    private static MessageInformations build( Message message, STATUS status ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getMessageId();
        messageInformations.callerResponse = new CallerResponse();
        messageInformations.callerResponse.setStatus( status );
        return messageInformations;
    }

    public static MessageInformations build( TryAccessMessage message ) {
        return build( message, STATUS.TRYACCESS_SENT );
    }

    public static MessageInformations build( StartAccessMessage message ) {
        MessageInformations messageInformations = build( message, STATUS.STARTACCESS_SENT );
        messageInformations.callerResponse.setSessionId( message.getSessionId() );
        return messageInformations;
    }

    public static MessageInformations build( EndAccessMessage message ) {
        return build( message, STATUS.ENDACCESS_SENT );
    }

    public static MessageInformations build( ReevaluationResponse message ) {
        if( message.getEvaluation().isDecision( DecisionType.PERMIT ) ) {
            return build( message, STATUS.SESSION_RESUMED );
        } else {
            return build( message, STATUS.REVOKED );
        }
    }

    public void merge( EvaluatedResponse message, STATUS status, STATUS positiveStatus, STATUS negativeStatus ) {
        if( callerResponse.getStatus() != status ) {
            throw new IllegalArgumentException( "Wrong flow of messages!! \n status is: " + callerResponse.getStatus() );
        }
        evaluation = message.getEvaluation();
        if( evaluation.isDecision( DecisionType.PERMIT ) ) {
            callerResponse.setStatus( positiveStatus );
        } else {
            callerResponse.setStatus( negativeStatus );
        }
    }

    public void merge( TryAccessResponse message ) {
        merge( message, STATUS.TRYACCESS_SENT, STATUS.TRYACCESS_PERMIT, STATUS.TRYACCESS_DENY );
        callerResponse.setSessionId( message.getSessionId() );
    }

    public void merge( StartAccessResponse message ) {
        merge( message, STATUS.STARTACCESS_SENT, STATUS.STARTACCESS_PERMIT, STATUS.STARTACCESS_DENY );
    }

    public void merge( EndAccessResponse message ) {
        merge( message, STATUS.ENDACCESS_SENT, STATUS.ENDACCESS_PERMIT, STATUS.ENDACCESS_DENY );
    }

}
