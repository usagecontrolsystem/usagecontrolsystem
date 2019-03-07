package iit.cnr.it.peprest.messagetrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;
import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * 
 * @author antonio
 *
 */
public class MessageStorage implements MessageStorageInterface, MessagesPerSession {
	private HashMap<String, MessageInformations> messageFlow = new HashMap<>();
	private HashMap<String, List<String>> messagesPerSession = new HashMap<>();

	private static final Logger LOGGER = Logger.getLogger(MessageStorage.class.getName());

	@Override
	public Optional<CallerResponse> getMessageStatus(String messageId) {
		if (messageId == null || messageId.isEmpty()) {
			throw new NullPointerException("Invalid message id passed: " + messageId);
		}
		if (!messageFlow.containsKey(messageId)) {
			return Optional.empty();
		}
		return Optional.of(messageFlow.get(messageId).getCallerResponse());

	}

	@Override
	public boolean addMessage(Message message) {
		if (message == null) {
			LOGGER.severe("Message is null");
			return false;
		}
		if (messageFlow.containsKey(message.getID())) {
			return mergeMessages(message);
		} else if (message instanceof TryAccessMessage) {
			return addNewMessage(message);
		} else if (message instanceof StartAccessMessage) {
			addMessageId(((StartAccessMessage) message).getSessionId(), message.getID());
			return addNewMessage(message);
		} else if (message instanceof EndAccessMessage) {
			addMessageId(((EndAccessMessage) message).getSessionId(), message.getID());
			return addNewMessage(message);
		} else {
			throw new IllegalArgumentException("Invalid message");
		}
	}

	private boolean mergeMessages(Message message) {
		MessageInformations messageInformations = messageFlow.get(message.getID());
		if (message instanceof TryAccessResponse) {
			addMessagePerSession((TryAccessResponse) message);
			messageInformations.merge((TryAccessResponse) message);
		} else if (message instanceof StartAccessResponse) {
			messageInformations.merge((StartAccessResponse) message);
		} else if (message instanceof EndAccessResponse) {
			messageInformations.merge((EndAccessResponse) message);
		}
		return insert(messageInformations);
	}

	private boolean addNewMessage(Message message) {
		MessageInformations messageInformations = null;
		if (message instanceof TryAccessMessage) {
			messageInformations = MessageInformations.fromTryAccessMessage((TryAccessMessage) message);
		} else if (message instanceof StartAccessMessage) {
			messageInformations = MessageInformations.fromStartAccessMessage((StartAccessMessage) message);
		} else if (message instanceof EndAccessMessage) {
			messageInformations = MessageInformations.fromEndAccessMessage((EndAccessMessage) message);
		}
		return insert(messageInformations);
	}

	private boolean insert(MessageInformations messageInformations) {
		if (messageInformations == null) {
			LOGGER.severe("MessageInformations is null");
			return false;
		}
		messageFlow.put(messageInformations.getMessageId(), messageInformations);
		return true;
	}

	@Override
	public List<String> getMessagesPerSession(String sessionId) {
		if (sessionId.isEmpty() || sessionId == null || !messagesPerSession.containsKey(sessionId)) {
			throw new IllegalArgumentException();
		}
		return messagesPerSession.get(sessionId);
	}

	private void addMessagePerSession(TryAccessResponse message) {
		if (message.getPDPEvaluation().getResult().equals(DecisionType.PERMIT.value())) {
			messagesPerSession.put(message.getSessionId(), new ArrayList<>());
			addMessageId(message.getSessionId(), message.getID());
		}
	}

	private void addMessageId(String sessionId, String messageId) {
		messagesPerSession.get(sessionId).add(messageId);
	}

}