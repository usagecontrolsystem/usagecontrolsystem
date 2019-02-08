package iit.cnr.it.peprest.bdd.example.jgiven;

public class PEPRestService {

	Message message;

	public void selectNodes(String originNode, String destinationNode) {
		this.message = new Message(originNode, destinationNode);
	}

	public Message transmitMessage(float payload) {
		if (payload<10.0) {
			throw new IllegalArgumentException("put some payload to be sent dude");
		}
		if (message == null) {
			throw new IllegalStateException("need to specify the transmission nodes");
		}
		message.setPayload(payload);
		return message;
	}

}
