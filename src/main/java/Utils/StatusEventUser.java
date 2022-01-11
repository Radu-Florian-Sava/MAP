package Utils;
/**
 *  public enum which represents the status of a user for an event doubled by a String equivalent
 *  to be used with a GUI
 */
public enum StatusEventUser {
    ORGANIZER("Organizer"),
    PARTICIPANT("Participant");
    private final String status;

    /**
     * @param status sets the private String status as the given value
     */
    private StatusEventUser(String status) {
        this.status = status;
    }

    /**
     * @return the value of status : ORGANIZER, PARTICIPANT
     */
    public String getStatus() {
        return status;
    }
}
