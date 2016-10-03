package datastructures;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.EncodeException;

/**
 * This class represents the chatrooms and the various attributes associated
 * with each
 */


public class Chatroom implements Serializable {	// serializable so it can be used with JSTL

	/**
	 * 
	 */
	private static final long serialVersionUID = 2023308169771998374L;

	/* static HashSet of chatrooms to hold all chatrooms */
	public static Set<Chatroom> chatrooms = Collections.synchronizedSet(new HashSet<Chatroom>());

	/* HashSet to store web socket sessions associated with each chatroom */
	private Set<UserSession> chatroomUsers = Collections.synchronizedSet(new HashSet<UserSession>());
	private String name;
	private String admin;

	/* set variables and add chatroom to the chatroom set in constructor */
	public Chatroom(String name, String admin) {
		this.name = name;
		this.admin = admin;
		chatrooms.add(this);
	}

	public void addUser(UserSession user) {
		chatroomUsers.add(user); // add a user to the set of users in a chatroom
		user.setChatroom(this);
	}

	public void removeUser(UserSession user) {
		chatroomUsers.remove(user); // remove a user from the set of users in
										// a chatroom
		user.setChatroom(null);
	}

	public String getName() {
		return name; // return the name of a chatroom
	}

	public String getAdmin() {
		return admin; // return the name of a admin
	}

	public HashSet<String> getAllUserIds() {
		HashSet<String> returnSet = new HashSet<String>();

		for (UserSession user : chatroomUsers) {
			returnSet.add(user.getUsername()); // return a set containing all of
												// the user ids in a chatroom
		}
		return returnSet;
	}

	public void sendUsersMessage(UsersMessage usersMessage) throws IOException, EncodeException {
		for (UserSession user : chatroomUsers) {
			user.sendUsersMessage(usersMessage); // send a users message to all
													// of the users in a
													// chatroom
		}
	}

	public void sendTextMessage(TextMessage outgoingTextMessage) throws IOException, EncodeException {
		for (UserSession user : chatroomUsers) {
			user.sendTextMessage(outgoingTextMessage); // send text message to
														// all the users of a
														// chatroom
		}
	}

	public void sendFileMessage(FileMessage outgoingFileMessage) throws IOException, EncodeException {
		for (UserSession user : chatroomUsers) {
			user.sendFileMessage(outgoingFileMessage); // send file message to
														// all the users of a
														// chatroom
		}
	}

	public static Chatroom getChatroomByName(String name) {
		/* iterate through set and return chatroom with specified name */
		for (Chatroom c : chatrooms) {
			if (name.equals(c.getName())) {
				return c;
			}
		}
		return null;
	}

	public static void removeChatroom(String name) {
		/* iterate through set and remove chatroom with specified name */
		for (Chatroom c : chatrooms) {
			if (c.getName().equals(name)) {
				chatrooms.remove(c);
				break;
			}
		}
	}
}
