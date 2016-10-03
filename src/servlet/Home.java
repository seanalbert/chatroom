package servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.codec.digest.DigestUtils;

import java.sql.ResultSet;

import database.DatabaseOperation;
import datastructures.Chatroom;
import properties.SystemProperties;

/**
 * This servlet processes users requests from the home page
 *
 */

@WebServlet("/home")
public class Home extends HttpServlet implements SystemProperties {

	DatabaseOperation dbo;
	ResultSet rs;
	String username;
	int active; // varibale to determine if user has verified account

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		username = request.getUserPrincipal().getName().toString();

		/* check of active session variable has been set */
		if (request.getSession().getAttribute("activeUser") == null) {

			try {
				/*
				 * if it hasnt been set read the value from the database and set
				 * the active as a session variable
				 */
				dbo = new DatabaseOperation();

				active = dbo.getActive(username);
				request.getSession().setAttribute("activeUser", active);

			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}

		/* check if user is registered */
		if ((int) request.getSession().getAttribute("activeUser") == 1) {

			/*
			 * set the username and chatrooms session variable. Chatrooms is a
			 * set containing all of the currently available chatrooms
			 */
			request.getSession().setAttribute("Chatrooms", Chatroom.chatrooms);
			request.getSession().setAttribute("username", username);			
			request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response); // forward
																							// to
																							// home
																							// page

		} else {
			/* if user is unverified call logout servlet */
			request.getSession().setAttribute("unverified", true);
			request.getRequestDispatcher("logout").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* get the various parameters entered by the user */
		String chatroom_join = null;
		String chatroom_delete = request.getParameter("submit2");
		chatroom_join = request.getParameter("submit");
		String chatroom_name = request.getParameter("chatroom_name");
		String password = request.getParameter("password");
		String cpassword = request.getParameter("cpassword");
		String delete_acc = request.getParameter("delete_acc");

		request.getSession().setAttribute("chatroom_join", chatroom_join);

		/* check variables are not null */
		if (chatroom_name != null && chatroom_name.length() > 0 && password != null && password.length() > 0
				&& cpassword != null && cpassword.length() > 0) {
			/* here if user is creating a chatroom */

			/*
			 * create pattern and matcher objects to check for special
			 * characters
			 */
			Pattern p = Pattern.compile("[^a-z0-9_ ]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = p.matcher(chatroom_name);

			if (!matcher.find()) { // if no special characters

				/* set the password and chatroom name session variables */
				request.getSession().setAttribute("password", password);
				request.getSession().setAttribute("chatroom_name", chatroom_name);

				if (password.equals(cpassword)) { // check if passwords entered
													// match
					try {

						password = DigestUtils.shaHex(password); // hash the
																	// password

						dbo = new DatabaseOperation();
						dbo.insertChatroom(chatroom_name, username, password); // insert
																				// chatroom
																				// to
																				// the
																				// database

						new Chatroom(chatroom_name, username); // create new
																// chatroom
																// object
						response.sendRedirect("chat"); // redirect user to chat
														// page

					} catch (SQLException | ClassNotFoundException e) {

						/* handle the exception */
						request.getSession().setAttribute("error",
								"There is already a chatroom with this name. Please choose a different name..");
						request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);
					}
				} else {
					/* handle the exception */
					request.getSession().setAttribute("error", "Passwords do not match..");
					request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);
				}
			} else {
				/* handle the exception */
				request.getSession().setAttribute("error", "Chatroom name cannot contain special characters..");
				request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);
			}
		} else if (chatroom_join != null && chatroom_join.length() > 0) {
			/* here if user is joining a chatroom */

			chatroom_join = chatroom_join.substring(5); // cut 'Join ' off the
														// string

			/* set the variables and redirect to to chat page */
			request.getSession().setAttribute("chatroom_name", chatroom_join);
			request.getSession().setAttribute("chatroom_join", chatroom_join);
			response.sendRedirect("chat");

		} else if (chatroom_delete != null && chatroom_delete.length() > 0) {
			/* here if user is deleting a chatroom */

			chatroom_delete = chatroom_delete.substring(7); // cut 'Delete ' off
															// the string

			try {

				dbo = new DatabaseOperation();
				dbo.deleteChatroom(chatroom_delete); // delete chatroom from
														// database

				Chatroom.removeChatroom(chatroom_delete); // remove chatroom
															// from set of
															// chatrooms

				// set success attribute and forward home page
				request.getSession().setAttribute("success", "Successful, " + chatroom_delete + " has been deleted !");
				request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);

			} catch (SQLException | ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		} else if (delete_acc != null && delete_acc.length() > 0) {
			/* here if user is deleting their user account */

			try {
				/* remove user from database */
				dbo = new DatabaseOperation();
				dbo.deleteUserAccount(request.getUserPrincipal().getName().toString());

				/* set success variable and redirect to logout servlet */
				request.getSession().setAttribute("delete", true);
				response.sendRedirect("logout");

			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();

				/* handle error and forward to homepage */
				request.getSession().setAttribute("error", "There was an error please try again...");
				request.getRequestDispatcher("WEB-INF/home.jsp").forward(request, response);
			}
		}
	}
}
