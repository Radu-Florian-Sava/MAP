package Validate;

import Domain.User;
import Exceptions.ValidateException;

import java.util.Objects;

/**
 * validates an user
 */
public class UserValidator implements Validation<Integer, User> {
    private String message;

    /**
     * the message is initialised as a null message by the constructor
     */
    public UserValidator() {
        message = "";
    }

    /**
     * @param user User class variable to be validated
     * @throws ValidateException if the element's id is not a natural number or if the FirstName or Surname are void
     */
    @Override
    public void genericValidate(User user) throws ValidateException {
        message = "";
        if (user.getId() < 1)
            message += "Id invalid!\n";
        if (Objects.equals(user.getFirstName(), ""))
            message += "Prenume invalid!\n";
        if (Objects.equals(user.getSurname(), ""))
            message += "Nume invalid!\n";
        if (message.length() > 0)
            throw new ValidateException(message);
    }
}
