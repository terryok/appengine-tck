<%@ page import="com.google.appengine.api.channel.ChannelService" %>
<%@ page import="com.google.appengine.api.channel.ChannelServiceFactory" %>
<%--
  ~ Copyright 2013 Google Inc. All Rights Reserved.
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<%
    ChannelService channelService = ChannelServiceFactory.getChannelService();
    String channelId = request.getParameter("test-channel-id");
    String token = channelService.createChannel(channelId);
%>
<html>
<head>
<script type="text/javascript" src="/_ah/channel/jsapi"></script>
</head>
<body>
<script>
   function updateStatus() {
       document.getElementById("status").innerHTML="bye!";
   }

   function sendServerMessage() {
       var channelId = document.getElementById("channel-id").innerHTML;
       var echoMsg = Math.random().toString();

       var xmlhttp = new XMLHttpRequest();
       xmlhttp.open("GET","channelEcho.jsp?test-channel-id=" + channelId + "&echo=" + echoMsg, true);
       xmlhttp.send();
       document.getElementById("last-sent-message").innerHTML = echoMsg;
   }

   function onOpened() {
       document.getElementById("status").innerHTML="opened";
   }

   function onMessage(msg) {
       document.getElementById("status").innerHTML="msg:" + msg.data;
       document.getElementById("last-received-message").innerHTML = msg.data;
   }

   function onError(err) {
       document.getElementById("status").innerHTML="err:" + err;
   }

   function onClose() {
       document.getElementById("status").innerHTML="closed";
   }

   function doIt() {
    socket = channel.open();
    socket.onopen = onOpened;
    socket.onmessage = onMessage;
    socket.onerror = onError;
    socket.onclose = onClose;

   }

    channel = new goog.appengine.Channel('<%= token %>');

    //socket = channel.open();
    //socket.onopen = onOpened;
    //socket.onmessage = onMessage;
    //socket.onerror = onError;
    //socket.onclose = onClose;

</script>
<p id="channel-id"><%= channelId %></p>
<p id="status">never-set</p>
<p id="last-sent-message">never-set</p>
<p id="last-received-message">never-set</p>
<button id="status-button" type="button" onclick="updateStatus()">status</button>
<button id="send-message-button" type="button" onclick="sendServerMessage()">send server message</button>
<button id="doit-button" type="button" onclick="doIt()">doIt</button>

</body>
</html>