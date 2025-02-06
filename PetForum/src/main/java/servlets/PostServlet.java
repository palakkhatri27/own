package servlets;

import java.io.*;
import java.util.*;

import beans.Comment;
import beans.Post;
import beans.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import database.*;


/**
 * An HTTP Servlet that overrides the service method to return a
 * simple web page.
 */
public class PostServlet extends HttpServlet {
    private CommentDBAO comtDB;
    private PostDBAO postDB;


    public void init() throws ServletException {
        comtDB = (CommentDBAO) getServletContext().getAttribute("comtDB");
        postDB = (PostDBAO) getServletContext().getAttribute("postDB");
        if (comtDB == null || postDB == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    public void destroy() {
        comtDB = null;
        postDB = null;
        
    }
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        // receive post information from url parameters
        
        request.setAttribute("user_id", session.getAttribute("user_id"));
        request.setAttribute("username", session.getAttribute("username"));
        request.setAttribute("role", session.getAttribute("role"));
        
        int postId = Integer.parseInt(request.getParameter("postId"));
        try {
            // get all posts
            Post post = postDB.getPostById(postId).get(0); // only 1 post
            List<Comment> comments = comtDB.getCommentByPostId(postId);
            
            request.setAttribute("post", post);
            request.setAttribute("comments", comments);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to retrieve posts.");
            return;
        }
        
        request.getRequestDispatcher("/post.jsp").forward(request, response);
    }
    
    
    // submit new comment
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        
        // set return type to json
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
		boolean status = false;
		
		String content = request.getParameter("content");
		int postId = Integer.parseInt(request.getParameter("postId"));
		int user_id = (Integer) session.getAttribute("user_id");
        
		try {
			comtDB.writeComment(postId, user_id, content);
			status = true;
		}
		catch (Exception e) {e.printStackTrace();}	
		
		if (status == true){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Comment successful.");
            
		} else { 		
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid post");
		}
	}
	
	// delete a post
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        
        // set return type to json
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
		boolean status = false;
		
        int comtUid = Integer.parseInt(request.getParameter("comtUid"));
        int comtId = Integer.parseInt(request.getParameter("deleteId"));
        
        // validation, if not admin user and not the comment owner call the delete method
        if ((Integer) session.getAttribute("role") != 1 && comtUid != (Integer) session.getAttribute("user_id") ) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid User role.");
            return;
        }
        
        
        try {
            comtDB.deleteComment(comtId);
            status = true;
        } catch (Exception e) {e.printStackTrace();}
        // define return message
		if (status == true){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Delete Comment successful.");
            
		} else { 		
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Comment deletation");
		}
	}

}
