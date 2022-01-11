package Utils;

import Domain.Identifiable;

/**
 * @param <Id> generic ID of an element
 * @param <T>  instance of the Identifiable class
 *             validates an element using the strategy pattern logic
 */
@Deprecated
public interface TypeParser<Id, T extends Identifiable<Id>> {
    /**
     * @param attributes a list of attributes of the element that it's going to create
     * @return a register with the specified attributes of the list TRANSFORMED
     */
    T parse(String[] attributes);
}
