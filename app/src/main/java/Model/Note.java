package Model;

public class Note {
    private String header;
    private String content;
    private String sender;
    private String recipient;
    private long  tofinish;
    private long deadline;

    public Note(){
    }

    public Note(String header, String content, String sender, String recipient, long tofinish, long deadline) {
        this.header = header;
        this.content = content;
        this.sender = sender;
        this.recipient = recipient;
        this.tofinish = tofinish;
        this.deadline = deadline;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public long getTofinish() {
        return tofinish;
    }

    public void setTofinish(long tofinish) {
        this.tofinish = tofinish;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }
}
