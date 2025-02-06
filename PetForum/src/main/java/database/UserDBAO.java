package database;

import java.sql.*;
import java.util.*;

import listener.ContextListener;
import beans.*;

// UserDatabase, including database manipulation functions and logic
public class UserDBAO {
    Connection con;
    private boolean conFree = true;
    
    public UserDBAO(HashMap<String,String> dbConfig) throws Exception {
        try {
        	String url = dbConfig.get("url");
        	String username = dbConfig.get("username");
        	String password = dbConfig.get("password");
            con = DriverManager.getConnection(url, username, password);
            
        } catch (Exception ex) {
            System.out.println("Exception in UserDBAO: " + ex);
            throw new Exception("Couldn't open connection to database: " +
                    ex.getMessage());
        }
    }
    
    public void remove() {
        try {
            con.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    protected synchronized Connection getConnection() {
        while (conFree == false) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        
        conFree = false;
        notify();
        
        return con;
    }
    
    protected synchronized void releaseConnection() {
        while (conFree == true) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        
        conFree = true;
        notify();
    }
    
    // General query, searching by replace different sql statement (from parameters)
    // params is a variable list.
    public ArrayList<User> getUserListBy(String query, Object... params) throws Exception {       
        ArrayList<User> users = new ArrayList();
        try {
            getConnection();
            PreparedStatement prepStmt = con.prepareStatement(query);

            //this place identify different type of parameters
            // then fill the sql statement
            // notice params must be in order, corresponding to mark "?" sequences in sql statement.
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    prepStmt.setInt(i + 1, (Integer) params[i]);
                } else if (params[i] instanceof String) {
                    prepStmt.setString(i + 1, (String) params[i]);
                }
            }

            ResultSet rs = prepStmt.executeQuery();

            while (rs.next()) {                
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("salt"),
                        rs.getInt("role")
                    );
                users.add(u);
            }

            prepStmt.close();
        } catch (SQLException ex) {
            releaseConnection();
            throw new Exception(ex.getMessage());
        }
        
        releaseConnection();
        return users;
    }
    
    // General sql execute function
    // Used for adding, deleting and updating (modifying actions)
    // Logic is same as above (using general query)
    public void executeUpdate(String query, Object... params) throws Exception {       
        try {
            getConnection();
            con.setAutoCommit(false);
            PreparedStatement prepStmt = con.prepareStatement(query);

            // same as getUsrListBy
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof Integer) {
                    prepStmt.setInt(i + 1, (Integer) params[i]);
                } else if (params[i] instanceof String) {
                    prepStmt.setString(i + 1, (String) params[i]);
                }
                // add more types if you have
                
            }

            prepStmt.executeUpdate();
            prepStmt.close();
            
        } catch (SQLException ex) {
        	con.rollback();
            releaseConnection();
            throw new Exception(ex.getMessage());
        }
        
        con.commit();
        con.setAutoCommit(true);
        releaseConnection();
    }
         
    // used in the servlet
    public ArrayList<User> getAllUsers() throws Exception {
    	String query = "select * from user";
    	return getUserListBy(query);
    }
    
    // get user by id
    public ArrayList<User> getUserById(int userId) throws Exception {
        String query = "SELECT * FROM user WHERE id = ?";
        return getUserListBy(query, userId);
    }

    // get user by username
    public ArrayList<User> getUserByUsername(String username) throws Exception {
        String query = "SELECT * FROM user WHERE username = ?";
        return getUserListBy(query, username);
    }
    
    // user login function
    // return null when there is no such a user in database
    // return the logged user if find a user
    // notice, username is Unique, thus if find a user, the size of user list must only be 1.
    public User userLogin(String username, String password) throws Exception {
        String query = "select * from user where username = ? and password = ?";
    	ArrayList<User> users = getUserListBy(query, username,password);
        if(users.size() == 1) {
        	return users.get(0);
        }
        return null;
    }
    
    // user register
    public boolean addNormalUser(String username, String password, String salt){
        String query = "INSERT INTO user (username, password, salt, role) VALUES (?, ?, ?, ?)";
        try {
        	executeUpdate(query,username,password,salt,0);
        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        	return false;
        }
        return true;
    }
    
    // add admin user
    public boolean addAdminUser(String username, String password, String salt){
        String query = "INSERT INTO user (username, password, salt, role) VALUES (?, ?, ?, ?)";
        try {
        	executeUpdate(query,username,password,salt,1);
        } catch (Exception ex) {
        	System.out.println(ex.getMessage());
        	return false;
        }
        return true;
    }
    
}
