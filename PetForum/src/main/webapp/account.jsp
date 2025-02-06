<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="beans.Post" %>
<%@ include file="WEB-INF/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Account</title>
    <style>
        body { 
        	font-family: 'Georgia', serif; 
		    background-color: #2C422D; 
		    color: #23363D; 
		    margin: 0;
		    padding: 20px;
		    font-size: 18px;
        }
        .title { 
        	font-size: 1.5em; 
        }
        .content { 
        	margin: 10px 0; 
        }
        .timestamp { 
        	font-size: 0.8em; color: gray; 
        }
        .delete-btn { 
	        position: absolute; 
	        top: 10px; right: 10px; 
	        background-color: red; 
	        color: white; 
	        border: none; 
	        cursor: pointer; 
        }
        .hidden { 
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
        
		.logout-btn {
	        background-color: red; 
			margin-right: 10px;
            padding: 10px 20px;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
		}
		
		.logout-btn:hover {
		    background-color: #c0392b;
		}
    </style>
    <script>

        // Function to confirm deletion
        function confirmDelete(postId) {
        	console.log('account?deleteId='+postId);
            if (confirm("Are you sure you want to delete this post?")) {
                fetch('account?deleteId='+postId, {
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
			<h1>MY POSTS</h1>
		    <h2>Welcome, <%= request.getAttribute("username") %>!</h2>
		    <form action="account?logout=true" method="post">
	        	<button type="submit" class="logout-btn">Logout</button>
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
	                <div class="timestamp">Created by: <%= post.getPoster() %> at: <%= post.getCreatedAt() %></div>
	                <div class="timestamp">Updated at: <%= post.getUpdatedAt() %></div>
	
	                <!-- Show delete button for admins only -->
	                
	                    <button class="delete-btn" onclick="confirmDelete(<%= post.getId() %>)">Delete</button>
	               
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
