package beans;

public class Event {
    private int eventId;
    private String creator;
    private int markerId;
    private String title;
    private String content;
    
    public Event(int eventId, String creator, int markerId, String title, String content) {
    	this.eventId = eventId;
    	this.creator = creator;
    	this.markerId = markerId;
    	this.title = title;
    	this.content = content;
    }
    
    public int getEventId() {
    	return eventId;
    }
    
    public String getCreator() {
    	return creator;
    }
    
    public int getMarkerId() {
    	return markerId;
    }
    
    public String getTitle() {
    	return title;
    }
    
    public String getContent() {
    	return content;
    }
}
