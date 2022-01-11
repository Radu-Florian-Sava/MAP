package Repo;

import Domain.Identifiable;

import java.util.HashMap;
import java.util.Map;

/**
 * @param <Id> generic id of an element contained by the repository
 * @param <T>  instance of Identifiable which partially implements the interface
 */
@Deprecated
public abstract class AbstractRepository<Id, T extends Identifiable<Id>> implements Repository<Id, T> {

    protected Map<Id, T> elems;

    /**
     * constructor which initiates the container of elements with a new HashMap
     */
    public AbstractRepository() {
        this.elems = new HashMap<>();
    }

    /**
     * @param id of the element we are looking for
     * @return the element which has the given id or null if it is not in the repository
     */
    @Override
    public T find(Id id) {
        if (!elems.containsKey(id))
            return null;
        return elems.get(id);
    }

    /**
     * @return an iterable containing the values of the HashMap
     */
    @Override
    public Iterable<T> getAll() {
        return elems.values();
    }

    /**
     * @return the number of elements currently contained by the repository
     */
    @Override
    public int size() {
        return elems.size();
    }
}
