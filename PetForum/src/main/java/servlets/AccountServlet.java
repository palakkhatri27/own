package servlets;

import java.io.*;
import java.util.*;

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
public class AccountServlet extends HttpServlet {
    private PostDBAO postDB;


    public void init() throws ServletException {
        postDB = (PostDBAO) getServletContext().getAttribute("postDB");
        if (postDB == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    public void destroy() {
        postDB = null;
    }
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        
        request.setAttribute("username", session.getAttribute("username"));
        request.setAttribute("role", session.getAttribute("role"));
        int userId = Integer.parseInt(request.getParameter("userId"));
        
        try {
            // get all posts
            List<Post> posts = postDB.getPostByPosterId(userId);
            
            request.setAttribute("posts", posts);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to retrieve posts.");
            return;
        }
        
        request.getRequestDispatcher("/account.jsp").forward(request, response);
    }
    
    // submit new post
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        
        String logout = request.getParameter("logout");
        if (logout != null && logout.equals("true")) {
            session.invalidate();  // Invalidate the session
            response.sendRedirect("login.jsp");  // Redirect to login page
            return;
        }
        
        // set return type to json
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
		boolean status = false;
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		int user_id = (Integer) session.getAttribute("user_id");
        
		try {
			postDB.writePost(user_id, title, content);
			status = true;
		}
		catch (Exception e) {e.printStackTrace();}	
		
		if (status == true){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Post successful.");
            
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
		
        if ((Integer) session.getAttribute("role") != 1) { // if user is not an admin
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid User role.");
            return;
        }
        
        int postId = Integer.parseInt(request.getParameter("deleteId"));
        
        try {
            postDB.deletePost(postId);
            status = true;
        } catch (Exception e) {e.printStackTrace();}
        // define return message
		if (status == true){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Delete Post successful.");
            
		} else { 		
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Post Deletation");
		}
	}
	
}
