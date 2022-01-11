package Service;

import Domain.Identifiable;
import Exceptions.RepoException;
import Repo.Repository;
import Validate.Validator;

import java.sql.SQLException;

/**
 * @param <Id> generic ID of an element
 * @param <T> instance of Identifiable
 */
public abstract class AbstractService<Id, T extends Identifiable<Id>> implements Service<Id, T> {
    protected Repository<Id, T> repository;
    protected Validator<Id, T> validator;

    /**
     * @param repository used by the service to contain the elements
     * @param validator used by the service to validate the elements
     */
    public AbstractService(Repository repository, Validator validator) {
        super();
        this.validator = validator;
        this.repository = repository;
    }

    /**
     * @return the number of elements contained by the service
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public int count() throws SQLException {
        return repository.size();
    }

    /**
     * @return an iterable containing all the elements
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Iterable<T> getRecords() throws SQLException {
        return repository.getAll();
    }

    /**
     * @param id of the element we are looking for
     * @return the element with the given ID or null if it doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public T findRecord(Id id) throws SQLException {
        return repository.find(id);
    }

    /**
     * @param id of the element we want to delete
     * @return the deleted element
     * @throws RepoException if the element with the given ID doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public T deleteRecord(Id id) throws RepoException, SQLException {
        return repository.delete(id);
    }
}
