package remoteendpoint;

import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import datastructures.FileMessage;
import datastructures.Message;
import datastructures.TextMessage;
import datastructures.UsersMessage;

/**
 * This class encodes outgoing messages from java Message objects to json
 * strings objects and returns the json string to be sent to the clients.
 *
 */

public class MessageEncoder implements Encoder.Text<Message> {

	/*
	 * Method that takes in a java message object, encodes it and returns a json
	 * string
	 */
	public String encode(Message message) throws EncodeException {
		String returnString = null;

		/* identify what message type it is */
		if (message instanceof TextMessage) {

			TextMessage textMessage = (TextMessage) message; // cast the generic
																// type message
																// to
																// TextMessage

			/* build json string using Json.createObjectBuilder() */
			returnString = Json.createObjectBuilder().add("messageType", textMessage.getClass().getSimpleName())
													.add("sender", textMessage.getSender())
													.add("message", textMessage.getMessage())
													.add("chatroom", textMessage.getChatroom())
													.add("dateString", textMessage.getDateString())
													.add("first", textMessage.getFirst())
													.add("colour", textMessage.getColour()).build().toString();
			
		} else if (message instanceof UsersMessage) {

			UsersMessage usersMessage = (UsersMessage) message; // cast the
																// generic type
																// message to
																// UsersMessage

			/*
			 * call the buildJsonUsersData method to build a json string from a
			 * set
			 */
			returnString = buildJsonUsersData(usersMessage.getUsers(), usersMessage.getClass().getSimpleName());

		} else if (message instanceof FileMessage) {

			FileMessage fm = (FileMessage) message; // cast the generic type
													// message to FileMessage

			/* build json string using Json.createObjectBuilder() */
			returnString = Json.createObjectBuilder().add("messageType", fm.getClass().getSimpleName())
													.add("filename", fm.getFilename())
													.add("message", fm.getMessage())
													.add("dateString", fm.getDateString())
													.add("sender", fm.getSender())
													.add("colour", fm.getColour()).build().toString();
		}

		return returnString; // return encoded java object as a json string
	}

	public void destroy() {
	}

	public void init(EndpointConfig arg0) {
	}

	private String buildJsonUsersData(Set<String> set, String messageType) {

		JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder(); // create
																		// JsonArrayBuilder

		for (String s : set) {
			jsonArrayBuilder.add(s); // add each username to array
		}

		/*
		 * add the json array of user's names to the json string builder and
		 * return it as a json string
		 */
		return Json.createObjectBuilder().add("messageType", messageType)
										.add("users", jsonArrayBuilder).build().toString();
	}
}
