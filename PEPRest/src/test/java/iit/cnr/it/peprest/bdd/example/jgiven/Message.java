package iit.cnr.it.peprest.bdd.example.jgiven;

public class Message {

    String originNode;
    String destinationNode;
    float payload;

    public float getPayload() {
		return payload;
	}

	public void setPayload(float content) {
		this.payload = content;
	}

	public Message (String originNode, String destinationNode){
    	this.originNode = originNode;
    	this.destinationNode = destinationNode;
    }

	public boolean isOriginNode(String originNode) {
		return this.originNode.equals(originNode);
	}

	public boolean isDestinationNode(String destinationNode) {
		return this.destinationNode.equals(destinationNode);
	}

}
