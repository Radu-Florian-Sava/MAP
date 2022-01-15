package Domain;

import Utils.StatusEventUser;
import javafx.util.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * a class used to store events in which application users can participate
 */
public class Event implements Identifiable<Integer> {
    private final int id;
    private Timestamp date;
    private String title;
    private String description;
    private final Map<Integer, Pair<StatusEventUser, Integer>> users_with_status = new HashMap<>();

    /**
     * default constructor
     * @param id of the event, unique
     * @param date in which the event takes place
     * @param title of the event
     * @param description of the event, what's going to happen
     * @param user the user who created the event
     * @param status the status of a user( Participant/Organiser)
     * @param idUserEvent the ID of the userEvent database pair, ID of a list of participants
     */
    public Event(int id,
                 Timestamp date,
                 String title,
                 String description,
                 int user,
                 StatusEventUser status,
                 int idUserEvent) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        users_with_status.put(user, new Pair<>(status, idUserEvent));
    }

    /**
     * @param date in which the event takes place
     * @param title of the event
     * @param description of the event, what's going to happen
     */
    public Event(Timestamp date, String title, String description) {
        this.id = 1;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    /**
     * @return the event description as a string
     */
    public String getDescription() {
        return description;
    }


    /**
     * @return the title of the event
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return the date in which the event takes place
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * @param date the new date in which the event will take place
     */
    public void setDate(Timestamp date) {
        this.date = date;
    }

    /**
     * @return the ID of the event, unique
     */
    @Override
    public Integer getId() {
        return id;
    }

    /**
     * @return Map containing the pair of users which participate in the event
     */
    public Map<Integer, Pair<StatusEventUser, Integer>> getUsers() {
        return users_with_status;
    }

    /**
     * @param idUser the ID of the user that will participate in the event
     * @param status the status of the new user (Participant/Organiser)
     * @param idUserEvent the ID of the list of users in which the new user will be added
     */
    public void add(int idUser, StatusEventUser status, int idUserEvent) {
        users_with_status.put(idUser, new Pair<>(status, idUserEvent));
    }

    /**
     * @return the data of our event as a string composed of an extended description
     */
    @Override
    public String toString() {
        return title + " at the date: " + date + "\nDescription: " + description;
    }
}
