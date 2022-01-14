package Domain;

import Utils.StatusEventUser;
import javafx.util.Pair;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Event implements Identifiable<Integer> {
    private final int id;
    private Timestamp date;
    private String title;
    private String description;
    private final Map<Integer, Pair<StatusEventUser, Integer>> users_with_status = new HashMap<>();

    public Event(int id,
                 Timestamp date,
                 String title,
                 String description,
                 int user,
                 StatusEventUser status,
                 int id_user_event) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        users_with_status.put(user, new Pair<>(status, id_user_event));
    }

    public Event(Timestamp date, String title, String description) {
        this.id = 1;
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public String getTitle() {
        return title;
    }


    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public Map<Integer, Pair<StatusEventUser, Integer>> getUsers() {
        return users_with_status;
    }

    public void add(int id_user, StatusEventUser status, int id_user_event) {
        users_with_status.put(id_user, new Pair<>(status, id_user_event));
    }

    @Override
    public String toString() {
        return title + " at the date: " + date + "\nDescription: " + description;
    }
}
