<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<link type="text/css" rel="stylesheet" href="styles/registerstyle.css">
</head>
<body>

	<!-- header div -->
	<div id="header">
		<h1>chatroom</h1>
	</div>

	<!-- main div contains registration form -->
	<div class="main">
		<div id="register">
			<h3>Choose a username and set a password to create a user
				profile.</h3>
			<form method="post" action="register">
				<p style="font-size: 14px;">max length of username is 8
					characters</p>
				<p>
					Username:<br /> <input type="text" name="username" size="25"
						required maxlength="8">
				</p>
				<p>
					Email:<br /> <input type="email" name="email" size="25" required>
				</p>
				<p style="font-size: 14px;">password must contain at least 6
					characters with at least 1 number.</p>
				Password:<br /> <input type="password" name="password"
					pattern="(?=.*\d)(?=.*[a-z]).{6,}" size="25" required>
				<p>
					Confirm Password:<br /> <input type="password" name="cpassword"
						pattern="(?=.*\d)(?=.*[a-z]).{6,}" size="25" required>
				</p>
				<p>
					<input type="submit" value="Register" method="post">
				</p>
			</form>

			<a href="home">Back to Login</a>

			<!-- check if the error session variable has been set and display message to user if it has -->
			<c:choose>

				<c:when test="${error != null }">

					<script>
						alert("${error}");
					</script>
					
					<!-- unset the error variable  -->
					<%
						session.setAttribute("error", null);
					%>
				</c:when>
			</c:choose>
		</div>
	</div>
</body>
</html>