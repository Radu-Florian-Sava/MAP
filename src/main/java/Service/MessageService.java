package Service;

import Domain.Message;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.Repository;
import Validate.Validator;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;


/**
 *  service used for storing messages
 */
public class MessageService extends AbstractService<Integer, Message> {

    /**
     * @param repository the repository used by the service
     * @param validator the validator used by the service
     */
    public MessageService(Repository repository, Validator validator) {
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
     * add a message in repository
     * @param params the parameters we need to create a new message
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException for unforeseen errors which might appear when the entities in our application
     *                           interact with one another
     * @throws ValidateException if the element contains invalid data
     * @throws RepoException if there already is an element with the given ID parameter
     */
    @Override
    public int createRecord(ArrayList<Object> params) throws SQLException, BusinessException, ValidateException, RepoException {
        if(params.size() != 4)
            throw new BusinessException("Numar invalid de parametri\n");

        Integer id_reply;
        Object id_reply_object = params.get(3);
        if(id_reply_object == null)
            id_reply = null;
        else
            id_reply = (Integer) id_reply_object;
        Message message;
        if(params.get(2).getClass().isAssignableFrom(Integer.class))
         message = new Message(
                 (Integer) params.get(2),
                 (Integer) params.get(0),
                 (Integer) params.get(1),
                 null,
                 Timestamp.from(Instant.now()),
                 id_reply
        );
        else
            message = new Message(
                    1,
                    1,
                    (String) params.get(2),
                    Timestamp.from(Instant.now()),
                    null
            );
        validator.validate(message);
        return repository.add(message);
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
