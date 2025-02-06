<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="beans.Comment" %>
<%@ page import="beans.Post" %>
<%@ include file="WEB-INF/header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Post Details</title>
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
	        font-size: 2em; 
	        font-weight: bold; 
        }
        .content { 
        	margin: 10px 0; 
        }
        .button {
	    	color: #ffffff;
	    	border: none;
	    	border-radius: 8px;
			padding: 12px 25px;
	    	cursor: pointer;
	    	font-size: 1em;
	    	margin: 10px 0px;
	    }
	    #delete-btn {
	    	position: absolute; 
	        top: 10px; right: 10px; 
	        background-color: red; 
	        color: white; 
	        border: none; 
	        cursor: pointer; 
	    }
	    .submit-btn {
	    	background-color: #1E4210;
	    }
	    .back-btn {
	    	background-color: #1E4210;;
	    }
        .timestamp { 
	        font-size: 0.8em; 
	        color: gray; 
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
	    }
        .comment-section { 
        	background-color: #B5B3B6;
	    	border: 1px solid #e6e6e6;
			border-radius: 12px;
	    	padding: 25px;
	    	margin-top: 20px;
	    	margin-bottom: 20px;
        }
        .comment { 
        	background-color: white;
	    	border: 1px solid #e6e6e6;
			border-radius: 12px;
	    	padding: 25px;
	    	margin: 10px;
	    	position: relative; 
	    }
        .comment-content { 
        	margin: 5px 0; 
        }
        .forum-footer textarea{
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
    </style>
    <script>
	    // validate input
	    function validateInput(postId) {
	        // get fields
	        const newComment = document.getElementById("newComment").value;
	
	        // check empty
	        if (newComment === "") {
	            alert("Comment cannot be empty");
	            return false;
	        }
	        // Using api to formalize the characters (preventing injection)
	
	        // add addtional validation here
	
	        // validate success and send
	        postNewPost(postId, newComment);
	    }
	
	    function postNewPost(postId, newComment) {
	        fetch('post', {
	            method: 'POST',
	            headers: {
	                'Content-Type': 'application/x-www-form-urlencoded'
	            },
	            body: new URLSearchParams({
	            	'postId': postId,
	                'content': newComment
	            })
	        }).then(response => {
	            if (response.ok) { // sent back success response 
	                return response.text();
	            } else {
	                throw new Error("Comment failed.");
	            }
	        }).then(data => {
	            window.location.href = "post?postId="+postId; // Redirect to post page
	        }).catch(error => {
	            alert(error.message); // Show error message to user
	        });
	    }
	    
	    // require for comment ID, post ID, present Logged user id, deleted comment's user id
        function confirmDelete(comtId,postId,commentUserId) {
        	let p = 'post?deleteId='+comtId + '&comtUid=' +commentUserId;
            if (confirm("Are you sure you want to delete this post?")) {
                fetch(p, {
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
                    window.location.href = "post?postId=" + postId; // Redirect to post
                }).catch(error => {
                    alert(error.message); // Show error message to user
                });
            }
        }
    </script>
</head>
<body>
	<div class="forum-container">
        <button class="button"><a href="<%= request.getContextPath() %>/forum">Back</a></button>
		<div class="forum-header">	
	    	<h1>POST DETAILS</h1>
		</div>
	    <!-- Display post details -->
	    <div class="post">
	        <div class="title"><%= ((Post) request.getAttribute("post")).getTitle() %></div>
	        <div class="content"><%= ((Post) request.getAttribute("post")).getContent() %></div>
	        <div class="timestamp">Created by: <%= ((Post) request.getAttribute("post")).getPoster() %> at: <%= ((Post) request.getAttribute("post")).getCreatedAt() %></div>
	        <div class="timestamp">Updated at: <%= ((Post) request.getAttribute("post")).getUpdatedAt() %></div>
	    </div>
	
	    <!-- Comment section -->
	    <div class="comment-section">
	        <h2>Comments</h2>
	        <% 
	            List<Comment> comments = (List<Comment>) request.getAttribute("comments");
	        
	        	int postId = ((Post) request.getAttribute("post")).getId();
	        	
	            if (comments != null && !comments.isEmpty()) {
	                for (Comment comment : comments) {
	        %>
	            <div class="comment">
	                <div class="comment-content"><%= comment.getContent() %></div>
	                <div class="timestamp">Commented by: <%= comment.getReplyerName() %> at: <%= comment.getCreatedAt() %></div>
	                <% if (role == 1 || loggedUser == comment.getUserId()) { %> 
	                    <button id="delete-btn" onclick="confirmDelete(<%= comment.getId() %>, <%= postId %>, <%= comment.getUserId() %>)">Delete</button>
	                <% } %>
	            </div>
	        <% 
	                }
	            } else {
	        %>
	            <p>No comments yet.</p>
	        <% 
	            }
	        %>
	    </div>
	
	    <!-- Add comment form -->
	    <div class="forum-footer">	
	    	<h3>Add a comment</h3>
		    <form onsubmit="event.preventDefault(); validateInput(<%= postId %>)">
		        <textarea name="newComment" id="newComment" rows="5" cols="50" required></textarea><br>
		        <button class="button submit-btn" type="submit">Submit</button>
		    </form>	
	    </div>
	</div>
</body>
</html>
