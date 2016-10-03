package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.DatabaseOperation;
import properties.SystemProperties;

/**
 * This servlet processes users requests from the chat page
 *
 */

@WebServlet("/chat")
public class Chat extends HttpServlet implements SystemProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DatabaseOperation dbo;
	int active; // varibale to determine if user has verified account

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* check of active session variable has been set */
		if (request.getSession().getAttribute("activeUser") == null) {

			try {
				/*
				 * if it hasnt been set read the value from the database and set
				 * the active as a session variable
				 */
				dbo = new DatabaseOperation();
				active = dbo.getActive(request.getUserPrincipal().getName().toString());

				request.getSession().setAttribute("activeUser", active);

			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}

		/* check if user is registered */
		if ((int) request.getSession().getAttribute("activeUser") == 1) {
			
			/* if chatroom_name has not been set redirect to home page */
			if (request.getSession().getAttribute("chatroom_name") != null) {

				/*
				 * check if user is joining a chatroom or creating a new
				 * chatroom. If they are creating a chatroom there is no need to
				 * check the password
				 */
				if ((String) request.getSession().getAttribute("chatroom_join") != null) {

					String chatroom_join = (String) request.getSession().getAttribute("chatroom_join");
					String actualpass = "";

					try {

						/* read chatroom password from database and forward user to chat page*/
						dbo = new DatabaseOperation();
						actualpass = dbo.getChatroomPassword(chatroom_join);
						
						request.getSession().setAttribute("actualpass", actualpass);
						request.getRequestDispatcher("WEB-INF/chat.jsp").forward(request, response);

					} catch (SQLException | ClassNotFoundException e1) {
						e1.printStackTrace();
					}

				} else {

					/* forward user to chat page */
					request.getRequestDispatcher("WEB-INF/chat.jsp").forward(request, response);
				}

			} else {
				response.sendRedirect("home"); // redirect to home
			}
		} else {

			/* if user is unverified call logout servlet */
			request.getSession().setAttribute("unverified", true);
			request.getRequestDispatcher("logout").forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}