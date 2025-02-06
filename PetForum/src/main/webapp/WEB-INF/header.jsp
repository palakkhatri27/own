<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" %>
<!DOCTYPE html>
<html>
<head>
    <style>
        body, html {
            margin: 0;
            padding: 0;
        }

        .navbar {
            background-color: #FBDAB9;
            overflow: hidden;
            width: 100%;
            position: fixed;
            top: 0;
            left: 0;
            z-index: 1000;
            height: 50px;
        }

        .navbar a {
            float: left;
            display: block;
            color: #2C422D;
            text-align: center;
            padding: 16px 20px;
            text-decoration: none;
            font-size: 18px;
            transition: background-color 0.3s ease;
        }

        .navbar a:hover {
            background-color: #80a68b;
            color: #ffffff;
        }

        .navbar a.active {
            background-color: #3a6d47;
            color: #ffffff;
        }
        .content {
            padding-top: 50px;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <a href="<%= request.getContextPath() %>/forum">Forum</a>

        <%
            int loggedUser = (Integer) session.getAttribute("user_id");
			int role = (Integer) request.getAttribute("role"); // Get logged user's role
        %>
        <a href="account?userId=<%= loggedUser %>">My Account</a>

        <a href="ai_doctor.jsp">AI Doctor</a>
        <a href="map.jsp">Event Map</a>
    </div>
    <div class="content">
    </div>
</body>
</html>
