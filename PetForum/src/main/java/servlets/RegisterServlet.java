package servlets;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

import beans.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import database.*;


/**
 * An HTTP Servlet that overrides the service method to return a
 * simple web page.
 */
public class RegisterServlet extends HttpServlet {
    private UserDBAO userDB;

    public void init() throws ServletException {
        userDB = (UserDBAO) getServletContext().getAttribute("userDB");
        if (userDB == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    public void destroy() {
        userDB = null;
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException {
        // set return type to json
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		// generate salt
		String salt = UUID.randomUUID().toString().replace("-", "");

		boolean result = false ;
		User registerUser = null;
		
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			String saltedPassword = Base64.getEncoder().encodeToString(md.digest((password+salt).getBytes()));
			//result = userDB.addNormalUser(username, password);
			result = userDB.addNormalUser(username, saltedPassword, salt);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
		if (result){
			try {
				// get the newly registered user
				registerUser = userDB.getUserByUsername(username).get(0);	
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			// setSession
			HttpSession session = request.getSession();
			session.setAttribute("user_id", registerUser.getId());
			session.setAttribute("username", registerUser.getUsername());
			session.setAttribute("role", registerUser.getRole());
			
			// sent back the response to let front-end functions do continuous actions
			// this way fits the situations that front-end needs to show response
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Registration successful.");
		}
		else { 	
            // Registration failed (e.g., username already exists)
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write("Username already exists.");
		}
	}
}
