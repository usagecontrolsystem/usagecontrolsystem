package it.cnr.iit.peprest.messagetrack;

/**
 * This object holds many useful informations:
 * <ol>
 * <li>The status of the message</li>
 * <li>The id that was assigned to the session initialized by the caller, if any, by which the caller can manage its
 * session</li>
 * <li>The id of the derived message (only after a tryAccess) that makes it easier for the caller to track the
 * evaluation of its session</li>
 * </ol>
 *
 * @author Antonio La Marra
 *
 */
public class CallerResponse {

    private STATUS status;
    private String sessionId;
    private String derivedMessageId;

    public STATUS getStatus() {
        return status;
    }

    public void setStatus( STATUS status ) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getDerivedMessageId() {
        return derivedMessageId;
    }

    public void setDerivedMessageId( String derivedMessageId ) {
        this.derivedMessageId = derivedMessageId;
    }

}
