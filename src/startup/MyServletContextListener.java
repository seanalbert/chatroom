package startup;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import database.DatabaseOperation;
import datastructures.Chatroom;

/**
 * This methods within in this class are ran on startup and shutdown of the
 * server. ServletContextListener is an interface that gets notified about
 * ServletContext life cycle changes.
 *
 */

public class MyServletContextListener implements ServletContextListener {

	DatabaseOperation dbo;
	ResultSet rs;

	/* this method is called on shutdown of tomcat */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {

			dbo = new DatabaseOperation();
			dbo.closeConnectionPool(); // close connection pool on shutdown

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	/* this method is called on start up of tomcat */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			dbo = new DatabaseOperation();
			rs = dbo.getChatrooms(); // get details of chatrooms from the
										// database
			while (rs.next()) {
				/* create chatroom objects for each chatroom in database */
				new Chatroom(rs.getString("name"), rs.getString("admin"));
			}
			
			dbo.closeConnection();
		} catch (ClassNotFoundException | SQLException e) {

			e.printStackTrace();
		}

	}

}