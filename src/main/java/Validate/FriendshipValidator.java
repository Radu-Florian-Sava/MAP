package Validate;

import Domain.Friendship;
import Exceptions.ValidateException;

/**
 * valideaza o relatie de prietenie dintre doi utilizatori
 */
public class FriendshipValidator implements Validation<Integer, Friendship> {
    private String message;

    /**
     * mesajul specific este intializat vid in constructor
     */
    public FriendshipValidator() {
        message = "";
    }

    /**
     * @param friendship element de tip relatie de prietenie care va fi validat
     * @throws ValidateException arunca eroare de validare cu mesajul creeat pe parcurs daca exista
     *                           verifica daca elementul are id-uri numere naturale si utilizatorii au id-uri diferite
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
