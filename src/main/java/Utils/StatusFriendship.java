package Utils;

/**
 *  public enum which represents the status of a friendship doubled by a String equivalent
 *  to be used with a GUI
 */
public enum StatusFriendship {
        ACCEPT("Accepted"),
        DECLINE("Declined"),
        WAIT("Waiting...");
private final String status;

        /**
         * @param status sets the private String status as the given value
         */
private StatusFriendship(String status) {
        this.status = status;
        }

        /**
         * @return the value of status : WAITING, DECLINED, ACCEPT
         */
public String getStatus() {
        return status;
        }
}