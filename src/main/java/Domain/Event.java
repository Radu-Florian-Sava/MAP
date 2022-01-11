package Domain;

import Utils.StatusEventUser;

import java.sql.Timestamp;

public class Event implements Identifiable<Integer> {
    private final int id;
    private Timestamp date;
    private String title;
    private String description;
    private int user;
    private StatusEventUser status;

    public Event(int id, Timestamp date, String title, String description, int user, StatusEventUser status) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        this.user = user;
        this.status = status;
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

    public int getUser() {
        return user;
    }

    public String getStatus() {
        return status.getStatus();
    }
}
