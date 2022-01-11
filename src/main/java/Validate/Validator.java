package Validate;

import Domain.Identifiable;
import Exceptions.ValidateException;

/**
 * @param <Id> generic ID of an element
 * @param <T>  instance of the Identifiable class
 *             validates an element using the strategy pattern logic
 */
public class Validator<Id, T extends Identifiable<Id>> {
    private Validation<Id, T> validation;

    /**
     * @param validation generic validator according to strategy pattern
     */
    public Validator(Validation<Id, T> validation) {
        this.validation = validation;
    }

    /**
     * @param t generic element to be validated
     * @throws ValidateException throws a context-specific error if the element is not valid
     */
    public void validate(T t) throws ValidateException {
        validation.genericValidate(t);
    }

}
