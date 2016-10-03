package junit;

import static org.junit.Assert.*;

import java.util.HashSet;

import javax.websocket.DecodeException;
import javax.websocket.EncodeException;

import org.junit.Test;

import database.DatabaseConnectionPool;
import datastructures.Chatroom;
import datastructures.TextMessage;
import datastructures.UserSession;
import remoteendpoint.MessageDecoder;
import remoteendpoint.MessageEncoder;

public class Tester {

	@Test
	public void testSingleton() {

		DatabaseConnectionPool dcp1 = DatabaseConnectionPool.getInstance();
		DatabaseConnectionPool dcp2 = DatabaseConnectionPool.getInstance();

		assertEquals(dcp1, dcp2);
	}

	@Test
	public void testChatroom() {

		Chatroom c1 = new Chatroom("name", "admin");
		Chatroom c2 = new Chatroom("name2", "admin2");

		assertTrue(Chatroom.chatrooms.contains(c1));
		assertTrue(Chatroom.chatrooms.contains(c2));

		Chatroom.removeChatroom("name");

		assertFalse(Chatroom.chatrooms.contains(c1));
		assertTrue(Chatroom.chatrooms.contains(c2));
		
		Chatroom c = Chatroom.getChatroomByName("name2");
		assertTrue(c == c2);
	}
	
	@Test
	public void testChatroomUsers() {

		Chatroom c3 = new Chatroom("name", "admin");

		UserSession cc1 = new UserSession();
		cc1.setUsername("cc1");
		UserSession cc2 = new UserSession();
		cc2.setUsername("cc2");

		c3.addUser(cc1);
		c3.addUser(cc2);

		HashSet<String> set = new HashSet<String>();
		set.add("cc1");
		set.add("cc2");

		assertEquals(c3.getAllUserIds(), set);

		c3.removeUser(cc1);
		set.remove("cc1");

		assertEquals(c3.getAllUserIds(), set);
	}
	
	@Test
	public void testEncoderDecoder() {

		try {
			MessageEncoder me = new MessageEncoder();
			TextMessage tm = new TextMessage();
			tm.setSender("sender");
			tm.setMessage("message");
			tm.setColour("colour");
			tm.setFirst(true);
			tm.setDateString("dateString");
			tm.setChatroom("chatroom");
			
			String expected = "{\"messageType\":\"TextMessage\",\"sender\":\"sender\","
					+ "\"message\":\"message\",\"chatroom\":\"chatroom\",\"dateString\":\"dateString\","
					+ "\"first\":true,\"colour\":\"colour\"}";

			assertEquals(me.encode(tm), expected);
			
			MessageDecoder md = new MessageDecoder();			
			TextMessage tm2 = (TextMessage) md.decode(expected);
	
			assertEquals(tm.getChatroom(), tm2.getChatroom());
			assertEquals(tm.getSender(), tm2.getSender());
			assertEquals(tm.getMessage(), tm2.getMessage());
			assertEquals(tm.getColour(), tm2.getColour());
			assertEquals(tm.getFirst(), tm2.getFirst());
			assertEquals(tm.getDateString(), tm2.getDateString());			
			
		} catch (EncodeException | DecodeException e) {
			e.printStackTrace();
		}
	}

}
