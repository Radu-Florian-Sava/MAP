package Utils;

/**
 *  A class which contains utilitarian functions
 */
public class UtilsFunctions {

    /**
     * @param index is the index of the friendship request (0 = PENDING, 1 = REJECTED, 2 = ACCEPTED)
     * @return the corresponding enum type of the given index
     */
    public static StatusFriendship transformIntegerToStatusFriendship(int index) {
        return switch (index) {
            case 0 -> StatusFriendship.WAIT;
            case 1 -> StatusFriendship.DECLINE;
            case 2 -> StatusFriendship.ACCEPT;
            default -> null;
        };
    }
}
