package remoteendpoint;

import java.io.IOException;
import java.sql.SQLException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.sql.ResultSet;

import database.DatabaseOperation;
import datastructures.UserSession;
import datastructures.Chatroom;
import datastructures.ColourMessage;
import datastructures.FileMessage;
import datastructures.Message;
import datastructures.TextMessage;
import datastructures.UsersMessage;
import properties.SystemProperties;

/**
 * Remote endpoint that is created when a client connects to the server. This
 * class manages the websocket connection and handles messages.
 */

@ServerEndpoint(value = "/chatEndpoint", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class ChatEndpoint implements SystemProperties {

	/* declare variables */
	private UserSession user;
	private ResultSet filers, textrs;
	private DatabaseOperation dbo;
	private FileMessage fileMessage;
	private TextMessage textMessage;
	private UsersMessage usersMessage;
	private ColourMessage colourMessage;

	@OnOpen
	public void handleOpen(Session userSession) // triggered when web socket
												// connection is opened
			throws IOException, EncodeException, SQLException, ClassNotFoundException {
		this.user = new UserSession(userSession); // create new UserSession
													// object when web
													// socket is opened
	}

	/*
	 * this method is triggered when a message is received. it is synchronized
	 * so if a message is received while another message is being handled the
	 * thread will wait until the message has been handled
	 */
	@OnMessage
	public synchronized void handleMessage(Message incomingMessage)
			throws IOException, EncodeException, SQLException, ClassNotFoundException {

		if (incomingMessage instanceof TextMessage) {

			/*
			 * if the incoming message is of type TextMessage cast it to this
			 * type
			 */
			textMessage = (TextMessage) incomingMessage;

			if (textMessage.getFirst()) {
				setupChatroom(textMessage); // if it is the first message call
											// setupChatroom()
			}

			user.getChatroom().sendTextMessage(textMessage); // send the message
																// to all users
																// in the
																// chatroom

			dbo = new DatabaseOperation(); // create a database operation object
			dbo.insertMessage(textMessage.getMessage(), textMessage.getSender(), textMessage.getChatroom(),
					textMessage.getDateString(), textMessage.getColour()); // insert
																			// message
																			// to
																			// database

		} else if (incomingMessage instanceof FileMessage) {

			/*
			 * if the incoming message is of type FileMessage cast it to this
			 * type
			 */
			fileMessage = (FileMessage) incomingMessage;

			user.getChatroom().sendFileMessage(fileMessage); // send File
																// Message to
																// all users in
																// the chatroom

			dbo = new DatabaseOperation(); // create a database operation object

			dbo.insertFile(fileMessage.getMessage(), fileMessage.getSender(), fileMessage.getChatroom(),
					fileMessage.getColour(), fileMessage.getFilename(), fileMessage.getDateString()); // insert
																										// file
																										// to
																										// database

		} else if (incomingMessage instanceof ColourMessage) {

			/*
			 * if the incoming message is of type ColourMessage cast it to this
			 * type
			 */
			colourMessage = (ColourMessage) incomingMessage;

			user.setMessageColour(colourMessage.getColour()); // set the users
																// message
																// colour

			dbo = new DatabaseOperation();
			dbo.updateColour(user.getUsername(), colourMessage.getColour()); // write
																				// it
																				// to
																				// the
																				// database
		}
	}

	@OnClose
	public void handleClose() throws IOException, EncodeException, SQLException {

		/* triggered when web scoket connection is closed */
		Chatroom c = user.getChatroom();
		c.removeUser(user); // remove user from the chatroom

		/*
		 * create a users message with passing in a Set containing the usernames
		 * of all users in the chatroom
		 */
		usersMessage = new UsersMessage(c.getAllUserIds());
		c.sendUsersMessage(usersMessage); // send the users
											// message to all
											// users in the
											// chatroom

	}

	/*
	 * This method is called in the onMessage handler when the first message of
	 * the conversation is received. This message is an automatic message sent
	 * from the browser so in effect it is called when a web socket connection
	 * is opened
	 */
	public void setupChatroom(TextMessage textMessage)
			throws ClassNotFoundException, SQLException, IOException, EncodeException {

		user.setUsername(textMessage.getSender()); // set the username
													// associated with the web
													// socket connection

		/* set user's the chatroom and add the user to the chat */
		Chatroom.getChatroomByName(textMessage.getChatroom()).addUser(user);

		/* get the users colour from the database */
		dbo = new DatabaseOperation();
		user.setMessageColour(dbo.getColour(user.getUsername()));
		textMessage.setColour(user.getMessageColour()); // set the first
														// messages colour

		/*
		 * read all text and file messages in the users chatroom from the
		 * database into result sets ordered by there timestamp
		 */
		textrs = dbo.selectMessage(user.getChatroom().getName());
		filers = dbo.selectFile(user.getChatroom().getName());

		FileMessage fm = new FileMessage(); // file message for sending messages
											// from the database
		TextMessage tm = new TextMessage(); // text message for sending messages
											// from the database

		boolean textNotDone;
		boolean fileNotDone; // booleans to indicate when all messages have been
								// read

		textNotDone = textrs.next(); // move cursor to next position and return
										// true. returns false if no more
										// elements
		fileNotDone = filers.next();

		/*
		 * this while loop checks if there is both files and messages to be
		 * sent. If there is it sends the older message first. If one of the
		 * sets reaches its end it breaks out of the loop.
		 */
		while (textNotDone && fileNotDone) {
			if (textrs.getTimestamp("timstamp").before(filers.getTimestamp("timstamp"))) {
				tm.setSender(textrs.getString("sender"));
				tm.setMessage(textrs.getString("message"));
				tm.setChatroom(textrs.getString("chatroom"));
				tm.setDateString(textrs.getString("dateString"));
				tm.setColour(textrs.getString("colour"));
				user.sendTextMessage(tm);
				textNotDone = textrs.next();
			} else {
				fm.setFilename(filers.getString("filename"));
				fm.setDateString(filers.getString("dateString"));
				fm.setSender(filers.getString("sender"));
				fm.setColour(filers.getString("colour"));
				fm.setMessage(filers.getString("file"));
				user.sendFileMessage(fm);
				fileNotDone = filers.next();
			}
		}

		/*
		 * if there are still text messages to be sent set the attributes and
		 * send the messages. else set the attributes of the file message and
		 * send
		 */
		if (textNotDone) {
			while (textNotDone) {
				tm.setSender(textrs.getString("sender"));
				tm.setMessage(textrs.getString("message"));
				tm.setChatroom(textrs.getString("chatroom"));
				tm.setDateString(textrs.getString("dateString"));
				tm.setColour(textrs.getString("colour"));
				user.sendTextMessage(tm); // send text message to the user
				textNotDone = textrs.next();
			}

		} else {
			while (fileNotDone) { // if there are still files to be
				fm.setFilename(filers.getString("filename"));
				fm.setDateString(filers.getString("dateString"));
				fm.setSender(filers.getString("sender"));
				fm.setColour(filers.getString("colour"));
				fm.setMessage(filers.getString("file"));
				user.sendFileMessage(fm); // send file message to the user
				fileNotDone = filers.next();
			}
		}

		dbo.closeConnection(); // close database connection

		/*
		 * send a users message to all users in the chatroom with updated user
		 * Ids
		 */
		usersMessage = new UsersMessage(user.getChatroom().getAllUserIds());
		user.getChatroom().sendUsersMessage(usersMessage);

	}

	@OnError
	public void handleError(Throwable t) {
		/* triggered when an error occurs */
		t.printStackTrace();
	}

}