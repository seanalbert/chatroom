package datastructures;

import java.util.Set;

/**
 * Message that is sent to all users of a chat room when a user joins the
 * chatroom or when a user leaves the chatroom. The users text box is
 * updated when a UsersMessage is received
 */

public class UsersMessage implements Message {
	
	private Set<String> users = null;

	/* constructor */
	public UsersMessage(Set<String> users) {
		this.users = users;
	}

	/* getter to return the set of usernames */
	public Set<String> getUsers() {
		return users;
	}
}
