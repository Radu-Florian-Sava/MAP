package Domain;


/**
 * class which contains specific information of a message
 */
public class MessageDTO {
    private final int id;
    private final String message;
    private final String message_reply;
    private final String userFrom;

    public MessageDTO(int id, String message, String message_reply, String userFrom) {
        this.id = id;
        this.message = message;
        this.message_reply = message_reply;
        this.userFrom = userFrom;
    }

    public MessageDTO(int id, String message, String userFrom) {
        this.id = id;
        this.message = message;
        this.userFrom = userFrom;
        message_reply = null;
    }

    public String getMessage_reply() {
        return message_reply;
    }

    public String getMessage() {
        return message;
    }

    public String getUserFrom() {
        return userFrom;
    }

    @Override
    public String toString() {
        return
                (message_reply == null) ?
        userFrom + ": " + message :
                "Reply la " + message_reply + "\n" + userFrom + ": " + message;
    }

    public int getId() {
        return id;
    }
}
