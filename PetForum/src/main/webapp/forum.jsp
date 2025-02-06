<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="beans.Post" %>
<%@ include file="WEB-INF/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to the Forum</title>
    <style>
        body 
        {
		    font-family: 'Georgia', serif; 
		    background-color: #2C422D; 
		    color: #23363D; 
		    margin: 0;
		    padding: 20px;
		    font-size: 18px;
        }
        .title 
        {
			font-size: 1.5em; 
		}
        .content 
        { 
        	margin: 10px 0; 
        }
        .timestamp 
        { 
        	font-size: 0.8em; color: gray; 
        }
        .delete-btn 
        { 
        	position: absolute; 
        	top: 10px; 
        	right: 10px; 
        	background-color: red; 
        	color: white; 
        	border: none; 
        	cursor: pointer; 
        }
        .hidden 
        { 
        	display: none; 
        }
        .forum-container
        {
        	max-width: 900px;
		    margin: 0 auto;
		    padding: 30px;
		    background-color: #ffffff;
		    border-radius: 15px;
		    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
		    border: 1px solid #e0e0e0;
        }
        .forum-header h1
        {
		    text-align: center;
	    }
	    .button, .nav-buttons a
	    {
	    	color: #ffffff;
	    	border: none;
	    	border-radius: 8px;
			padding: 12px 25px;
	    	cursor: pointer;
	    	font-size: 1em;
	    	margin: 10px 0px;
	    }
	    #my-account-button
	    {
	    	background-color: #4E7CD9;
	    }
	    #submit-button
	    {
	    	background-color: #1E4210;
	    }
	    #cancel-button
	    {
	    	background-color: #8B2F2F;
	    }
	    #new-post-button
	    {
    		background-color: #1E4210;
	    }
	    #doctor
	    {
	    	background-color: black;
	    	text-decoration: none;
	    }
	    #post-form textarea{
			width: 100%;
			height: 150px;
			padding: 12px 20px;
			box-sizing: border-box;
			border: 2px solid #ccc;
			border-radius: 4px;
			background-color: #f8f8f8;
			font-size: 16px;
			resize: none;
        }
        #post-form input[type="text"]{
			width: 50%;
			padding: 2px;
			box-sizing: border-box;
			border: 2px solid #ccc;
			border-radius: 4px;
			background-color: #f8f8f8;
			font-size: 16px;
			resize: none;
        }
	    #post-list
	    {
	    	background-color: #B5B3B6;
	    	border: 1px solid #e6e6e6;
			border-radius: 12px;
	    	padding: 25px;
	    	margin-top: 20px;
	    	margin-bottom: 20px;
	    }
	    .post
	    {
	    	background-color: white;
	    	border: 1px solid #e6e6e6;
			border-radius: 12px;
	    	padding: 25px;
	    	margin: 10px;
	    	position: relative; 
	    	cursor: pointer
	    }
        .post:hover 
        {
        	background-color: #6698D9;
        }
    </style>
    <script>
        // Function to toggle between post list and post creation form
        function togglePostForm() {
            var postList = document.getElementById("post-list");
            var postForm = document.getElementById("post-form");
            var postButton = document.getElementById("new-post-button")
            postList.classList.toggle("hidden");
            postForm.classList.toggle("hidden");
            postButton.classList.toggle("hidden");
        }

        // Function to confirm deletion
        function confirmDelete(postId) {
        	console.log('forum?deleteId='+postId);
            if (confirm("Are you sure you want to delete this post?")) {
                fetch('forum?deleteId='+postId, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }).then(response => {
                    if (response.ok) {
                        return response.text();
                    } else if (response.status === 401) {
                        throw new Error("Invalid user type.");
                    } else {
                        throw new Error("Delete failed.");
                    }
                }).then(data => {
                    window.location.href = "forum"; // Redirect to forum
                }).catch(error => {
                    alert(error.message); // Show error message to user
                });
            }
        }
        
        // validate input
        function validateInput() {
            // get fields
            const title = document.getElementById("title").value;
            const content = document.getElementById("content").value;

            // check empty
            if (title === "" || content === "") {
                alert("Title and content cannot be empty");
                return false;
            }
            // Using api to formalize the characters (preventing injection)

            // add addtional validation here

            // validate success and send
            postNewPost(title, content);
        }

        function postNewPost(title, content) {
            fetch('forum', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'title': title,
                    'content': content
                })
            }).then(response => {
                if (response.ok) { // sent back success response 
                    return response.text();
                } else {
                    throw new Error("Post failed.");
                }
            }).then(data => {
                window.location.href = "forum"; // Redirect to forum or login page
            }).catch(error => {
                alert(error.message); // Show error message to user
            });
        }
        
        function enterPost(postId) {
        	// console.log(postId);
        	// console.log(typeof postId);
            window.location.href = "post?postId="+postId;
        }
        
    </script>
</head>
<body>
	<div class="forum-container">
		<div class="forum-header">
			<h1>DISCUSSION FORUM</h1>
		    <h2>Welcome, <%= request.getAttribute("username") %>!</h2>
		</div>
		
		<div class="nav-buttons">
	    	<button id="new-post-button" class="button" onclick="togglePostForm()">Write a new post</button>
	    </div>
	  
	    <!-- new post(initially hidden) -->
	    <div id="post-form" class="hidden">
	        <h2>Create a new Post</h2>
	        <form onsubmit="event.preventDefault(); validateInput();">
	            <label for="title">Title:</label>
	            <input type="text" id="title" name="title" required><br><br>
	            <label for="content">Content:</label><br>
	            <textarea id="content" name="content" rows="10" cols="50" required></textarea><br><br>
	            <button id="submit-button" class="button" type="submit">Submit</button>
	            <button id="cancel-button" class="button" type="button" onclick="togglePostForm()">Cancel</button> <!-- Button to go back to post list -->
	        </form>
	    </div>
	
	    <!-- Post list -->
	    <div id="post-list">
	        <% 
	            List<Post> posts = (List<Post>) request.getAttribute("posts");
	            if (posts != null) {
	                for (Post post : posts) {
	        %>
	            <div class="post" onclick="enterPost(<%= post.getId() %>)">
	                <div class="title"><%= post.getTitle() %></div>
	                <div class="content"><%= post.getContent() %></div>
	                <div class="timestamp">Created by: <%= post.getPoster() %> at: <%= post.getCreatedAt() %></div>
	                <div class="timestamp">Updated at: <%= post.getUpdatedAt() %></div>
	
	                <!-- Show delete button for admins only -->
	                <% if (role == 1) { %> 
	                    <button class="delete-btn" onclick="confirmDelete(<%= post.getId() %>)">Delete</button>
	                <% } %>
	            </div>
	        <% 
	                }
	            } else {
	        %>
	            <p>No posts available.</p>
	        <% 
	            }
	        %>
	    </div>
    </div>
</body>
</html>
