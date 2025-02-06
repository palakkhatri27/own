package beans;

public class Message {
    private int id;
    private int topicId;
    private String sender;
    private String messageContent;
    private String sendTime;

    public Message(int id, int topicId, String sender, String messageContent, String sendTime) {
        this.id = id;
        this.topicId = topicId;
        this.sender = sender;
        this.messageContent = messageContent;
        this.sendTime = sendTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }
}
