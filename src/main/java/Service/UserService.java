package Service;

import Domain.User;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.Repository;
import Validate.Validator;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * service used for storing users
 */
public class UserService extends AbstractService<Integer, User>{

    /**
     * @param repository the repository used by the service
     * @param validator the validator used by the service
     */
    public UserService(Repository repository, Validator validator) {
        super(repository, validator);
    }

    /**
     * @return the 'least' numeric ID which is not used
     * @throws SQLException if the database cannot be accessed
     * @deprecated since we are using a database
     */
    @Deprecated
    @Override
    public Integer generateId() throws SQLException {
        if (repository.size() == 0)
            return 1;
        for (int i = 1; i < repository.size(); i++) {
            if (repository.find(i) == null)
                return i;
        }
        return repository.size() + 1;
    }

    /**
     * @param params the parameters we need to update a user
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there already is an element with the given ID parameter
     */
    @Override
    public int createRecord(ArrayList<Object> params) throws SQLException, BusinessException, ValidateException, RepoException {
        if(params.size()!=2 && params.size() != 4)
            throw new BusinessException("Numar invalid de parametri\n");
        User user;
        String firstName =(String)params.get(0);
        String surname =(String)params.get(1);
        if(params.size() > 3) {
            String username = (String) params.get(2);
            String password = (String) params.get(3);
            user = new User(firstName, surname, username, password);
        }
        else {
            user = new User(firstName, surname, null, null);
        }

        validator.validate(user);
        int id = repository.add(user);
        return id;
    }

    /**
     * @param id of the user which will be updated
     * @param params the parameters we need to update a user
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there is no element with the given ID parameter
     */
    @Override
    public void updateRecord(Integer id, ArrayList<Object> params) throws BusinessException, ValidateException, SQLException, RepoException {
        if(params.size()!=2)
            throw new BusinessException("Numar invalid de parametri\n");

        String newFirstName =(String)params.get(0);
        String newSurname =(String)params.get(1);

        User user = new User(id, newFirstName, newSurname);
        validator.validate(user);
        repository.update(id, user);
    }
}
