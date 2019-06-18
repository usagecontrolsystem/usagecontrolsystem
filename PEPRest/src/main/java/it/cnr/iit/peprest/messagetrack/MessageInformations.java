package it.cnr.iit.peprest.messagetrack;

import it.cnr.iit.ucs.message.EvaluatedMessage;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponseMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponseMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponseMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponseMessage;
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

    private static MessageInformations build( Message message, PEP_STATUS status ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getMessageId();
        messageInformations.callerResponse = new CallerResponse();
        messageInformations.callerResponse.setStatus( status );
        return messageInformations;
    }

    public static MessageInformations build( TryAccessMessage message ) {
        return build( message, PEP_STATUS.TRYACCESS_SENT );
    }

    public static MessageInformations build( StartAccessMessage message ) {
        MessageInformations messageInformations = build( message, PEP_STATUS.STARTACCESS_SENT );
        messageInformations.callerResponse.setSessionId( message.getSessionId() );
        return messageInformations;
    }

    public static MessageInformations build( EndAccessMessage message ) {
        return build( message, PEP_STATUS.ENDACCESS_SENT );
    }

    public static MessageInformations build( ReevaluationResponseMessage message ) {
        if( message.getEvaluation().isDecision( DecisionType.PERMIT ) ) {
            return build( message, PEP_STATUS.SESSION_RESUMED );
        } else {
            return build( message, PEP_STATUS.REVOKED );
        }
    }

    public void merge( EvaluatedMessage message, PEP_STATUS status, PEP_STATUS positiveStatus, PEP_STATUS negativeStatus ) {
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

    public void merge( TryAccessResponseMessage message ) {
        merge( message, PEP_STATUS.TRYACCESS_SENT, PEP_STATUS.TRYACCESS_PERMIT, PEP_STATUS.TRYACCESS_DENY );
        callerResponse.setSessionId( message.getSessionId() );
    }

    public void merge( StartAccessResponseMessage message ) {
        merge( message, PEP_STATUS.STARTACCESS_SENT, PEP_STATUS.STARTACCESS_PERMIT, PEP_STATUS.STARTACCESS_DENY );
    }

    public void merge( EndAccessResponseMessage message ) {
        merge( message, PEP_STATUS.ENDACCESS_SENT, PEP_STATUS.ENDACCESS_PERMIT, PEP_STATUS.ENDACCESS_DENY );
    }

}
