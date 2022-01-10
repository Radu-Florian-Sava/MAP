package Domain;

import Utils.StatusFriendship;

import java.util.Date;


public class FriendshipDTO {
    private final int relation;
    private final int id;
    private final String first_name;
    private final String second_name;
    private final Date date;
    private final StatusFriendship status;

    public FriendshipDTO(int relation, int id, String first_name, String second_name, Date date, StatusFriendship status) {
        this.relation = relation;
        this.id = id;
        this.first_name = first_name;
        this.second_name = second_name;
        this.date = date;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getSecondName() {
        return second_name;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus() {
        return status.getStatus();
    }

    public int getRelation() {
        return relation;
    }

    public String getStringRelation() {
        return switch(getRelation()) {
            case 0 -> "->";
            case 1 -> "<-";
            default -> null;
        };
    }
}
