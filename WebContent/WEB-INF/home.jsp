<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>

<title>Home</title>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link type="text/css" rel="stylesheet" href="styles/homestyle.css" />

</head>
<body>

	<!-- header div -->
	<div id="header">
		<h1>chatroom</h1>
		<div class="loggedin">
			You are logged in as <span style="color: #c000ff;">${username}</span>
			<a href="logout"><button class="logout" >Logout</button></a>
		</div>
	</div>

	<!-- JOIN CHATROOM DIV -->
	<div id="join">
		<h2>join a chatroom</h2>
		<br />

		<!-- use JSTL to iterate through chatrooms list and output correct display to user -->
		<c:choose>
			<c:when test="${fn:length(Chatrooms) gt 0}">
				<table>
					<tr>
						<th>chatroom</th>
						<th>admin</th>
					</tr>
					<form method="post" action="home">

						<c:forEach var="cr" items="${Chatrooms}">

							<tr>
								<td>${cr.getName()}</td>
								<td>${cr.getAdmin()}</td>
								<td><input type="submit" name="submit" method="post"
									value="Join ${cr.getName()}"></td>

								<c:if test="${username == cr.getAdmin()}">
									<td><input type="submit" name="submit2" method="post"
										value="Delete ${cr.getName()}"></td>

								</c:if>

							</tr>

						</c:forEach>
					</form>
				</table>

			</c:when>
			<c:otherwise>	
	There are no chatrooms available :(
	</c:otherwise>
		</c:choose>

	</div>


	<!-- CREATE CHATROOM DIV contains form to create a chatroom and a form to delete user account-->
	<div id="create">
		<h2>create a chatroom</h2>
		<h4>To create a chatroom enter a name and set a password.</h4>
		<form method="post" action="home">
			<p style="font-size: 14px;">max length of chatroom name is 8
				characters</p>

			<p>
				chatroom name<br /> <input type="text" name="chatroom_name"
					size="25" required maxLength="8">
			</p>

			<p style="font-size: 14px;">password must contain at least 6
				characters with at least 1 number.</p>

			<p>
				password<br /> <input type="password" name="password" size="25"
					pattern="(?=.*\d)(?=.*[a-z]).{6,}" size="25" required>
			</p>

			<p>
				confirm password<br /> <input type="password" name="cpassword"
					pattern="(?=.*\d)(?=.*[a-z]).{6,}" size="25" required>
			</p>
			<input type="submit" value="Create Chatroom">
		</form>

		<br />

		<form method="post" action="home">
			<input type="submit" value="Delete User Account" name="delete_acc">
		</form>
	</div>


	<!-- check if the error session variable has been set and display message to user if it has -->
	<c:if test="${error != null }">
		<script>
			var error = "${error}";
			alert(error);
		</script>
		<%
			session.setAttribute("error", null);
		%>
	</c:if>

	<c:if test="${success != null }">
		<script>
			var success = "${success}";
			alert(success);
		</script>
		<%
			session.setAttribute("success", null);
		%>
	</c:if>

</body>
</html>