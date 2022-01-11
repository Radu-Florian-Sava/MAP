package Domain;

/**
 * @param <Id> is a generic ID type
 */
public interface Identifiable<Id> {

    /**
     * @return the ID of the element
     */
    Id getId();

}
