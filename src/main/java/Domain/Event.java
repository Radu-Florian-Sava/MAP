package Domain;

import Utils.StatusEventUser;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Event implements Identifiable<Integer> {
    private final int id;
    private Timestamp date;
    private String title;
    private String description;
    private Map<Integer, StatusEventUser> users_with_status = new HashMap<>();

    public Event(int id, Timestamp date, String title, String description, int user, StatusEventUser status) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        users_with_status.put(user, status);
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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Map<Integer, StatusEventUser> getUsers() {
        return users_with_status;
    }

    public void add(int id_user, StatusEventUser status) {
        users_with_status.put(id_user, status);
    }
}
