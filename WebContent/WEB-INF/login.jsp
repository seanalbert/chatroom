<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>

<link type="text/css" rel="stylesheet" href="styles/loginstyle.css">
</head>
<body>

<!-- header div -->
	<div id="header">
		<h1>chatroom</h1>
	</div>
	
	<!-- main div contains login form -->
	<div class=main>
		<div id="login">
			<form method=post action="j_security_check">
				<h3>Enter your details to login.</h3>
				<p>
					<span>Username:</span> <br /> <input type="text" name="j_username"
						required>
				</p>
				<p>
					<span>Password:</span> <br /> <input type="password"
						name="j_password" required>
				</p>
				<p>
					<input type="submit" value="Login">
				</p>
			</form>
			<p>
				Not a user? <a href="register">Register here.</a>
			</p>
		</div>
	</div>
	
	
	<!-- check if the error or success session variable has been set and display message to user if it has -->
	<c:choose>
		<c:when test="${error != null }">
			
			<script>
				alert("${error}");
			</script>
			
			<%
				session.setAttribute("error", null);
			%>
		</c:when>
		<c:when test="${success != null }">

			<script>
				alert("${success}");
			</script>
			<%
				session.setAttribute("success", null);
			%>

		</c:when>
	</c:choose>



</body>
</html>