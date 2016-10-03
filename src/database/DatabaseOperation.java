package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;

import properties.SystemProperties;

/**
 * This class is responsible for all database operations that are carried out by the system
 *
 */

public class DatabaseOperation implements SystemProperties {

	/* declare variables */
	private DatabaseConnectionPool connectionPool;
	private Connection conn;
	private ResultSet rs;
	private Date d;
	private Timestamp timestamp;
	private PreparedStatement preparedStmt;

	/* constructor which gets the instance of a connection pool */
	public DatabaseOperation() throws ClassNotFoundException, SQLException {
		connectionPool = DatabaseConnectionPool.getInstance();
	}

	
	public void openConnection() throws SQLException {
		conn = connectionPool.getConnection();		// get a connection from the connection pool
	}

	
	public void closeConnectionPool() throws SQLException {
		connectionPool.closeDatapool();				// close the connection pool
	}

	
	public void closeConnection() throws SQLException {
		if (conn != null)
			conn.close();		// close the connection and return it to the pool
	}

	/*
	 *		INSERT OPERATIONS
	 *
	 */

	/* insert a new message and relevant details */
	public void insertMessage(String message, String sender, String chatroom, String dateString, String colour)
			throws SQLException {
		openConnection();
		d = new Date();
		timestamp = new Timestamp(d.getTime());
		String query = "insert into message (message, sender, chatroom, dateString, timstamp, colour) values (?, ?, ?, ?, ?, ?)";
		preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, message);
		preparedStmt.setString(2, sender);
		preparedStmt.setString(3, chatroom);
		preparedStmt.setString(4, dateString);
		preparedStmt.setTimestamp(5, timestamp);
		preparedStmt.setString(6, colour);
		preparedStmt.execute();
		closeConnection();
	}

	/* insert a new file and relevant details */
	public void insertFile(String file, String sender, String chatroom, String colour, String filename,
			String dateString) throws SQLException {
		openConnection();
		d = new Date();
		timestamp = new Timestamp(d.getTime());
		String query = "insert into file (file, sender, filename, chatroom, dateString, timstamp, colour) values (?, ?, ?, ?, ?, ?, ?)";
		preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, file);
		preparedStmt.setString(2, sender);
		preparedStmt.setString(3, filename);
		preparedStmt.setString(4, chatroom);
		preparedStmt.setString(5, dateString);
		preparedStmt.setTimestamp(6, timestamp);
		preparedStmt.setString(7, colour);
		preparedStmt.execute();
		closeConnection();
	}

	/* insert a new chatroom and relevant details */
	public void insertChatroom(String name, String admin, String password) throws SQLException {
		openConnection();
		String query = " insert into chatroom (name, admin, password)" + " values (?, ?, ?)";
		preparedStmt = conn.prepareStatement(query);
		preparedStmt.setString(1, name);
		preparedStmt.setString(2, admin);
		preparedStmt.setString(3, password);
		preparedStmt.execute();
		closeConnection();
	}

	/* insert a new user and relevant details */
	public void insertUser(String username, String password, String email, String hash, int active)
			throws SQLException {
		openConnection();
		String insertUser = " insert into tomcat_users (username, password, email, hash, active)"
				+ " values (?, ?, ?, ?, ?)";
		preparedStmt = conn.prepareStatement(insertUser);
		preparedStmt.setString(1, username);
		preparedStmt.setString(2, password);
		preparedStmt.setString(3, email);
		preparedStmt.setString(4, hash);
		preparedStmt.setInt(5, active);
		preparedStmt.execute();
		closeConnection();
	}

	/* insert a new user role */
	public void insertUserRole(String username, String role) throws SQLException {
		openConnection();
		String insertUserRole = " insert into tomcat_users_roles (username, rolename)" + " values (?, ?)";
		preparedStmt = conn.prepareStatement(insertUserRole);
		preparedStmt.setString(1, username);
		preparedStmt.setString(2, role);
		preparedStmt.execute();
		closeConnection();
	}

	/*
	 * 
	 * SELECT OPERATIONS 
	 */

	/* check of email address is already in use */
	public boolean emailExists(String email) throws SQLException {
		openConnection();
		email = StringEscapeUtils.escapeJava(email);		// escape string to SQL injection attacks
		String selectEmail = "select email from tomcat_users where email like '" + email + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(selectEmail);
		if (rs.next())
			return true;
		else
			return false;
	}

	/* get password for specified chatroom */
	public String getChatroomPassword(String chatroom) throws SQLException {
		openConnection();
		chatroom = StringEscapeUtils.escapeJava(chatroom);		// escape string to SQL injection attacks
		String getPassword = "select password from chatroom where name like '" + chatroom + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getPassword);
		rs.next();
		String returnStr = rs.getString("password");
		closeConnection();

		return returnStr;
	}

	/* get all files from specified chatroom */
	public ResultSet selectFile(String c) throws SQLException {
		openConnection();
		c = StringEscapeUtils.escapeJava(c);		// escape string to SQL injection attacks
		String getFile = "select file, filename, dateString, sender, timstamp, colour from file where chatroom like '"
				+ c + "' order by timstamp";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getFile);

		return rs;
	}

	/* get a users selected colour */
	public String getColour(String username) throws SQLException {
		openConnection();		
		username = StringEscapeUtils.escapeJava(username);	// escape string to SQL injection attacks
		String getColour = "select colour from tomcat_users where username like '" + username + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getColour);
		rs.next();
		String returnStr = rs.getString("colour");
		closeConnection();

		return returnStr;
	}

	/* get a users hash  */
	public String getHash(String email) throws SQLException {
		openConnection();		
		email = StringEscapeUtils.escapeJava(email);	// escape string to SQL injection attacks
		String getHash = "select hash from tomcat_users where email like '" + email + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getHash);
		rs.next();
		String hash = rs.getString("hash");
		closeConnection();
		return hash;
	}

	/* get the active attribute of a user 1=verified 0=unverified */
	public int getActive(String username) throws SQLException {
		openConnection();
		String getActive = "select active from tomcat_users where username like '" + username + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getActive);
		rs.next();
		int active = rs.getInt("active");
		rs.close();
		closeConnection();
		return active;
	}

	/* get all chatrooms in database */
	public ResultSet getChatrooms() throws SQLException {
		openConnection();
		String getChatroomNames = "select name, admin from chatroom";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getChatroomNames);
		return rs;
	}

	/* get all messages in the specified chatroom */
	public ResultSet selectMessage(String chatroom) throws SQLException {
		openConnection();
		chatroom = StringEscapeUtils.escapeJava(chatroom);
		String getMessage = "select sender, message, chatroom, dateString, timstamp, colour from message where chatroom like '"
				+ chatroom + "'";
		Statement statement = conn.createStatement();
		rs = statement.executeQuery(getMessage);
		return rs;
	}

	/*
	 * UPDATE OPERATIONS
	 *
	 */

	/* update a users colour */
	public void updateColour(String username, String colour) throws SQLException {
		openConnection();
		username = StringEscapeUtils.escapeJava(username);
		colour = StringEscapeUtils.escapeJava(colour);
		String updateColour = "update tomcat_users set colour = '" + colour + "' where username like '" + username
				+ "'";
		Statement statement = conn.createStatement();
		statement.executeUpdate(updateColour);
		closeConnection();
	}

	/* set a the active attribute of a user when they verify the email account */
	public void activateUser(String email, int i) throws SQLException {
		openConnection();
		String activateUser = "update tomcat_users set active = '" + 1 + "' where email like '" + email + "'";
		Statement statement = conn.createStatement();
		statement.executeUpdate(activateUser);
		closeConnection();
	}

	/*
	 * DELETE OPERATIONS
	 * 
	 */

	/* delete the specified chatroom from the database */
	public void deleteChatroom(String chatroom) throws SQLException {
		openConnection();
		String deleteChatroom = "delete from chatroom where name like '" + chatroom + "'";
		Statement statement = conn.createStatement();
		statement.executeUpdate(deleteChatroom);
		closeConnection();
	}

	/* delete the user account with the specified username */
	public void deleteUserAccount(String username) throws SQLException {
		openConnection();
		username = StringEscapeUtils.escapeJava(username);
		String deleteUserAccount = "delete from tomcat_users where username like '" + username + "'";
		Statement statement = conn.createStatement();
		statement.executeUpdate(deleteUserAccount);
		closeConnection();
	}
}
