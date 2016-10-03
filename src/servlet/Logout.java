//Sean Moran
//11341021
package servlet;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import properties.SystemProperties;

/**
 * This servlet is used to log users out of their account. It is done by using
 * the invalidate() method
 *
 */

@WebServlet("/logout")
public class Logout extends HttpServlet implements SystemProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * session varaibles get deleted when the session is invalidated so use
	 * booleans to keep track
	 */
	
	HttpSession session;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		session = request.getSession();
		boolean success = false;
		boolean unverified = false;

		if (session.getAttribute("unverified") != null && (boolean) session.getAttribute("unverified")) {
			unverified = true;
		} else if (session.getAttribute("delete") != null && (boolean) session.getAttribute("delete")) {
			success = true;
		}

		session.invalidate(); // invalidate session

		if (success)
			request.getSession().setAttribute("success", "Successful, your account has been deleted !");
		else if (unverified)
			request.getSession().setAttribute("error", "You have not verified your email account..");

		response.sendRedirect("home"); // redirect to home page.
										// session login page will be presented
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}
}
