package datastructures;

/**
 * subclass of the abstract superclass ChatMessage which represents all
 * TextMessages sent and received by users
 */

public class TextMessage extends ChatMessage {	

	private boolean first; // variable to determine if a message is the first message sent

	public TextMessage() {
		super();	// call to superclass constructor
		first = false;
	}

	/* setters and getters */
	public boolean getFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

}
