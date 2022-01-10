package Utils;

public class UtilsFunctions {
    public static StatusFriendship transformIntegerToStatusFriendship(int index) {
        return switch (index) {
            case 0 -> StatusFriendship.WAIT;
            case 1 -> StatusFriendship.DECLINE;
            case 2 -> StatusFriendship.ACCEPT;
            default -> null;
        };
    }
}
