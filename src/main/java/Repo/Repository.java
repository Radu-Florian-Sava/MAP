package Repo;

import Domain.Identifiable;
import Exceptions.RepoException;
import java.sql.SQLException;

/**
 * @param <Id> generic id of an element contained by the repository
 * @param <T>  instance of Identifiable
 *           allows CRUD operations
 */

public interface Repository<Id, T extends Identifiable<Id>> {

    /**
     * @param t generic element which will be added
     * @throws RepoException depending on the context
     * @throws SQLException if it cannot connect to the database
     * @return the ID of the added element
     */
    Id add(T t) throws RepoException, SQLException;

    /**
     * @param id of the element which will be deleted
     * @return the deleted element
     * @throws RepoException depending on the context
     * @throws SQLException if it cannot connect to the database
     */
    T delete(Id id) throws RepoException, SQLException;

    /**
     * @param id of the element which will be updated
     * @param t  the new element which has the same ID and will replace the old one
     * @throws RepoException depending on the context
     * @throws SQLException if it cannot connect to the database
     */
    void update(Id id, T t) throws RepoException, SQLException;

    /**
     * @param id of the element we are looking for
     * @return the element which has the given ID
     * @throws SQLException if it cannot connect to the database
     */
    T find(Id id) throws SQLException;

    /**
     * @return an iterable of generic type
     * @throws SQLException if it cannot connect to the database
     */
    Iterable<T> getAll() throws SQLException;

    /**
     * @return the number of records inside the repository
     * @throws SQLException if it cannot connect to the database
     */
    int size() throws SQLException;
}
