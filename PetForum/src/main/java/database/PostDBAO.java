package database;

import java.sql.*;
import java.util.*;

import listener.ContextListener;
import beans.*;

// UserDatabase, including database manipulation functions and logic
public class PostDBAO {
    Connection con;
    private boolean conFree = true;
    
    public PostDBAO(HashMap<String,String> dbConfig) throws Exception {
        try {
        	String url = dbConfig.get("url");
        	String username = dbConfig.get("username");
        	String password = dbConfig.get("password");
            con = DriverManager.getConnection(url, username, password);
            
        } catch (Exception ex) {
            System.out.println("Exception in PostDBAO: " + ex);
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
    public ArrayList<Post> getPostListBy(String query, Object... params) throws Exception {       
        ArrayList<Post> posts = new ArrayList();
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
                Post p = new Post(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("username")
                    );
                posts.add(p);
            }

            prepStmt.close();
        } catch (SQLException ex) {
            releaseConnection();
            throw new Exception(ex.getMessage());
        }
        
        releaseConnection();
        return posts;
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
                } else if (params[i] instanceof Timestamp) {
                    prepStmt.setTimestamp(i + 1, (Timestamp) params[i]);
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
    
    public ArrayList<Post> getAllPosts() throws Exception {
    	String query = "SELECT *, user.username " + // additionally pick out the username of posters. it avoids to connect database again for username
    				   "FROM post " +
    				   "JOIN user ON post.user_id = user.id " + 
                       "ORDER BY post.updated_at DESC"; // sorted by lastly updated time
    	return getPostListBy(query);
    }
    
    // get user by id
    public ArrayList<Post> getPostById(int postId) throws Exception {
        String query = "SELECT *, user.username " +
        			   "FROM post " +
        			   "JOIN user ON post.user_id = user.id " +
        			   "WHERE post.id = ? ";
        return getPostListBy(query, postId);
    }

    // get user by username
    public ArrayList<Post> getPostByPosterId(int userID) throws Exception {
        String query = "SELECT *, user.username FROM post " +
        			   "JOIN user ON post.user_id = user.id " +
        			   "WHERE user_id = ? ";
        return getPostListBy(query, userID);
    }
    
    // user login function
    // return null when there is no such a user in database
    // return the logged user if find a user
    // notice, username is Unique, thus if find a user, the size of user list must only be 1.
    
    public void writePost(int userId, String title, String content) throws Exception {
        String query = "INSERT INTO post (user_id, title, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        Timestamp createdTime = new Timestamp(System.currentTimeMillis());
        executeUpdate(query,userId, title,content,createdTime,createdTime);
    }  
    
    public void deletePost(int postId) throws Exception{
        String query = "DELETE FROM post WHERE id = ?";
        executeUpdate(query,postId);
    }
    
}
