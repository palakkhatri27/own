package database;

import java.sql.*;
import java.util.*;

import listener.ContextListener;
import beans.*;

// UserDatabase, including database manipulation functions and logic
public class CommentDBAO {
    Connection con;
    private boolean conFree = true;
    
    public CommentDBAO(HashMap<String,String> dbConfig) throws Exception {
        try {
        	String url = dbConfig.get("url");
        	String username = dbConfig.get("username");
        	String password = dbConfig.get("password");
            con = DriverManager.getConnection(url, username, password);
            
        } catch (Exception ex) {
            System.out.println("Exception in CommentDBAO: " + ex);
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
    public ArrayList<Comment> getCommentListBy(String query, Object... params) throws Exception {       
        ArrayList<Comment> comments = new ArrayList();
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
                Comment c = new Comment(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getInt("user_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getString("username")
                    );
                comments.add(c);
            }

            prepStmt.close();
        } catch (SQLException ex) {
            releaseConnection();
            throw new Exception(ex.getMessage());
        }
        
        releaseConnection();
        return comments;
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
    
    public ArrayList<Comment> getAllComments() throws Exception {
    	String query = "SELECT *, user.username " + // additionally pick out the username of comments
    				   "FROM comment " +
    				   "JOIN user ON comment.user_id = user.id " + 
                       "ORDER BY comment.created_at DESC"; // sorted by lastly updated time
    	return getCommentListBy(query);
    }
    
    // get comment by postId
    public ArrayList<Comment> getCommentByPostId(int postId) throws Exception {
        String query = "SELECT *, user.username " +
        			   "FROM comment " +
        			   "JOIN user ON comment.user_id = user.id " +
        			   "WHERE post_id = ? ";
        return getCommentListBy(query, postId);
    }

    // get comment by userId
    public ArrayList<Comment> getCommentByUserId(int userID) throws Exception {
        String query = "SELECT *, user.username FROM comment " +
        			   "JOIN user ON comment.user_id = user.id " +
        			   "WHERE user_id = ? ";
        return getCommentListBy(query, userID);
    }
    
 
    public void writeComment(int postId, int userId, String content) throws Exception {
        String query = "INSERT INTO comment (post_id, user_id, content, created_at) VALUES (?, ?, ?, ?)";
        Timestamp createdTime = new Timestamp(System.currentTimeMillis());
        executeUpdate(query,postId, userId, content, createdTime);
        
        // update the post time
        String updateTime = "UPDATE post SET updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        executeUpdate(updateTime,postId);
    }  
    
    public void deleteComment(int commentId) throws Exception{
        String query = "DELETE FROM comment WHERE id = ?";
        executeUpdate(query,commentId);
    }
    
}
