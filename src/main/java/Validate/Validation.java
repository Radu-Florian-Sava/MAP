package Validate;

import Domain.Identifiable;
import Exceptions.ValidateException;

/**
 * @param <Id> generic ID of an element
 * @param <T>  instance of the Identifiable class
 *             validates an element using the strategy pattern logic
 */
public interface Validation<Id, T extends Identifiable<Id>> {
    /**
     * @param t generic element to be validated
     * @throws ValidateException throws a context-specific error if the element is not valid (depends on each case)
     */
     void genericValidate(T t) throws ValidateException;
}
