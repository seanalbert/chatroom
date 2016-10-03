/**
 * 
 */
function initialize() {
	var endpoint = "/WebChatroom/chatEndpoint";
	if (window.location.protocol == "http:") {
		return new WebSocket("ws://" + window.location.host + endpoint); // create
		// websocket
		// connection
		// at
		// specified
		// endpoint
	} else {
		return new WebSocket("wss://" + window.location.host + endpoint);
	}
}

var webSocket = initialize();
var messageColour;

/* triggered when websocket is opened */
webSocket.onopen = function processOpen(message) {

	var loader = document.getElementById("loader");
	loader.style.visibility = "visible";

	var encryptedMsg = encrypt("Hi, I have joined the conversation."); // encrypt
	// the
	// welcome
	// message

	/* create json string and send to endpoint */
	webSocket.send(JSON.stringify({
		'message' : encryptedMsg,
		'sender' : getUsername(),
		'chatroom' : getChatroomName(),
		'dateString' : getDate(),
		'first' : true,
		'colour' : "default",
		'messageType' : "TextMessage"
	}));
};

/* triggered when there is an error */
webSocket.onerror = function processError(message) {
	console.log("error message " + message);
};

/* triggered when before user leaves the page */
window.onbeforeunload = function doSomething() {
	webSocket.close(); // close websocket
	return null;
};

webSocket.onclose = function(message) {
	console.log("closing websocket");
};

/* click sendButton when return button is pressed */
function sendKeyPress(e) {
	// look for window.event in case event isn't passed in
	e = e || window.event;
	if (e.keyCode == 13) {
		document.getElementById('sendButton').click();
		return false;
	}
	return true;
}

/* scroll down to the most recent message */
function autoScroll() {
	var textarea = document.getElementById("messagesDisplay");
	textarea.scrollTop = textarea.scrollHeight;
}

/* check if a string is in json format */
function isJson(str) {
	try {
		JSON.parse(str);
	} catch (e) {
		return false;
	}
	return true;
}

/* triggered when a message is recieved */
webSocket.onmessage = function processMessage(incomingMessage) {

	if (isJson(incomingMessage.data)) { // check if message is in json format

		var msgDisplay = document.getElementById("messagesDisplay");

		var jsonData = JSON.parse(incomingMessage.data);

		if (jsonData.messageType == "TextMessage") {
			/*
			 * here if incoming message is a TextMessage.. If its the first
			 * message received set the background and message colour
			 */
			if (jsonData.first == true && jsonData.sender == getUsername()) {
				document.getElementById("bgselect").value = jsonData.colour;
				bgChange();
			}

			/*
			 * if the message is from the user display it on the right hand side
			 * of the messages display, otherwise display it on the left.
			 */
			if (getUsername() == jsonData.sender) {

				msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"selfmessageBubble\" style=\"background-color:"
						+ jsonData.colour
						+ "\">"
						+ "<span class=\"message\">"
						+ decrypt(jsonData.message) // decrypt the message
						+ "</span><br/><span class=\"date\">"
						+ jsonData.dateString + "</span></div>";
			} else {
				msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"messageBubble\" style=\"background-color:"
						+ jsonData.colour
						+ "\"><span class=\"username\">"
						+ jsonData.sender
						+ ":</span>\t<span class=\"message\">"
						+ decrypt(jsonData.message) // decrypt the message
						+ "</span>  <br/>  <span class=\"date\">"
						+ jsonData.dateString + "</span></div>";
			}

		} else if (jsonData.messageType == "UsersMessage") {

			/* here if incoming message is a UsersMessage.. */

			var usersTextArea = document.getElementById("usersTextArea");
			usersTextArea.innerHTML = "";
			var i = 0;

			/* iterate through array and add elements to users display array */
			while (i < jsonData.users.length)
				usersTextArea.innerHTML += jsonData.users[i++] + "<br/>";

		} else if (jsonData.messageType == "FileMessage") {

			/* here if incoming message is a UsersMessage.. */

			var fileUrl; // variable to store URL to use as source for files
			// and images
			var loader = document.getElementById("loader");
			loader.style.visibility = "hidden";

			var urlCreator = window.URL || window.webkitURL;

			/*
			 * decrypt message and convert to arraybuffer. Create blob from the
			 * array buffer
			 */
			var blob = new Blob(
					[ stringToArrayBuffer(decrypt(jsonData.message)) ]);

			fileUrl = urlCreator.createObjectURL(blob); // use blob to create
			// url

			if (jsonData.filename.toLowerCase().indexOf("jpg") > -1
					|| jsonData.filename.toLowerCase().indexOf("png") > -1
					|| jsonData.filename.toLowerCase().indexOf("jpeg") > -1
					|| jsonData.filename.toLowerCase().indexOf("gif") > -1) { // check
				// if
				// its
				// an
				// image

				/*
				 * if the file message is from the user then display it on the
				 * right hand side of the messages display, otherwise display it
				 * on the left.
				 */
				if (getUsername() == jsonData.sender) {

					msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"selfmessageBubble\" style=\"background-color:"
							+ jsonData.colour
							+ "\">"
							+ "<img class=\"image\" download=\""
							+ jsonData.filename
							+ "\" src=\""
							+ fileUrl // use the created url as the source for
							// the image
							+ "\"></img></span><br/>"
							+ "<span class=\"date\">"
							+ jsonData.dateString + "</span></div>";
				} else {
					msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"messageBubble\" style=\"background-color:"
							+ jsonData.colour
							+ "\"><span class=\"username\">"
							+ jsonData.sender
							+ ":</span>\t<img class=\"image\" download=\""
							+ jsonData.filename
							+ "\" src=\""
							+ fileUrl // use the created url as the source for
							// the image
							+ "\"></img></span><br/>"
							+ "<span class=\"date\"> "
							+ jsonData.dateString
							+ "</span></div>";
				}

				/* add image to the images div of the chatroom */
				var image = document.createElement("img");
				var att = document.createAttribute("class");
				att.value = "image";
				image.setAttributeNode(att);
				image.src = fileUrl;
				var div = document.getElementById("images");
				div.appendChild(image);

			} else {

				/*
				 * Here if file is not an image... if the file message is from
				 * the user then display it on the right hand side of the
				 * messages display, otherwise display it on the left.
				 */

				if (getUsername() == jsonData.sender) {

					msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"selfmessageBubble\" style=\"background-color:"
							+ jsonData.colour
							+ "\">"
							+ "<a download=\""
							+ jsonData.filename
							+ "\"  href=\""
							+ fileUrl // use the created url as the source for
							// the image
							+ "\">"
							+ jsonData.filename
							+ "</a></span><br/><span class=\"date\">"
							+ jsonData.dateString + "</span></div>";
				} else {
					msgDisplay.innerHTML = msgDisplay.innerHTML += "<div class=\"messageBubble\" style=\"background-color:"
							+ jsonData.colour
							+ "\"><span class=\"username\">"
							+ jsonData.sender
							+ ":</span> <a  download=\""
							+ jsonData.filename
							+ "\"  href=\""
							+ fileUrl // use the created url as the source for
							// the image
							+ "\">"
							+ jsonData.filename
							+ "</a></span><br/><span class=\"date\">"
							+ jsonData.dateString + "</span></div>";
				}

				/*
				 * add the file to the files div of the chatroom and provide a
				 * download link.
				 */
				var a = document.createElement("a");
				var att = document.createAttribute("href");
				var linkText = document.createTextNode(jsonData.filename);
				a.appendChild(linkText);
				a.title = jsonData.filename;
				att.value = fileUrl;
				var att2 = document.createAttribute("download");
				var att3 = document.createAttribute("class");
				att2.value = jsonData.filename;
				att3.value = "file";
				a.setAttributeNode(att);
				a.setAttributeNode(att2);
				a.setAttributeNode(att3);
				var div = document.getElementById("files");
				div.appendChild(a);
			}
		}
	} else {
		console.log(incomingMessage.data);
	}

	autoScroll(); // autoscroll each time new message is received
	var loader = document.getElementById("loader");
	loader.style.visibility = "hidden";
}

function getColour() {
	return messageColour; // return the users message colour
}

function send() {

	if (messagetextField.value.length != 0) { // check if message has been
		// entered

		var encryptedMsg = encrypt(messagetextField.value); // encrypt the
		// message entered
		// by the user

		/* send the message as json string */
		webSocket.send(JSON.stringify({
			'sender' : this.getUsername(),
			'message' : encryptedMsg,
			'chatroom' : getChatroomName(),
			'dateString' : getDate(),
			'first' : false,
			'colour' : getColour(),
			'messageType' : "TextMessage"
		}));

		messagetextField.value = "";
	} else {
		alert("You have not entered a message to send....");
	}
}

function encrypt(message) {
	/*
	 * CryptoJS library used to encrypt all of the data. pass is the passphrase
	 * used to generate a key for encryption. It is set to the password of the
	 * chatroom that the user enters to join the chatroom
	 */
	var encrypted = CryptoJS.AES.encrypt(message, pass);
	return encrypted.toString();
}

function decrypt(message) {
	/*
	 * CryptoJS library used to decrypt all of the data. pass is the passphrase
	 * used to generate a key for encryption. It is set to the password of the
	 * chatroom that the user enter to enter the chatroom
	 */
	var decrypted = CryptoJS.AES.decrypt(message, pass);
	return decrypted.toString(CryptoJS.enc.Utf8).toString();
}

function uploadImage() {

	var reader = new FileReader();

	reader.onload = function() {

		rawData = reader.result; // rawData is an array buffer that contains
		// the chosen file

		/* convert the arraybuffer to string and encrypt */
		var encryptedFile = encrypt(arrayBufferToString(rawData));

		webSocket.send(JSON.stringify({ // send encrypted file with other
										// attributes in json format

			'sender' : getUsername(),
			'filename' : getFilename("filename"),
			'message' : encryptedFile,
			'chatroom' : getChatroomName(),
			'dateString' : getDate(),
			'colour' : getColour(),
			'messageType' : "FileMessage"
		}));

	};

	var input = document.getElementById("filename");

	if (!input.files[0]) { // check if file has been chosen
		alert("You have not chosen a file to send....");
	} else if (input.files[0].size > 1000000) { // check file size
		alert("file must be 1MB or less")
	} else {
		var loader = document.getElementById("loader");
		loader.style.visibility = "visible";
		reader.readAsArrayBuffer(input.files[0]); // read the chosen file into
		// an array buffer
	}
}

/* convert array buffer to string */
function arrayBufferToString(buffer) {

	var string = '';
	var bytes = new Uint8Array(buffer);
	var len = bytes.byteLength;
	for (var i = 0; i < len; i++) {
		string += String.fromCharCode(bytes[i]);
	}

	return string;
}

/* convert string to array buffer */
function stringToArrayBuffer(string) {

	var len = string.length;
	var bytes = new Uint8Array(len);
	for (var i = 0; i < len; i++) {
		bytes[i] = string.charCodeAt(i);
	}

	return bytes.buffer;
}

function getFilename(id) {

	/* return the filename of the file that has been chosen by the user */
	var fullPath = document.getElementById(id).value;

	if (fullPath) {
		var startIndex = (fullPath.indexOf('\\') >= 0 ? fullPath
				.lastIndexOf('\\') : fullPath.lastIndexOf('/'));
		var filename = fullPath.substring(startIndex);
		if (filename.indexOf('\\') === 0 || filename.indexOf('/') === 0) {
			filename = filename.substring(1);
		}
	}

	return filename;
}

function getDate() { // get the date in hh:mm dd/Mon format
	var date = new Date();
	var monthNames = [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
			"Sep", "Oct", "Nov", "Dec" ];
	var day = date.getDate();
	var month = date.getMonth();
	var year = date.getFullYear();
	var hour = date.getHours();
	var min = date.getMinutes();

	var minstr = min.toString();
	var hourstr = hour.toString();

	if (minstr.length == 1) {
		minstr = "0" + minstr;
	}

	if (hourstr.length == 1) {
		hourstr = "0" + hourstr;
	}
	return hourstr + ":" + minstr + " " + day + "/" + monthNames[month];
}

function bgChange() {
	/* called when the user selects a different colour */

	var myselect = document.getElementById("bgselect");
	var colour = myselect.options[myselect.selectedIndex].value;
	messageColour = colour;

	webSocket.send(JSON.stringify({ // send colour message to endpoint
		'colour' : colour,
		'messageType' : "ColourMessage"
	}));

	document.getElementById("chat").style.backgroundColor = colour; // update
	// background
	// colour
	myselect.blur();
}