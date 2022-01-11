package Service;

import Domain.Friendship;
import Domain.Identifiable;
import Domain.User;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.Repository;
import Utils.MergeGraph;
import Validate.FriendshipValidator;
import Validate.UserValidator;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

/**
 * @deprecated use Controller from the Control layer instead, it is much more elegant
 * allows operations on the user service and friendship service at a basic and rather primitive level
 */
@Deprecated
public class MergedService {
    private Repository<Integer, User> userRepository;
    private Repository<Integer, Friendship> friendshipRepository;
    private UserValidator userValidator;
    private FriendshipValidator friendshipValidator;

    /**
     * @param userRepository  the user repository we use
     * @param friendshipRepository the friendship repository we use
     */
    public MergedService(Repository userRepository, Repository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        userValidator = new UserValidator();
        friendshipValidator = new FriendshipValidator();
    }

    /**
     * @param repository for which the ID will be generated
     * @param <T> instance of Identifiable
     * @return the 'least' id not used by the repository
     * @throws SQLException if the database cannot be accessed
     */
    private <T extends Identifiable<Integer>> int generateId(Repository<Integer, T> repository) throws SQLException {
        if (repository.size() == 0)
            return 1;
        for (int i = 1; i < repository.size(); i++) {
            if (repository.find(i) == null)
                return i;
        }
        return repository.size() + 1;
    }

    /**
     * @param firstName of the user that we want to add
     * @param surname of the user that we want to add
     * @throws ValidateException if the firstName/surname are void("")
     * @throws RepoException if there is a duplicated ID(or rather an attempt)
     * @throws SQLException if the database cannot be accessed
     */
    public void addUser(String firstName, String surname) throws ValidateException, RepoException, SQLException {
        User user = new User(generateId(userRepository), firstName, surname);
        userValidator.genericValidate(user);
        userRepository.add(user);
    }

    /**
     * @param id of the user whose friends we want to find
     * @return a vector of the friendships' IDs
     * @throws SQLException if the database cannot be accessed
     */
    private Vector<Integer> friendsOf(int id) throws SQLException {
        Vector<Integer> friendshipVector = new Vector<>();
        for (Friendship friendship : friendshipRepository.getAll()) {
            if (friendship.getReceiver() == id || friendship.getSender() == id)
                friendshipVector.add(friendship.getId());
        }
        return friendshipVector;
    }

    /**
     * @param id of the user that we want to delete
     * @return the user we deleted
     * @throws RepoException if the element doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    public User deleteUser(int id) throws RepoException, SQLException {
        for (int friendshipId : friendsOf(id)) {
            friendshipRepository.delete(friendshipId);
        }
        return userRepository.delete(id);
    }

    /**
     * @param id of the user we are looking for
     * @return the user with the given ID or null if it doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    public User findUser(int id) throws SQLException {
        return userRepository.find(id);
    }

    /**
     * @return an iterable containing all the users
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<User> getUsers() throws SQLException {
        return userRepository.getAll();
    }

    /**
     * @return the number of users contained by the repository
     * @throws SQLException if the database cannot be accessed
     */
    public int numberOfUsers() throws SQLException {
        return userRepository.size();
    }

    /**
     * @param id of the element which we want to update
     * @param newFirstName of the user which we want to update
     * @param newSurname of the user which we want to update
     * @throws ValidateException if one of the names if void("")
     * @throws RepoException if there is no user with the given ID
     * @throws SQLException if the database cannot be accessed
     */
    public void updateUser(int id, String newFirstName, String newSurname) throws ValidateException, RepoException, SQLException {
        User user = new User(id, newFirstName, newSurname);
        userValidator.genericValidate(user);
        userRepository.update(id, user);
    }

    /**
     * creates a new friendship relationship between the two users given by IDs
     * @param one id of an user
     * @param two another id of a different user
     * @throws ValidateException if the IDs are negative or equal
     * @throws RepoException if the users don't exist
     * @throws SQLException if the database cannot be accessed
     */
    public void addFriendship(int one, int two) throws ValidateException, RepoException, SQLException {
        if (userRepository.find(one) == null || userRepository.find(two) == null || one == two)
            throw new ValidateException("Pereche id invalida\n");

        int id = generateId(friendshipRepository);
        Friendship friendship = new Friendship(id, one, two);
        friendshipValidator.genericValidate(friendship);
        for (Friendship friendship1 : friendshipRepository.getAll()) {
            if (friendship1.equals(friendship))
                throw new RepoException("Element existent\n");
        }
        friendshipRepository.add(friendship);
    }

    /**
     * @param id of the friendship relationship which we are going to delete
     * @return the deleted friendship relationship
     * @throws RepoException if there is no friendship with the given ID in the repository
     * @throws SQLException if the database cannot be accessed
     */
    public Friendship deleteFriendship(int id) throws RepoException, SQLException {
        return friendshipRepository.delete(id);
    }

    /**
     * @param id of the friendship relationship that we are looking for
     * @return the friendship relationship with the given ID or null if it doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    public Friendship findFriendship(int id) throws SQLException {
        return friendshipRepository.find(id);
    }

    /**
     * @return an iterable containing the friendship relationships
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<Friendship> getFriendships() throws SQLException {
        return friendshipRepository.getAll();
    }

    /**
     * @return the number of friendships contained by the repository
     * @throws SQLException if the database cannot be accessed
     */
    public int numberOfFriendships() throws SQLException {
        return friendshipRepository.size();
    }

    /**
     * @param id of the friendship relationship that we want to update
     * @param newOne id of one of the new users
     * @param newTwo id of the other new user
     * @throws ValidateException if the user IDs are invalid or the friendship cannot be found
     * @throws RepoException if there is no friendship with the given ID
     * @throws SQLException if the database cannot be accessed
     */
    public void updateFriendship(int id, int newOne, int newTwo) throws ValidateException, RepoException, SQLException {
        if (userRepository.find(newOne) == null || userRepository.find(newTwo) == null)
            throw new ValidateException("Pereche id invalida\n");

        Friendship friendship = new Friendship(id, newOne, newTwo);
        friendshipValidator.genericValidate(friendship);
        for(Friendship check : friendshipRepository.getAll())
            if(check.equals(friendship) && !Objects.equals(check.getId(), id))
                throw new RepoException("Prietenie deja stabilita\n");
        friendshipRepository.update(id, friendship);
    }

    /**
     * @return the number of user communities
     * @throws SQLException if the database cannot be accessed
     */
    public int numberOfCommunities() throws SQLException {
        MergeGraph mergeGraph = new MergeGraph(this);
        return mergeGraph.connected();
    }

    /**
     * @return the most sociable community composed of the users contained in the arrayList returned
     * @throws SQLException if the database cannot be accessed
     */
    public ArrayList<User> mostSocial() throws SQLException {
        MergeGraph mergeGraph = new MergeGraph(this);
        return mergeGraph.socialComponent();
    }
}
