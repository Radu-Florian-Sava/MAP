package Repo;

import Domain.Identifiable;
import Exceptions.RepoException;

/**
 * @param <Id> generic ID of an element
 * @param <T>  instance of Identifiable
 *             the elements have the generic type T
 *             IMPORTANT: since it is in memory it will disappear when the application in closed
 */
@Deprecated
public class InMemoryRepository<Id, T extends Identifiable<Id>> extends AbstractRepository<Id, T> {

    /**
     * standard constructor
     */
    public InMemoryRepository() {
        super();
    }

    /**
     * @param t element which will be added
     * @throws RepoException if there already is an element with the ID of t
     * @return the ID of the added element
     */
    @Override
    public Id add(T t) throws RepoException {
        if (elems.containsKey(t.getId()))
            throw new RepoException("Element existent\n");
        elems.put(t.getId(), t);
        return t.getId();
    }

    /**
     * @param id of the deleted element
     * @return the deleted element
     * @throws RepoException if there is no element with the given ID
     */
    @Override
    public T delete(Id id) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        return elems.remove(id);
    }

    /**
     * @param id of the element to be updated
     * @param t  element with the same ID which will replace the existing element
     * @throws RepoException if there is no element with the given ID
     */
    @Override
    public void update(Id id, T t) throws RepoException {
        if (!elems.containsKey(id))
            throw new RepoException("Element inexistent\n");
        elems.replace(id, t);
    }
}
