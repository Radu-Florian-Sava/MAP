package Validate;

import Domain.Message;
import Exceptions.ValidateException;
import java.util.Objects;

/**
 * validates a friendship relationship between two users
 */
public class MessageValidator implements Validation<Integer, Message> {
    /**
     * @param message Message class variable to be validated
     * @throws ValidateException if the id is not a natural number, the id of the sender/receiver ID
     *                           is not a natural number, the message body is void,
     *                           the message date is null or if replyID is not a natural number
     */
    @Override
    public void genericValidate(Message message) throws ValidateException {
        String err = "";

        if(message.getId() < 1)
            err += "Id invalid!\n";
        if(message.getFrom() < 1)
            err += "Utilizatorul care trimite invalid!\n";
        if(message.getTo() < 1)
            err += "Utilizatorul care primeste invalid!\n";
        if(message.getMessage() != null)
            if(Objects.equals(message.getMessage(), ""))
                err += "Mesaj invalid!\n";
        if(message.getDate() == null)
            err += "Data invalida!\n";
        if(message.getIdReply() != null)
            if(message.getIdReply() <= 0)
                err += "Id reply invalid!\n";

        if(err.length() > 0)
            throw new ValidateException(err);
    }
}
