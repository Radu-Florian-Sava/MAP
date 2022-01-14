package Service;

import Domain.Identifiable;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @param <Id> generic ID of the stored elements
 * @param <T> instance of Identifiable and type of elements contained by the service
 */
public interface Service<Id, T extends Identifiable<Id>> {
    /**
     * @return number if elements contained by the service
     * @throws SQLException if the database cannot be accessed
     */
    int count() throws SQLException;

    /**
     * @return an iterable containing all the records
     * @throws SQLException if the database cannot be accessed
     */
    Iterable<T> getRecords() throws SQLException;

    /**
     * @param id of the element that we are looking for
     * @return the element with the given ID or null if the element doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    T findRecord(Id id) throws SQLException;

    /**
     * @param id of the element that we want to delete
     * @return the deleted element
     * @throws RepoException if there is no element with the given ID
     * @throws SQLException if the database cannot be accessed
     */
    T deleteRecord(Id id) throws RepoException, SQLException;

    /**
     * @return generates the 'least' ID not used by the service
     * @throws SQLException if the database cannot be accessed
     * @deprecated since we use databases
     */
    @Deprecated
    Id generateId() throws SQLException;

    /**
     * @param params list of parameters needed in order to create a new element
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen interactions between different entities
     * @throws ValidateException if the parameters provided are not valid for the element we want to create
     * @throws RepoException if there already is an element which has the ID we provide as a parameter
     */
    int createRecord(ArrayList<Object> params) throws SQLException, BusinessException, ValidateException, RepoException;

    /**
     * @param id of the element that we want to update
     * @param params list of parameters needed in order to update an element
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen interactions between different entities
     * @throws ValidateException if the parameters provided are not valid for the element we want to update
     * @throws RepoException if there is no element which has the ID we provide as a parameter
     */
    void updateRecord(Id id, ArrayList<Object> params) throws BusinessException, ValidateException, SQLException, RepoException;
}
