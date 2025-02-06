package listener;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import database.CommentDBAO;
import database.EventDBAO;
import database.PostDBAO;
import database.UserDBAO;
import database.MarkerDBAO;
import jakarta.servlet.*;

// Event handler class for handling application scope events
// some global variables saved here
public final class ContextListener implements ServletContextListener {
	
    private ServletContext context = null;
    // This method gets called when the application is deployed
    public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();
        
        // configure the database
        HashMap<String, String> dbConfig = new HashMap<>();
        dbConfig.put("url", "jdbc:mysql://localhost:3306/app");
        dbConfig.put("username", "root");
        dbConfig.put("password", "root");
    	
        // Create UserDBAO object and save it as an attribute to
        // ServletContext scope object.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            UserDBAO userDB = new UserDBAO(dbConfig);
            PostDBAO postDB = new PostDBAO(dbConfig);
            CommentDBAO comtDB = new CommentDBAO(dbConfig);
            MarkerDBAO markerDB = new MarkerDBAO(dbConfig);
            EventDBAO eventDB = new EventDBAO(dbConfig);
            
            context.setAttribute("userDB", userDB);
            context.setAttribute("postDB", postDB);
            context.setAttribute("comtDB", comtDB);
            context.setAttribute("markerDB", markerDB);
            context.setAttribute("eventDB", eventDB);
        } catch (Exception ex) {
            System.out.println("Couldn't create bookstore database bean: " +
                ex.getMessage());
        }
    }

    // This method gets called when the application is undeployed
    public void contextDestroyed(ServletContextEvent event) {
        context = event.getServletContext();

        UserDBAO userDB = (UserDBAO) context.getAttribute("userDB");
        PostDBAO postDB = (PostDBAO) context.getAttribute("postDB");
        CommentDBAO comtDB = (CommentDBAO) context.getAttribute("comtDB");
        MarkerDBAO markerDB = (MarkerDBAO) context.getAttribute("markerDB"); 
        EventDBAO eventDB = (EventDBAO) context.getAttribute("eventDB");
        
        if (userDB != null) {userDB.remove();}
        
        if (postDB != null) {postDB.remove();}
        
        if (comtDB != null) {comtDB.remove();}
        
        if (markerDB != null) { markerDB.remove(); } 
        
        if (eventDB != null) {eventDB.remove();}
        
        try {
            DriverManager.deregisterDriver(DriverManager.getDriver("jdbc:mysql://localhost:3306/app"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        context.removeAttribute("userDB");
        context.removeAttribute("postDB");
        context.removeAttribute("comtDB");
        context.removeAttribute("markerDB");
        context.removeAttribute("eventDB");
        
    }
}
