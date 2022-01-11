package Domain;

import Utils.StatusFriendship;

import java.util.Date;


/**
 *  contains specific information about a friendship relationship
 */
public class FriendshipDTO {
    private final int relation;
    private final int id;
    private final String first_name;
    private final String second_name;
    private final Date date;
    private final StatusFriendship status;

    /**
     * @param relation the friendship status under a very specific form
     * @param id unique for each friendship
     * @param first_name the first name of the person associated with this friendship
     * @param second_name the surname of the person associated with this friendship
     * @param date the date when the friendship was created
     * @param status specific values given by an enum (WAITING, DENIED, ACCEPTED )
     */
    public FriendshipDTO(int relation, int id, String first_name, String second_name, Date date, StatusFriendship status) {
        this.relation = relation;
        this.id = id;
        this.first_name = first_name;
        this.second_name = second_name;
        this.date = date;
        this.status = status;
    }

    /**
     * @return unique id for each friendship
     */
    public int getId() {
        return id;
    }

    /**
     * @return the first name of the person associated with this friendship
     */
    public String getFirstName() {
        return first_name;
    }

    /**
     * @return the surname of the person associated with this friendship
     */
    public String getSecondName() {
        return second_name;
    }

    /**
     * @return the date when the friendship was created
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return specific values given by an enum (WAITING, DENIED, ACCEPTED )
     */
    public String getStatus() {
        return status.getStatus();
    }

    /**
     * @return the friendship status under a very specific form
     */
    public int getRelation() {
        return relation;
    }

    /**
     * @return returns the relation as an arrow representing the person who initiated the relationship
     */
    public String getStringRelation() {
        return switch(getRelation()) {
            case 0 -> "->";
            case 1 -> "<-";
            default -> null;
        };
    }
}
