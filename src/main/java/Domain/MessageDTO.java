package Domain;

import java.util.List;

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

    public List<String> getUsers_to() {
        return users_to;
    }

    @Override
    public String toString() {
        return user_from + ": " + message;
    }

    public int getId() {
        return id;
    }
}
