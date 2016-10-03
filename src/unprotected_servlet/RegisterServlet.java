package unprotected_servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import database.DatabaseOperation;
import emailer.Emailer;
import properties.SystemProperties;
import java.security.SecureRandom;
import java.math.BigInteger;

/**
 * This servlet processes requests from the register page
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet implements SystemProperties {

	private static final long serialVersionUID = 1L;

	/* declare variables */
	DatabaseOperation dbo;
	SecureRandom random;
	Emailer emailer;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* forward to register page */
		request.getRequestDispatcher("WEB-INF/register.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/* get session variables */
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String cpassword = request.getParameter("cpassword");

		if (username != null && username.length() > 0 && email != null && email.length() > 0 && password != null
				&& password.length() > 0 && cpassword != null && cpassword.length() > 0) { // check
																							// variables

			if (password.equals(cpassword)) { // check passwords match

				try {
					password = DigestUtils.shaHex(password); // hash password

					/* create a random hash to use for account verification */
					random = new SecureRandom();
					String hash = DigestUtils.md5Hex(new BigInteger(130, random).toString(32));

					dbo = new DatabaseOperation();

					if (dbo.emailExists(email)) { // check if email already
													// exists in database and
													// throw exception if it
													// does
						throw new SQLException();
						
					} else {

						dbo.insertUser(username, password, email, hash, 0);
						dbo.insertUserRole(username, "user");

						/*
						 * pass the email and password to the gmail account
						 * being used to the emailer onject
						 */
						Emailer emailer = new Emailer("seanmoran827@gmail.com", "bellx1x2");
						emailer.sendEmail(email, "Email confirmation",
								"Dear " + username + "," + "\n\n Click the link below to activate your account."
										+ "\n\n" + URL + "verify?email=" + email + "&hash=" + hash
										+ "\n\nThanks.");

						/* set success attribute and redirect to home */
						request.getSession().setAttribute("success",
								"Registered successfully ! A confirmation email has been sent to the email you provided. Follow "
										+ "the link in the email to validate your account and start using the chatroom!");
						response.sendRedirect("home");
					}

				} catch (SQLException | ClassNotFoundException e) {

					/* handle error */
					request.setAttribute("error",
							"Username and/ or email is already taken please choose a different one");
					request.getRequestDispatcher("WEB-INF/register.jsp").forward(request, response);
				}

			} else {

				/* handle error */
				request.setAttribute("error", "Passwords do not match. Please try again.");
				request.getRequestDispatcher("WEB-INF/register.jsp").forward(request, response);

			}
		}
	}

}
