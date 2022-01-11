package Validate;

import Domain.Friendship;
import Exceptions.ValidateException;

/**
 * validates a friendship relationship between two users
 */
public class FriendshipValidator implements Validation<Integer, Friendship> {
    private String message;

    /**
     * the message is initialised as a null message by the constructor
     */
    public FriendshipValidator() {
        message = "";
    }

    /**
     * @param friendship Friendship class element to be validated
     * @throws ValidateException if the ID is not a natural number, the sender/receiver IDs are not natural number or
     *                           if the sender and the receiver have the same ID
     */
    @Override
    public void genericValidate(Friendship friendship) throws ValidateException {
        message = "";
        if (friendship.getId() < 1)
            message += "Id invalid!\n";
        if (friendship.getSender() < 1)
            message += "Primul utlilizator nu e valid!\n";
        if (friendship.getReceiver() < 1)
            message += "Al doilea utlilizator nu e valid!\n";
        if (friendship.getReceiver() == friendship.getSender())
            message += "Utilizatorii au id identic!\n";
        if (message.length() > 0)
            throw new ValidateException(message);
    }
}
