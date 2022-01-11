package Service;

import Domain.Friendship;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.Repository;
import Validate.Validator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 *  service used for storing users
 */
public class FriendshipService extends AbstractService<Integer, Friendship>{

    /**
     * @param repository the repository used by the service
     * @param validator the validator used by the service
     */
    public FriendshipService(Repository repository, Validator validator) {
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
     * add a friendship in repository
     * @param params the parameters we need to create a new friendship
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there already is an element with the given ID parameter
     */
    @Override
    public int createRecord( ArrayList<Object> params) throws SQLException, BusinessException, ValidateException, RepoException {
        if(params.size()!=2)
            throw new BusinessException("Numar invalid de parametri\n");
        Integer one =(Integer)params.get(0);
        Integer two =(Integer)params.get(1);
        Friendship friendship = new Friendship(one, two);
        validator.validate(friendship);
        for (Friendship iter : repository.getAll()) {
            if (iter.getSender() == friendship.getSender() && iter.getReceiver() == friendship.getReceiver())
                throw new RepoException("Element existent\n");
            else if(iter.getSender() == friendship.getReceiver() && iter.getReceiver() == friendship.getSender()) {
                throw new RepoException("Element existent\n");
            }
        }
        int id = repository.add(friendship);
        return id;
    }

    /**
     * update a friendship in repository
     * @param params the parameters we need to update a friendship
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there is no element with the given ID
     */
    @Override
    public void updateRecord(Integer id, ArrayList<Object> params) throws BusinessException, ValidateException, SQLException, RepoException {
        if(params.size() != 3 && params.size() != 2)
            throw new BusinessException("Numar invalid de parametri\n");

        Integer newOne =(Integer)params.get(0);
        Integer newTwo =(Integer)params.get(1);
        Integer friendship_request = -1;
        Friendship friendship;
        if(params.size() == 3) {
            friendship_request = (Integer) params.get(2);
            friendship = new Friendship(
                    id,
                    newOne,
                    newTwo,
                    new Date(System.currentTimeMillis()),
                    friendship_request);
        }
        else
            friendship = new Friendship(
                    id,
                    newOne,
                    newTwo,
                    new Date(System.currentTimeMillis()));
        validator.validate(friendship);
        for(Friendship check : repository.getAll())
            if(check.equals(friendship) && !Objects.equals(check.getId(), id))
                throw new RepoException("Prietenie deja stabilita\n");
            else if(check.equals(friendship) && friendship_request == -1)
                friendship.setFriendship_request(check.getFriendship_request());
        repository.update(id, friendship);
    }
}
