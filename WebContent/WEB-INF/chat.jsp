<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chat</title>

<link type="text/css" rel="stylesheet" href="styles/chatstyle.css" />

<!-- import javascript libraries for hashing and encryption -->
<script
	src="scripts/sha1.js"></script>
<script
	src="scripts/aes.js"></script>
	
<script>
	// functions to return the username and chatroom of the user
	function getUsername() {
		var username = "${username}";
		return username;
	}

	function getChatroomName() {
		var chatroom = "${chatroom_name}";
		return chatroom;
	}
</script>

</head>
<body>

	<!-- JSTL to do if else -->
	<c:choose>
		<c:when test="${chatroom_join != null}">
			<script>
				// check password that user enters
				var pass = prompt("Enter password for ${chatroom_join}");
				if (CryptoJS.SHA1(pass) != "${actualpass}") {
					window.alert("Incorrect Password");
					window.location.href = "home";
				}
			</script>
		</c:when>
		<c:otherwise>
			<script>
				// pass is used as the passphrase to generate the encryption key
				var pass = "${password}";
			</script>
		</c:otherwise>
	</c:choose>

	<!-- import javascript file -->
	<script src="scripts/chatscripts.js"></script>


	<!-- header div -->
	<div id="header">
		<h1>chatroom</h1>
		<div class="loggedin">
			You are logged in as <span style="color: #c000ff;">${username}</span>
			in chatroom <span style="color: #c000ff;">${chatroom_name}</span>
			<a href="logout"><button class="logout" >Logout</button></a>
		</div>
	</div>


	<!-- files div -->
	<div id="files">
		<div class="imagesHead">chatroom files</div>
	</div>


	<!-- images div -->
	<div id="images">
		<div class="imagesHead">chatroom images</div>
	</div>


	<!-- chat div -->
	<div id="chat">
		<div id="rightchat">
			<!-- colour select -->
			<p style="font-size: 85%;">
				message & background colour <select id="bgselect"
					onchange="bgChange()">
					<option selected="selected" value="#c0c0c0">default</option>
					<option value="#f7b2df">pink</option>
					<option value="#ccffff">blue</option>
					<option value="#ff5c00">orange</option>
					<option value="#99ff00">green</option>
					<option value="#ffff00">yellow</option>
					<option value="#ad3e96">purple</option>
				</select>
			</p>
		</div>

		<p style="font-weight: bold;">
			<a href="home">Home</a>
		</p>

		<input id="filename" type="file" />

		<button onclick="uploadImage()">Send File</button>

		<!-- loader image displays when file message is being sent -->
		<img id="loader"
			src="https://danu6.it.nuigalway.ie/seanswatchescom/images/spinner.gif" />

		<div id="chatbox">

			<!-- messages display box  -->
			<div id="messagesDisplay" contenteditable="false"></div>

			<div id="usersTextArea" contenteditable="false"></div>
			<br /> <input id="messagetextField"
				placeholder="Enter message here..." type="text" size="75"
				onkeypress="return sendKeyPress(event)" maxlength="250" /> <input
				type="button" value="Send" id="sendButton" onclick="send()" />
		</div>
	</div>

</body>
</html>