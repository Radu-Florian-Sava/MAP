package Domain;

import java.sql.Timestamp;
import java.util.Objects;

/**
 *  class which contains a message
 */
public class Message implements Identifiable<Integer> {
    private final int id;
    private final int from;
    private final int to;
    private final String message;
    private final Timestamp date;
    private final Integer id_reply;

    /**
     * @param from is the sender id
     * @param to is the receiver id
     * @param message is the message body
     * @param date is the date when the message has been sent
     * @param id_reply applied if the message is a reply corresponding to the message it replies to
     */
    public Message(int from, int to, String message, Timestamp date, Integer id_reply) {
        this.id = 1;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.id_reply = id_reply;
    }

    /**
     * @param id is the id of the message which is going to be sent
     * @param from is the sender id
     * @param to is the receiver id
     * @param message is the message body
     * @param date is the date when the message has been sent
     * @param id_reply applied if the message is a reply
     */
    public Message(int id, int from, int to, String message, Timestamp date, Integer id_reply) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.id_reply = id_reply;
    }

    /**
     * @return the message sender ID
     */
    public int getFrom() {
        return from;
    }

    /**
     * @return the message receiver ID
     */
    public int getTo() {
        return to;
    }

    /**
     * @return the message body
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the date when the message has been sent
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * @return the ID of the message it reaplies to
     */
    public Integer getIdReply() {
        return id_reply;
    }

    /**
     * @return the message ID
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @param o is the object we compare
     * @return true if o and this object are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return id == message1.id && from == message1.from && to == message1.to && message.equals(message1.message) && date.equals(message1.date) && Objects.equals(id_reply, message1.id_reply);
    }

    /**
     * @return an unique integer for each object of this type
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, message, date, id_reply);
    }

    /**
     * @return the message using a specific format
     * messageBody + 'de la' + fromID + 'la' + toID + 'la data de' + date
     */
    @Override
    public String toString() {
        return "'" +
                message + "' de la " + from + " la " + to + " la data de " + date.toString();
    }
}
