package beans;

import java.sql.Timestamp;

public class Post {
    private int id;
    private int userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    private String poster; // poster username

    public Post(int id, int userId, String title, String content, Timestamp createdAt, Timestamp updatedAt,String username) {
    	this.id = id;
    	this.userId = userId;
    	this.title = title;
    	this.content = content;
    	this.createdAt = createdAt;
    	this.updatedAt = updatedAt;
    	this.poster = username; // this field is added by join sql statement
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getPoster() {
    	return this.poster;
    }
    
    public void setPoster(String username) {
    	this.poster = username;
    }
}
