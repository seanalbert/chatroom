package remoteendpoint;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import datastructures.ColourMessage;
import datastructures.FileMessage;
import datastructures.Message;
import datastructures.TextMessage;

/**
 * This class decodes incoming messages from json strings to java Message
 * objects and returns the message object to the message handler in the remote
 * endpoint class
 *
 */

public class MessageDecoder implements Decoder.Text<Message> {

	/*
	 * Method that takes in a json string, decodes it and returns a Message
	 * object
	 */
	public Message decode(String jsonMessage) throws DecodeException {

		/* create Java JsonObject from the json string */
		JsonObject jsonObject = Json.createReader(new StringReader(jsonMessage)).readObject();

		/* identify what message type it is */
		if (jsonObject.getString("messageType").equals("TextMessage")) {
			/*
			 * create TextMessage object and set its attributes using the
			 * JsonObject to retrieve object values.
			 */
			TextMessage textMessage = new TextMessage();

			textMessage.setMessage(jsonObject.getString("message"));
			textMessage.setSender(jsonObject.getString("sender"));
			textMessage.setChatroom(jsonObject.getString("chatroom"));
			textMessage.setDateString(jsonObject.getString("dateString"));
			textMessage.setFirst(jsonObject.getBoolean("first"));
			textMessage.setColour(jsonObject.getString("colour"));

			return textMessage;

		} else if (jsonObject.getString("messageType").equals("FileMessage")) {
			/*
			 * create FileMessage object and set its attributes using the
			 * JsonObject to retrieve object values.
			 */
			FileMessage fileMessage = new FileMessage();

			fileMessage.setMessage(jsonObject.getString("message"));
			fileMessage.setFilename(jsonObject.getString("filename"));
			fileMessage.setDateString(jsonObject.getString("dateString"));
			fileMessage.setSender(jsonObject.getString("sender"));
			fileMessage.setChatroom(jsonObject.getString("chatroom"));
			fileMessage.setColour(jsonObject.getString("colour"));

			return fileMessage;

		} else {
			/*
			 * create ColourMessage object and set its attributes using the
			 * JsonObject to retrieve object values.
			 */
			ColourMessage colourMessage = new ColourMessage(jsonObject.getString("colour"));

			return colourMessage;
		}
	}

	/* check if the submitted string can be converted to JsonObject */
	public boolean willDecode(String jsonMessage) {
		boolean flag = true;
		try {
			Json.createReader(new StringReader(jsonMessage)).readObject();
		} catch (Exception e) {
			flag = false; // return false if there is an error and decode method
							// will not be called
		}
		return flag;
	}

	public void destroy() {
	}

	public void init(EndpointConfig arg0) {
	}
}
