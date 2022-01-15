package Domain;


/**
 * class which contains specific information of a message
 */
public class MessageDTO {
    private final int id;
    private final String message;
    private final String message_reply;
    private final String userFrom;

    /**
     * @param id of the message
     * @param message is the message body
     * @param messageReply the reply of the message
     * @param userFrom the name of the user who sent the message
     */
    public MessageDTO(int id, String message, String messageReply, String userFrom) {
        this.id = id;
        this.message = message;
        this.message_reply = messageReply;
        this.userFrom = userFrom;
    }

    /**
     * @param id of the message
     * @param message is the message body
     * @param userFrom the name of the user who sent the message
     */
    public MessageDTO(int id, String message, String userFrom) {
        this.id = id;
        this.message = message;
        this.userFrom = userFrom;
        message_reply = null;
    }

    /**
     * @return the message body as a string
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return an extended message containing the replied message, the person who sent the message
     *         and the message body as a string
     */
    @Override
    public String toString() {
        return
                (message_reply == null) ?
        userFrom + ": " + message :
                "Reply la " + message_reply + "\n" + userFrom + ": " + message;
    }

    /**
     * @return the ID of the message
     */
    public int getId() {
        return id;
    }
}
