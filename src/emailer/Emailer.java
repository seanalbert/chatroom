package emailer;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import properties.SystemProperties;

/**
 * Class to handle sending of the email to verify a users account
 */

public class Emailer implements SystemProperties {

	private Session session; // javax.mail.Session

	/*
	 * emailUsername and emailPassword are username and password for gmail
	 * account
	 */
	public Emailer(String emailUsername, String emailPassword) {

		Properties props = new Properties(); // properties object to initialise
												// the session

		props.put("mail.smtp.auth", "true"); // require authentication
		props.put("mail.smtp.starttls.enable", "true"); // enable TLS
		props.put("mail.smtp.host", "smtp.gmail.com"); // use gmail as the host
		props.put("mail.smtp.port", "587"); // set port number

		/* initialise session if email account authentication is successful */
		session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailUsername, emailPassword);
			}
		});
	}

	public void sendEmail(String to, String subject, String content) {

		try {

			/* create a message object and set the parameters */
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(content);

			Transport.send(message); // send the email

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
