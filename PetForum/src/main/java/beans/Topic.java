package beans;

import java.sql.Timestamp;

public class Topic {
    private int id;
    private int userId;
    private String title;
    private String createdAt;
    private String lastUpdatedAt;

//    public Topic(int id, int userId, String title) {
//        this.id = id;
//        this.userId = userId;
//        this.title = title;
//    }
//
//    public Topic(int id, int userId, String title, String createdAt) {
//        this.id = id;
//        this.userId = userId;
//        this.title = title;
//        this.createdAt = createdAt;
//    }

    public Topic(int id, int userId, String title, String createdAt, String lastUpdatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}
