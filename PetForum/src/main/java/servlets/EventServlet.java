package servlets;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import com.google.gson.Gson;

import beans.Event;
import beans.Marker;
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
public class EventServlet extends HttpServlet {
    private EventDBAO eventDB;


    public void init() throws ServletException {
        eventDB = (EventDBAO) getServletContext().getAttribute("eventDB");
        if (eventDB == null) {
            throw new UnavailableException("Couldn't get database.");
        }
    }

    public void destroy() {
        eventDB = null;
        
    }
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // get present session, return null if not exist
        if (session == null || session.getAttribute("username") == null) {
            // Redirect to login if session is unavailable
            response.sendRedirect("login.jsp");
            return;
        }
        try {
        	String m = request.getParameter("markerId");
        	if (m == null) {
                List<Event> events = eventDB.getAllEvents();
                Gson gson = new Gson();
                String jsonEvents = gson.toJson(events);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonEvents);
        	} else {
        		int markerId = Integer.parseInt(request.getParameter("markerId"));
                List<Event> events = eventDB.getEventByMarkerId(markerId);
                Gson gson = new Gson();
                String jsonEvents = gson.toJson(events);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonEvents);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"error occurred.\"}");
        }
    }
    
    // submit new post
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
		
		String title = request.getParameter("title");
		String content = request.getParameter("content");
		int markerId = Integer.parseInt(request.getParameter("markerId"));
		int user_id = (Integer) session.getAttribute("user_id");
		
        
		try {
			eventDB.addEvent(user_id, markerId, title, content);
			status = true;
		}
		catch (Exception e) {e.printStackTrace();}	
		
		if (status == true){
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Add successful.");
            
		} else { 		
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid Event");
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
        
        int eventId = Integer.parseInt(request.getParameter("deleteId"));
        
        try {
            eventDB.deleteEvent(eventId);
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