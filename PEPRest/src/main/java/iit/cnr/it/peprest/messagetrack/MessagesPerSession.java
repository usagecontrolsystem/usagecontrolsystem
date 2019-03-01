package iit.cnr.it.peprest.messagetrack;

import java.util.List;

public interface MessagesPerSession {
	/**
	 * Retrieves the list of messages exchanged for a certain session id
	 * 
	 * @param sessionId the session id to be considered
	 * @return the list of message ids exchanged for that session
	 */
	public List<String> getMessagesPerSession(String sessionId);
}
