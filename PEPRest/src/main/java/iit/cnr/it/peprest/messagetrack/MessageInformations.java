package iit.cnr.it.peprest.messagetrack;

import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * Tracks the status of a response with the list of the evaluation received by the UCS. It also stores the session id.
 * The only information that can be seen by an external component, i.e. a component external to the PEP is the status of
 * the session.
 *
 * @author antonio
 *
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
        this.evaluation = evaluationList;
    }

    public CallerResponse getCallerResponse() {
        return callerResponse;
    }

    public void setCallerResponse( CallerResponse callerResponse ) {
        this.callerResponse = callerResponse;
    }

    public static MessageInformations fromTryAccessMessage( TryAccessMessage message ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getID();
        messageInformations.callerResponse = new CallerResponse();
        messageInformations.callerResponse.setStatus( STATUS.TRYACCESS_SENT );
        return messageInformations;
    }

    public static MessageInformations fromStartAccessMessage( StartAccessMessage message ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getID();
        messageInformations.callerResponse = new CallerResponse();
        messageInformations.callerResponse.setStatus( STATUS.STARTACCESS_SENT );
        messageInformations.callerResponse.setSessionId( message.getSessionId() );
        return messageInformations;
    }

    public static MessageInformations fromEndAccessMessage( EndAccessMessage message ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getID();
        messageInformations.callerResponse = new CallerResponse();
        messageInformations.callerResponse.setStatus( STATUS.ENDACCESS_SENT );
        return messageInformations;
    }

    public void merge( TryAccessResponse message ) {
        if( callerResponse.getStatus() != STATUS.TRYACCESS_SENT ) {
            throw new IllegalArgumentException( "Wrong flow of messages!! \n status is: " + callerResponse.getStatus() );
        }
        evaluation = message.getPDPEvaluation();
        System.err.println( evaluation.getResult() + "\t" + DecisionType.PERMIT.toString() );
        if( evaluation.getResult().equals( DecisionType.PERMIT.value() ) ) {
            callerResponse.setStatus( STATUS.TRYACCESS_PERMIT );
            callerResponse.setSessionId( message.getSessionId() );
        } else {
            callerResponse.setStatus( STATUS.TRYACCESS_DENY );
        }
    }

    public void merge( StartAccessResponse message ) {
        if( callerResponse.getStatus() != STATUS.STARTACCESS_SENT ) {
            throw new IllegalArgumentException( "Wrong flow of messages!!" );
        }
        evaluation = message.getPDPEvaluation();
        if( evaluation.getResult().equals( DecisionType.PERMIT.value() ) ) {
            callerResponse.setStatus( STATUS.STARTACCESS_PERMIT );
        } else {
            callerResponse.setStatus( STATUS.STARTACCESS_DENY );
        }
    }

    public void merge( EndAccessResponse message ) {
        if( callerResponse.getStatus() != STATUS.ENDACCESS_SENT ) {
            throw new IllegalArgumentException( "Wrong flow of messages!!" );
        }
        evaluation = message.getPDPEvaluation();
        if( evaluation.getResult().equals( DecisionType.PERMIT.value() ) ) {
            callerResponse.setStatus( STATUS.ENDACCESS_PERMIT );
        } else {
            callerResponse.setStatus( STATUS.ENDACCESS_DENY );
        }
    }

    public static MessageInformations fromReevaluationResponse( ReevaluationResponse message ) {
        MessageInformations messageInformations = new MessageInformations();
        messageInformations.messageId = message.getID();
        messageInformations.callerResponse = new CallerResponse();
        if( message.getPDPEvaluation().getResult().equals( DecisionType.PERMIT.value() ) ) {
            messageInformations.callerResponse.setStatus( STATUS.SESSION_RESUMED );
        } else {
            messageInformations.callerResponse.setStatus( STATUS.REVOKED );
        }
        return messageInformations;
    }

}
