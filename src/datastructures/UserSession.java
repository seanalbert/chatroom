package datastructures;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

/**
 * This class represents each web socket session (conversation) and the various
 * attributes associated with each. One user may be have multiple sessions if
 * they are in multiple chatrooms.
 */

public class UserSession {

	/* declare variables */
	private Session userSession;
	private Chatroom chatroom;
	private String username;
	private String messageColour;
	private Basic remote;

	public UserSession() {
		/*
		 * method used for unit testing as cannot instantiate a web socket
		 * session which mean the other constructor cannot be used...
		 */
	}

	public UserSession(Session userSession) {

		this.userSession = userSession; // set the session
		/*
		 * getBasicRemote returns a reference to the remote endpoint of the peer
		 * of the conversation that can send a message to the other peer
		 */
		remote = userSession.getBasicRemote();
	}

	/* setters and getters */
	public Session getSession() {
		return userSession;
	}

	public void setSession(Session userSession) {
		this.userSession = userSession;
	}

	public Chatroom getChatroom() {
		return chatroom;
	}

	public void setChatroom(Chatroom chatroom) {
		this.chatroom = chatroom;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessageColour() {
		return messageColour;
	}

	public void setMessageColour(String messageColour) {
		this.messageColour = messageColour;
	}

	public void sendUsersMessage(UsersMessage usersMessage) throws IOException, EncodeException {
	
		remote.sendObject(usersMessage); // use the remote endpoint to
													// send a
													// users message
	}

	public void sendTextMessage(TextMessage textMessage) throws IOException, EncodeException {

		remote.sendObject(textMessage);// use the remote endpoint to
										// send a
										// text message
	}

	public synchronized void sendFileMessage(FileMessage fileMessage) throws IOException, EncodeException {

		remote.sendObject(fileMessage); // use the remote
										// endpoint to send a file message

		/*
		 * When large files were being sent they did not reach the intended
		 * clients. I think this is due to them not leaving the output stream
		 * for some reason. By sending data after the file the stream clears and
		 * the data sends. This is why I send the "clear stream" string...
		 */
		remote.sendText("clear stream");

	}
}
