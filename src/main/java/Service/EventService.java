package Service;

import Domain.Event;
import Domain.Message;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.Repository;
import Utils.StatusEventUser;
import Validate.Validator;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;

/**
 *  service used for storing messages
 */
public class EventService extends AbstractService<Integer, Event> {

    /**
     * @param repository the repository used by the service
     * @param validator the validator used by the service
     */
    public EventService(Repository repository, Validator validator) {
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
        // mnu - this comment was made by Dragos, I left it here since I found it funny during code cleanup
        // and refactoring, may my sin be forgiven
        return 0;
    }

    /**
     * add an event in repository
     * @param params the parameters we need to create a new event
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there already is an element with the given ID parameter
     */
    @Override
    public int createRecord(ArrayList<Object> params) throws SQLException, BusinessException, ValidateException, RepoException {
        if(params.size() != 3)
            throw new BusinessException("Numar invalid de parametri\n");

        Event event;
        if(params.get(0).getClass().isAssignableFrom(Integer.class))
            event = new Event(
                    (Integer) params.get(0),
                    null,
                    null,
                    null,
                    (Integer) params.get(1),
                    (Integer) params.get(2) == 1 ? StatusEventUser.ORGANIZER : StatusEventUser.PARTICIPANT
            );
        else
            event = new Event(
                    (Timestamp) params.get(0),
                    (String) params.get(1),
                    (String) params.get(2)
            );
        validator.validate(event);
        return repository.add(event);
    }

    /**
     * we do not actually use this method, a message cannot be modified
     * instead we agreed on using the classic '*it was a typo, sorry'
     * @param integer id of the message to be modified
     * @param params the parameters we need to update a message
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there is no element with the given ID parameter
     */
    @Override
    public void updateRecord(Integer integer, ArrayList<Object> params) throws BusinessException, ValidateException, SQLException, RepoException {

    }
}