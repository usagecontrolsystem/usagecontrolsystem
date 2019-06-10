package it.cnr.iit.ucs.message;

/**
 * A message that has a sessionId
 *
 * @author Alessandro Rosetti
 */
public interface IdentifiedMessage {

    public void setSessionId( String sessionId );

    public String getSessionId();
}
