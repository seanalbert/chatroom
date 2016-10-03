package datastructures;

/**
 * 
 * Abstract superclass of TextMessage and FileMessage
 *
 */

public abstract class ChatMessage implements Message {
	
	/* declare variables */
	private String sender;
	private String message;
	private String chatroom;
	private String dateString;
	private String colour;
	
	public ChatMessage() {
		colour = "#c0c0c0";		// set colour to default
	}

	/* setters and getters */
	public String getSender() {
		return sender;
	}

	public void setSender(String name) {
		this.sender = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getChatroom() {
		return chatroom;
	}

	public void setChatroom(String chatroom) {
		this.chatroom = chatroom;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}
}
