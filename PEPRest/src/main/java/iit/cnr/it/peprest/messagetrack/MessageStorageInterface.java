package iit.cnr.it.peprest.messagetrack;

import java.util.Optional;

import iit.cnr.it.ucsinterface.message.Message;

/**
 * This is the interface that must be implemented by every object that wants to keep rtack of messages going from/to the
 * PEP. Messages are tracked by means of the MessageId that is automatically assigned by the class that creates the
 * Message. Messages are then grouped together by means of the sessionId
 * 
 * @author antonio
 *
 */
public interface MessageStorageInterface {

  /**
   * Retrieves the status of message. The status of a message is an enumerate
   * 
   * @param messageId the id of the message that was returned to the caller
   * @return the Caller response associated with that messageId
   */
  public Optional<CallerResponse> getMessageStatus(String messageId);

  /**
   * Adds a new message in the queue
   * 
   * @param message the message to be added in the queue
   * @return true if the add is ok, false otherwise
   */
  public boolean addMessage(Message message);

}
