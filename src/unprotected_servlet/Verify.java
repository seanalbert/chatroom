package unprotected_servlet;

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
 * This servlet is used to complete the user verification process
 */
@WebServlet("/verify")
public class Verify extends HttpServlet implements SystemProperties {
	private static final long serialVersionUID = 1L;

	DatabaseOperation dbo;
	String dbhash;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		/* get session varaibles set in the url */
		String hash = request.getParameter("hash");
		String email = request.getParameter("email");

		try {
			dbo = new DatabaseOperation();
			dbhash = dbo.getHash(email);	// get the random hash that was generated for the user

			if (dbhash.equals(hash)) {	// check if the hash in url matches
				try {
					
					dbo = new DatabaseOperation();
					dbo.activateUser(email, 1);	// update user's active attribute

					request.getSession().setAttribute("success",
							"Successful your account has been verified. Click ok to login.");
					response.sendRedirect("home");	// redirect to home

				} catch (ClassNotFoundException | SQLException e) {

					/* handle error */
					request.setAttribute("error", "There was error. Please try again..");
					request.getRequestDispatcher("WEB-INF/register.jsp").forward(request, response);
				}

			}

		} catch (SQLException | ClassNotFoundException e) {

			/* handle error for invalid link */
			request.setAttribute("error", "This is an invalid confirmation link please try again..");
			request.getRequestDispatcher("WEB-INF/register.jsp").forward(request, response);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
