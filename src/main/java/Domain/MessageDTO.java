package Domain;


/**
 * class which contains specific information of a message
 */
public class MessageDTO {
    private final int id;
    private final String message;
    private final String message_reply;
    private final String user_from;

    public MessageDTO(int id, String message, String message_reply, String user_from) {
        this.id = id;
        this.message = message;
        this.message_reply = message_reply;
        this.user_from = user_from;
    }

    public MessageDTO(int id, String message, String user_from) {
        this.id = id;
        this.message = message;
        this.user_from = user_from;
        message_reply = null;
    }

    public String getMessage_reply() {
        return message_reply;
    }

    public String getMessage() {
        return message;
    }

    public String getUser_from() {
        return user_from;
    }

    @Override
    public String toString() {
        return
                (message_reply == null) ?
        user_from + ": " + message :
                "Reply la " + message_reply + "\n" + user_from + ": " + message;
    }

    public int getId() {
        return id;
    }
}
