package servlets;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

import beans.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import database.*;


/**
 * An HTTP Servlet that overrides the service method to return a
 * simple web page.
 */
public class LoginServlet extends HttpServlet {
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

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//
		request.getRequestDispatcher("/login.jsp").forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // set return type to json
		response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
		
		// receive username and password, and check validation
		String username = request.getParameter("username");
		String password = request.getParameter("password");
//		System.out.println(username);
//		System.out.println(password);
		User loginUser = null;
		
		try {
			loginUser = userDB.getUserByUsername(username).get(0);
			String salt = loginUser.getSalt();
			MessageDigest md = MessageDigest.getInstance("MD5");
			String saltedPassword = Base64.getEncoder().encodeToString(md.digest((password+salt).getBytes()));
			loginUser = userDB.userLogin(username, saltedPassword);
		}
		catch (Exception e)
		{
		 e.printStackTrace();
			
		}		
		if (loginUser != null){
			// login success
			HttpSession session = request.getSession();
			session.setAttribute("user_id", loginUser.getId());
			session.setAttribute("username", loginUser.getUsername());
			session.setAttribute("role", loginUser.getRole());
			
			// html return type
			// bellow are 2 redirect ways
			// request.getRequestDispatcher("/forum").forward(request,response); // not applied, cause it's looking for post method
			// or
			// response.sendRedirect(request.getContextPath()+"/forum"); // redirect to backend straightly, not go through front-end
			
			// json return type
			// backend do nothing, let front end handle the response
			int userId = loginUser.getId();
			PrintWriter out = response.getWriter();
			out.write("{\"userId\": " + userId + "}");
            response.setStatus(HttpServletResponse.SC_OK);
//            response.getWriter().write("Login successful.");
		}
		else { 		
			// login failed
			
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid username or password.");
            
//			response.sendRedirect("login.jsp");
		}
	}
	
}
