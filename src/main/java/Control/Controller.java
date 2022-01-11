package Control;

import Domain.*;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.DatabaseFriendshipRepository;
import Repo.DatabaseMessageRepository;
import Repo.DatabaseUserRepository;
import Repo.Repository;
import Service.Service;
import Utils.Constants;
import Utils.Graph;
import Utils.Hasher;
import Utils.UtilsFunctions;
import Validate.FriendshipValidator;
import Validate.MessageValidator;
import Validate.UserValidator;
import Validate.Validator;
import Service.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *  highest level of the backend and information expert
 *  controller class which contains most of the application logic
 */
public class Controller {
    private static final Controller controller = new Controller();
    private Service<Integer, User> userService;
    private Service<Integer, Friendship> friendshipService;
    private Service<Integer, Message> messageService;

    /**
     *  private constructor, we are using the singleton design pattern
     */
    private Controller() {
        String url = "jdbc:postgresql://localhost:5432/mesagerie";
        String password = "1234";
        Repository<Integer, User> userRepository = new DatabaseUserRepository(
                url, "postgres", password);
        Validator<Integer, User> userValidator = new Validator<>(new UserValidator());
        userService = new UserService(userRepository, userValidator);

        Repository<Integer, Friendship> friendshipRepository = new DatabaseFriendshipRepository(
                url, "postgres", password);
        Validator<Integer, Friendship> friendshipValidator = new Validator<>(new FriendshipValidator());
        friendshipService = new FriendshipService(friendshipRepository, friendshipValidator);

        Repository<Integer, Message> messageRepository = new DatabaseMessageRepository(
                url, "postgres", password);
        Validator<Integer, Message> messageValidator = new Validator<>(new MessageValidator());
        messageService = new MessageService(messageRepository, messageValidator);
    }

    /**
     * @return the only instance of this class, part of the singleton design pattern
     */
    public static Controller getInstance() {
        return controller;
    }


    /**
     * @param userService  which contains information about the users
     * @param friendshipService which contains information about the friendship relationships
     * @param messageService which contains information about the messages between users
     * @deprecated since we are using the singleton design pattern
     */
    @Deprecated
    public Controller(Service<Integer, User> userService, Service<Integer, Friendship> friendshipService,
                      Service<Integer, Message> messageService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
    }

    /**
     * @param userService  which contains information about the users
     * @param friendshipService which contains information about the friendship relationships
     * @deprecated remains only for tests, use Controller(userService, friendshipService, messageService) instead
     */
    @Deprecated
    public Controller(Service userService, Service friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    /**
     * @param firstName of the user which is going to be added
     * @param surname of the user which is going to be added
     * @throws ValidateException if the at least one of the names are void ("")
     * @throws RepoException if there is an element with the assign ID already contained by the repository
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     * @deprecated use signup instead
     */
    @Deprecated
    public void addUser(String firstName, String surname) throws ValidateException, RepoException,
            SQLException, BusinessException, SQLException {
        ArrayList<Object> params = new ArrayList<>();
        params.add(firstName);
        params.add(surname);
        userService.createRecord(params);
    }


    /**
     * @param id of the user whose friends we want to find
     * @return a vector which contains the IDs of the friendship relationships of the user
     * WARNING: all relationships, REGARDLESS of the friendship FLAG
     * @throws SQLException if the database cannot be accessed
     */
    private Vector<Integer> friendsOf(int id) throws SQLException {
        Vector<Integer> friendshipVector = new Vector<>();
        for (Friendship friendship : friendshipService.getRecords()) {
            if (friendship.getReceiver() == id || friendship.getSender() == id)
                friendshipVector.add(friendship.getId());
        }
        return friendshipVector;
    }

    /**
     * @param id of the user which we want to delete
     * @return the deleted user
     * @throws RepoException if there is no user with the given ID
     * @throws SQLException  if the database cannot be accessed
     */
    public User deleteUser(int id) throws RepoException, SQLException {
        return userService.deleteRecord(id);
    }

    /**
     * @param id of the user which we are looking for
     * @return the user with the given ID or null if no user with the given ID exists
     * @throws SQLException if the database cannot be accessed
     */
    public User findUser(int id) throws SQLException {
        return userService.findRecord(id);
    }

    /**
     * @return an iterable containing all the users
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<User> getUsers() throws SQLException {
        return userService.getRecords();
    }

    /**
     * @return the number of users
     * @throws SQLException if the database cannot be accessed
     */
    public int numberOfUsers() throws SQLException {
        return userService.count();
    }

    /**
     * @param id of the user which we want to update
     * @param newFirstName of the user
     * @param newSurname of the user
     * @throws ValidateException if at least one of the names is void("")
     * @throws RepoException if there is no user with the given ID
     * @throws SQLException  if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     */
    public void updateUser(int id, String newFirstName, String newSurname) throws ValidateException,
            RepoException, SQLException, BusinessException {
        ArrayList<Object> params = new ArrayList<>();
        params.add(newFirstName);
        params.add(newSurname);
        userService.updateRecord(id, params);
    }

    /**
     * @param sender ID of the user who sent the friendship request
     * @param receiver  ID of the user who received the friendship request
     * @throws ValidateException if the IDs are not natural numbers
     * @throws RepoException if the friendship relationship's generated ID already exists or there already is
     *                       a friendship relationship between the users
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     * @deprecated use sendFriendshipRequest() instead
     */
    @Deprecated
    public void addFriendship(int sender, int receiver) throws ValidateException, RepoException,
            SQLException, BusinessException {
        if (userService.findRecord(sender) == null || userService.findRecord(receiver) == null ||
                sender == receiver)
            throw new ValidateException("Pereche id invalida\n");

        ArrayList<Object> params = new ArrayList<>();
        params.add(sender);
        params.add(receiver);

        friendshipService.createRecord(params);
    }

    /**
     * @param id of the friendship relationship that we want to delete
     * @return the deleted friendship relationship
     * @throws RepoException if there is no friendship relationship with the given ID
     * @throws SQLException if the database cannot be accessed
     */
    public Friendship deleteFriendship(int id) throws RepoException, SQLException {
        return friendshipService.deleteRecord(id);
    }

    /**
     * @param id of the friendship relationship that we are looking for
     * @return the friendship relationship with the given ID or null if it doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    public Friendship findFriendship(int id) throws SQLException {
        return friendshipService.findRecord(id);
    }

    /**
     * @return an iterable containing all the friendship relationships
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<Friendship> getFriendships() throws SQLException {
        return friendshipService.getRecords();
    }

    /**
     * @return the number of friendship relationships(update: it has become the number of friendship requests)
     * @throws SQLException if the database cannot be accessed
     */
    public int numberOfFriendships() throws SQLException {
        return friendshipService.count();
    }

    /**
     * @param id of the friendship relationship that we want to update
     * @param newSender ID of the user who sent the friendship request
     * @param newReceiver ID of the user who received the friendship request
     * @throws ValidateException if the IDs are not natural numbers
     * @throws RepoException if there is no friendship relationship with the given ID
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     * @deprecated only to be used with non-GUI
     */
    @Deprecated
    public void updateFriendship(int id, int newSender, int newReceiver) throws ValidateException, RepoException, SQLException, BusinessException {
        if (userService.findRecord(newSender) == null || userService.findRecord(newReceiver) == null)
            throw new ValidateException("Pereche id invalida\n");

        ArrayList<Object> params = new ArrayList<>();
        params.add(newSender);
        params.add(newReceiver);

        friendshipService.updateRecord(id, params);
    }

    /**
     * @return the number of communities between users
     *         (update: it has been reinterpreted as the users who sent friendship requests)
     * @throws SQLException if the database cannot be accessed
     * @deprecated it doesn't have much use in a graphical user interface application
     */
    @Deprecated
    public int numberOfCommunities() throws SQLException {
        Graph graph = new Graph(userService, friendshipService);
        return graph.connected();
    }

    /**
     * @return the most sociable community as an arrayList of users
     *         (updateL it has been reinterpreted)
     * @throws SQLException if the database cannot be accessed
     * @deprecated it doesn't have much use in a graphical user interface application
     */
    @Deprecated
    public ArrayList<User> mostSocial() throws SQLException {
        Graph graph = new Graph(userService, friendshipService);
        return graph.socialComponent();
    }

    /**
     * @param id_from is the ID of the user who sent the message
     * @param id_to is the ID of the user who received the message
     * @param message is the message body
     * @param id_reply is the ID of the message it refers as a reply (or null if it's not a reply)
     * @throws ValidateException if the message is void("") or the IDs are not natural numbers
     * @throws RepoException  if the entities referred via the IDs do not exist
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     */
    public void sendMessage(int id_from, int id_to, String message, Integer id_reply) throws SQLException, ValidateException, BusinessException, RepoException {
        if (userService.findRecord(id_from) == null || userService.findRecord(id_to) == null)
            throw new ValidateException("Id utilizatori invalid\n");

        ArrayList<Object> params = new ArrayList<>();
        params.add(id_from);
        params.add(id_to);
        params.add(message);
        params.add(id_reply);
        List<Friendship> friendships = (List<Friendship>) friendshipService.getRecords();
        boolean ok = friendships.stream().anyMatch((x) ->
                ((x.getSender() == id_from && x.getReceiver() == id_to) ||
                        (x.getSender() == id_to && x.getReceiver() == id_from)) &&
                        x.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP);
        if (!ok)
            throw new BusinessException("Nu exista prietenie dintre cei doi utilizatori\n");
        if (id_reply != null) {
            List<Message> messages = (List<Message>) messageService.getRecords();
            if (messages.stream().noneMatch((x) -> (x.getFrom() == id_from && x.getTo() == id_to)
            || (x.getFrom() == id_to && x.getTo() == id_from))) {
                throw new BusinessException("Mesajul replied nu apartine acestei conversatii\n");
            }
        }
        int id_message = messageService.createRecord(params);
        params = new ArrayList<>();
        params.add(id_from);
        params.add(id_to);
        params.add(id_message);
        params.add(id_reply);
        messageService.createRecord(params);
    }

    /**
     * @return an iterable containing all the messages
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<Message> getMessages() throws SQLException {
        List<Message> messages = (List<Message>) messageService.getRecords();

        return messages.stream().sorted(Comparator.comparing(Message::getDate)).collect(Collectors.toList());
    }

    /**
     * @param id of the message which is going to be deleted
     * @return the deleted message
     * @throws SQLException if the database cannot be accessed
     * @throws RepoException if the message with the given ID does not exist
     */
    public Message deleteMessage(int id) throws SQLException, RepoException {
        return messageService.deleteRecord(id);
    }

    /**
     * @param id1 is the id of one of the users
     * @param id2 is the id of the other user
     * @return an iterable containing the conversation between the two users as MessageDTOs, not Messages
     * @throws SQLException if the database cannot be accessed
     * this kind of iterable can be used to transfer information to the GUI
     */
    public Iterable<MessageDTO> getMessagesBy2Users(int id1, int id2) throws SQLException {
        List<Message> messages = (List<Message>) messageService.getRecords();
        ArrayList<MessageDTO> messages_filtered = new ArrayList<>();
        User user1 = findUser(id1);
        User user2 = findUser(id2);

        //the messages are sorted chronologically
        messages = messages.stream().sorted(
                Comparator.comparing(Message::getDate)).collect(Collectors.toList());

        // we transform the Messages into MessageDTOs
        for (Message message : messages) {
            if (message.getFrom() == id1 && message.getTo() == id2) {
                if (message.getIdReply() != null) {
                    Message message1 = messageService.findRecord(message.getIdReply());
                    MessageDTO messageDTO = new MessageDTO(
                            message.getId(),
                            message.getMessage(),
                            message1.getMessage(),
                            user1.getFirstName() + " " + user1.getSurname()
                    );
                    messages_filtered.add(messageDTO);
                }
                else {
                    MessageDTO messageDTO = new MessageDTO(
                            message.getId(),
                            message.getMessage(),
                            user1.getFirstName() + " " + user1.getSurname()
                    );
                    messages_filtered.add(messageDTO);
                }
            } else if (message.getFrom() == id2 && message.getTo() == id1) {
                if (message.getIdReply() != null) {
                    Message message1 = messageService.findRecord(message.getIdReply());
                    MessageDTO messageDTO = new MessageDTO(
                            message.getId(),
                            message.getMessage(),
                            user2.getFirstName() + " " + user2.getSurname(),
                            message1.getMessage()
                    );
                    messages_filtered.add(messageDTO);
                } else {
                    MessageDTO messageDTO = new MessageDTO(
                            message.getId(),
                            message.getMessage(),
                            user2.getFirstName() + " " + user2.getSurname()
                    );
                    messages_filtered.add(messageDTO);
                }
            }
        }

        return messages_filtered;
    }

    /**
     * @param id of the user whose friendship relationships we want to find
     * @return an iterable containing the ACCEPTED friendships of the user
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<Friendship> getAcceptedFriendshipsOf(int id) throws SQLException {
        return StreamSupport.stream(friendshipService.getRecords().spliterator(), false)
                .filter((x) -> (x.getSender() == id || x.getReceiver() == id) &&
                        x.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP)
                .toList();
    }

    /**
     * @param id of the user whose ACCEPTED friendships we want to get
     * @param monthNumber a natural number between 1 and 12 representing
     *                    the month when the friendship was created
     * @return an iterable containing the friendship relationships created during the given month
     * @throws SQLException if the database cannot be accessed
     * @deprecated it is not used with GUI
     */
    @Deprecated
    public Iterable<Friendship> getAcceptedFriendshipsWithMonth(int id, int monthNumber) throws SQLException {
        return StreamSupport.stream(friendshipService.getRecords().spliterator(), false)
                .filter((x) -> (x.getSender() == id || x.getReceiver() == id)
                        && x.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP
                        && (x.getStringDate().contains("-" + monthNumber + "-")
                        || x.getStringDate().contains("-0" + monthNumber + "-"))
                ).toList();
    }

    /**
     * @param id of the user whose friendship relationships we want to find
     * @return an iterable containing the PENDING friendships RECEIVED by the user with the given ID
     * @throws SQLException if the database cannot be accessed
     */
    public Iterable<Friendship> getPendingFriendshipsReceivedBy(int id) throws SQLException {
        return StreamSupport.stream(friendshipService.getRecords().spliterator(), false)
                .filter((x) -> x.getReceiver() == id && x.getFriendshipRequest() == Constants.PENDING_FRIENDSHIP)
                .toList();
    }

    /**
     * @param id_from is the ID of the user who sent the friendship request
     * @param id_to is the ID of the user who received the friendship request
     * @throws ValidateException if the IDs are not natural numbers
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen collisions with the other application entities
     * @throws RepoException if the friendship created has received an invalid ID
     */
    public void sendFriendship(int id_from, int id_to) throws ValidateException, BusinessException,
            SQLException, RepoException {
        if (userService.findRecord(id_from) == null ||
                userService.findRecord(id_to) == null ||
                id_from == id_to)
            throw new ValidateException("Pereche id invalida\n");

        List<Friendship> friendships = (List<Friendship>) friendshipService.getRecords();
        for (Friendship friendship : friendships)
            // at this point it is settled that an exception will be thrown, but it depends on the message
            if (friendship.getSender() == id_from && friendship.getReceiver() == id_to) {

                if (friendship.getFriendshipRequest() == Constants.PENDING_FRIENDSHIP) {
                    throw new BusinessException("A fost deja trimisa o cerere de prietenie");
                } else if (friendship.getFriendshipRequest() == Constants.REJECTED_FRIENDSHIP) {
                    friendshipService.deleteRecord(friendship.getId());
                } else if (friendship.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP) {
                    throw new BusinessException("Sunteti deja prieteni");
                }

            } else if (friendship.getSender() == id_to && friendship.getReceiver() == id_from) {
                if (friendship.getFriendshipRequest() == Constants.PENDING_FRIENDSHIP) {
                    ArrayList<Object> params = new ArrayList<>();
                    params.add(id_to);
                    params.add(id_from);
                    params.add(Constants.ACCEPTED_FRIENDSHIP);
                    friendshipService.updateRecord(friendship.getId(), params);
                    return;
                } else if (friendship.getFriendshipRequest() == Constants.REJECTED_FRIENDSHIP) {
                    friendshipService.deleteRecord(friendship.getId());
                } else if (friendship.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP) {
                    throw new BusinessException("Sunteti deja prieteni");
                }
            }

        ArrayList<Object> params = new ArrayList<>();
        params.add(id_from);
        params.add(id_to);

        friendshipService.createRecord(params);
    }

    /**
     * accept a friendship
     * @param id  friendship's ID
     * @param id_user the user who accepts it
     * @throws SQLException  if the database cannot be accessed
     * @throws ValidateException if the friendship ID is null
     * @throws BusinessException if a friendship relationship already exists
     * @throws RepoException if the ID given by the repository is duplicated
     */
    public void acceptFriendship(int id, int id_user) throws SQLException, ValidateException,
            BusinessException, RepoException {
        Friendship friendship = friendshipService.findRecord(id);
        if (friendship == null) {
            throw new ValidateException("Nu exista aceasta prietenie!");
        }
        if (friendship.getReceiver() == id_user) {
            if (friendship.getFriendshipRequest() == Constants.REJECTED_FRIENDSHIP) {
                throw new BusinessException("A fost deja refuzata!\n");
            } else if (friendship.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP) {
                throw new BusinessException("Deja sunteti prieteni!\n");
            } else {
                friendship.setFriendshipRequest(Constants.ACCEPTED_FRIENDSHIP);
                ArrayList<Object> params = new ArrayList<>();
                params.add(friendship.getSender());
                params.add(friendship.getReceiver());
                params.add(friendship.getFriendshipRequest());
                friendshipService.updateRecord(friendship.getId(), params);
            }
        } else {
            throw new ValidateException("Nu poate fi acceptata de acest utilizator!");
        }
    }

    /**
     * reject a friendship
     * @param id  friendship's ID
     * @param id_user the user who rejects it
     * @throws SQLException  if the database cannot be accessed
     * @throws ValidateException if the friendship ID is null
     * @throws BusinessException if a friendship relationship already exists
     * @throws RepoException if the ID given by the repository is duplicated
     */
    public void rejectFriendship(int id, int id_user) throws SQLException, ValidateException, BusinessException, RepoException {
        Friendship friendship = friendshipService.findRecord(id);
        if (friendship == null) {
            throw new ValidateException("Nu exista aceasta prietenie!");
        }
        if (friendship.getReceiver() == id_user) {
            if (friendship.getFriendshipRequest() == Constants.REJECTED_FRIENDSHIP) {
                throw new BusinessException("A fost deja refuzata!\n");
            } else if (friendship.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP) {
                throw new BusinessException("Deja sunteti prieteni!\n");
            } else {
                friendship.setFriendshipRequest(Constants.REJECTED_FRIENDSHIP);
                ArrayList<Object> params = new ArrayList<>();
                params.add(friendship.getSender());
                params.add(friendship.getReceiver());
                params.add(1);
                friendshipService.updateRecord(friendship.getId(), params);
            }
        } else {
            throw new ValidateException("Nu poate fi acceptata de acest utilizator!");
        }
    }

    /**
     * sends a message with the same body to multiple users (broadcast)
     * @param id_from is the ID of the sender
     * @param message is the message body
     * @param ids an arrayList containing the IDs of the receivers
     * @throws ValidateException if the message is void("")
     * @throws BusinessException if there are unforeseen problems with the entities interactions
     * @throws SQLException if the database cannot be accessed
     * @throws RepoException if the IDs referred do not belong to any users
     */
    public void sendMessageToIds(int id_from, String message, ArrayList<Integer> ids) throws ValidateException, BusinessException, SQLException, RepoException {
        if (userService.findRecord(id_from) == null)
            throw new ValidateException("Id utilizator trimitator invalid\n");

        ArrayList<Object> params = new ArrayList<>();
        params.add(id_from);
        params.add(1);
        params.add(message);
        params.add(null);
        List<Friendship> friendships = (List<Friendship>) friendshipService.getRecords();

        int id_message = messageService.createRecord(params);

        for (Integer id_to : ids) {

            boolean ok = friendships.stream().anyMatch((x) ->
                    ((x.getSender() == id_from && x.getReceiver() == id_to) ||
                            (x.getSender() == id_to && x.getReceiver() == id_from)) &&
                            x.getFriendshipRequest() == 2);
            if (!ok)
                throw new BusinessException("Nu exista prietenie cu " + id_to + "\n");

            params = new ArrayList<>();
            params.add(id_from);
            params.add(id_to);
            params.add(id_message);
            params.add(null);
            messageService.createRecord(params);
        }
    }

    /**
     * @param id of the user who sent the friendship REQUESTS
     * @return an arrayList containing the friendships(or more likely friendship requests) sent by the user
     * @throws SQLException if the database cannot be accessed
     */
    public ArrayList<Friendship> getSentFriendships(int id) throws SQLException {
        ArrayList<Friendship> friendships = new ArrayList<>();
        List<Friendship> friendshipList = (List<Friendship>) friendshipService.getRecords();
        for (Friendship friendship : friendshipList) {
            if (friendship.getSender() == id)
                friendships.add(friendship);
        }

        return friendships;
    }

    /**
     * @param id of the user whose friendship REQUESTS(PENDING,REJECTED, ACCEPTED) we want to see
     * @return a list containing the friendship requests extracted information as FriendshipDTOs
     * @throws SQLException if the database cannot be accessed
     */
    public List<FriendshipDTO> getAllTypesOfFriendshipsOf(int id) throws SQLException {
        List<Friendship> friendshipList = (List<Friendship>) friendshipService.getRecords();
        friendshipList = friendshipList.stream()
                .filter((x) -> (x.getSender() == id || x.getReceiver() == id)).toList();

        List<User> users = (List<User>) userService.getRecords();
        List<FriendshipDTO> friendshipDTOS = new ArrayList<>();
        for (Friendship friendship : friendshipList) {
            if (friendship.getSender() == id)
                friendshipDTOS.add(new FriendshipDTO(
                        0,
                        friendship.getId(),
                        users.stream().filter((x) -> x.getId() == friendship.getSender()).toList().get(0).toString(),
                        users.stream().filter((x) -> x.getId() == friendship.getReceiver()).toList().get(0).toString(),
                        friendship.getDate(),
                        UtilsFunctions.transformIntegerToStatusFriendship(friendship.getFriendshipRequest())
                ));
            else
                friendshipDTOS.add(new FriendshipDTO(
                        1,
                        friendship.getId(),
                        users.stream().filter((x) -> x.getId() == friendship.getReceiver()).toList().get(0).toString(),
                        users.stream().filter((x) -> x.getId() == friendship.getSender()).toList().get(0).toString(),
                        friendship.getDate(),
                        UtilsFunctions.transformIntegerToStatusFriendship(friendship.getFriendshipRequest())
                ));
        }
        return friendshipDTOS;
    }

    /**
     * @return a list of all the users whose information was strictly extracted via UserDTOs
     * @throws SQLException if the database cannot be accessed
     */
    public List<UserDTO> getAllUsersDTO() throws SQLException {
        List<User> users = (List<User>) userService.getRecords();
        List<UserDTO> userDTOS = new ArrayList<>();
        for (User user : users) {
            userDTOS.add(new UserDTO(user.getId(), user.getFirstName(), user.getSurname(),user.getUsername()));
        }
        return userDTOS;
    }

    /**
     * @param id1 is the ID of one of the users
     * @param id2 is the ID of the other user
     * @return true if the users are friends(ACCEPTED friendship request), false otherwise
     * @throws SQLException if the database cannot be accessed
     */
    public Boolean areFriends(int id1, int id2) throws SQLException {
        for (Friendship friendship : friendshipService.getRecords())
            if (
                    ((friendship.getSender() == id1 && friendship.getReceiver() == id2) ||
                            (friendship.getSender() == id2 && friendship.getReceiver() == id1))
                            && friendship.getFriendshipRequest() == Constants.ACCEPTED_FRIENDSHIP)
                return true;
        return false;
    }

    /**
     * @param username of the users who wants to log in
     * @param password the password of the user
     * @return the user who logged in
     * @throws SQLException if the database cannot be accessed
     * @throws NoSuchAlgorithmException if the hashing algorithm used cannot be accessed
     * @implNote : we use a SHA-256 algorithm to hash the passwords and keep the hashed passwords
     *             instead of just the passwords
     */
    public User login(String username, String password) throws SQLException, NoSuchAlgorithmException {
        List<User> list = ((List<User>) userService.getRecords()).stream()
                .filter((x) -> Objects.equals(x.getUsername(), username)).collect(Collectors.toList());
        if (list.size() == 0)
            return null;

        User user = list.get(0);

        if (!Hasher.isHashedCorrectly(user.getPassword(), password))
            return null;
        return user;
    }

    /**
     * @param firstname of the user who wants to sign up for the application
     * @param surname of the user who wants to sign up for the application
     * @param username of the user who wants to sign up for the application
     * @param password of the user who wants to sign up for the application
     * @param confirm the same password just to make sure that it was introduced correctly
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen interactions between the application entities
     * @throws ValidateException if the introduced values are not valid
     * @throws RepoException if the ID given by the repository is duplicated
     */
    public void signup(String firstname, String surname, String username, String password, String confirm) throws SQLException, BusinessException, ValidateException, RepoException {
        String error = "";
        if (firstname.length() == 0)
            error += "Firstname cannot be null!\n";
        if (surname.length() == 0)
            error += "Surname cannot be null!\n";
        if (username.length() == 0)
            error += "Username cannot be null!\n";
        if (password.length() == 0)
            error += "Password cannot be null!\n";
        if (!Objects.equals(password, confirm))
            error += "The passwords are not the same!\n";
        if (error.length() != 0)
            throw new BusinessException(error);
        ArrayList<Object> objects = new ArrayList<>();

        objects.add(firstname);
        objects.add(surname);
        objects.add(username);
        objects.add(password);
        userService.createRecord(objects);
    }

    /**
     * @param id of the user whose data we want to transfer to the PDF
     * @param date_start the date since we want to get the required information about the user
     * @param date_end the last date when we want to get the required information about the user
     * @param file_dest the file which will contain the messages and friendships related
     *                  to the user referred by ID
     * @throws IOException if the input/output interfaces malfunction
     * @throws SQLException if the database cannot be accessed
     */
    public void friendsAndMessagesBetweenADatePDF(int id, Date date_start, Date date_end, File file_dest)
            throws IOException, SQLException {
        PDDocument document = new PDDocument();
        PDPage pdPageFriendships = new PDPage();
        document.addPage(pdPageFriendships);

        List<FriendshipDTO> friendships = getAllTypesOfFriendshipsOf(id);

        PDPageContentStream contentStream = new PDPageContentStream(document, pdPageFriendships);

        contentStream.beginText();

        contentStream.setLeading(14.5f);

        contentStream.newLineAtOffset(25, 725);
        contentStream.setFont(PDType1Font.TIMES_BOLD, 15);
        contentStream.showText("Friendships:");
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 12);
        for (FriendshipDTO friendship : friendships) {
            if (friendship.getDate().getTime() >= date_start.getTime() &&
                    friendship.getDate().getTime() <= date_end.getTime()) {
                contentStream.newLine();
                String text;
                if (friendship.getId() == id)
                    text =
                            friendship.getFirstName() + " " +
                                    friendship.getStatus() + " " +
                                    friendship.getDate().toString();
                else
                    text =
                            friendship.getSecondName() + " " +
                                    friendship.getStatus() + " " +
                                    friendship.getDate().toString();
                text = text.replace("\n", "").replace("\r", "");
                contentStream.showText(text);
            }
        }

        contentStream.endText();
        contentStream.close();

        PDPage pdPageMessages = new PDPage();
        document.addPage(pdPageMessages);

        List<Message> messages = (List<Message>) getMessages();

        contentStream = new PDPageContentStream(document, pdPageMessages);

        contentStream.beginText();

        contentStream.setLeading(14.5f);

        contentStream.newLineAtOffset(25, 725);
        contentStream.setFont(PDType1Font.TIMES_BOLD, 15);
        contentStream.showText("Messages:");
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 12);
        for (Message message : messages) {
            if (
                    message.getTo() == id &&
                            message.getDate().getTime() >= date_start.getTime() &&
                            message.getDate().getTime() <= date_end.getTime()) {
                String text = userService.findRecord(message.getFrom()) + " " +
                        message.getMessage() + " " +
                        message.getDate();
                text = text.replace("\n", "").replace("\r", "");
                contentStream.newLine();
                contentStream.showText(text);
            }
        }

        contentStream.endText();
        contentStream.close();

        document.save(file_dest);
        document.close();
    }

    /**
     * @param date_start the date since we want to get the required information about the user
     * @param date_end the last date when we want to get the required information about the user
     * @param id of the user whose data we want to transfer to the PDF
     * @param friend of the user, we want to list the conversation between these two users
     * @param file_dest the file which will contain the messages and friendships related
     *                  to the user referred by ID
     * @throws IOException if the input/output interfaces malfunction
     * @throws SQLException if the database cannot be accessed
     * @throws BusinessException if there are unforeseen errors produced by interactions between
     *                           entities of the applications
     */
    public void messagesFromAFriendBetweenDatesPDF(Date date_start,
                                                   Date date_end,
                                                   int id,
                                                   UserDTO friend,
                                                   File file_dest)
            throws IOException, SQLException, BusinessException {

        List<Friendship> friendships = (List<Friendship>) friendshipService.getRecords();
        boolean ok = friendships.stream().anyMatch((x) ->
                ((x.getSender() == id && x.getReceiver() == friend.getId()) ||
                        (x.getSender() == friend.getId() && x.getReceiver() == id)) &&
                        x.getFriendshipRequest() == 2);
        if (!ok)
            throw new BusinessException("Nu exista prietenie dintre cei doi utilizatori\n");
        PDDocument pdDocument = new PDDocument();
        PDPage pdPage = new PDPage();
        pdDocument.addPage(pdPage);

        PDPageContentStream contentStream = new PDPageContentStream(pdDocument, pdPage);

        contentStream.beginText();
        contentStream.newLineAtOffset(25, 725);
        contentStream.setFont(PDType1Font.TIMES_BOLD, 15);
        contentStream.showText("Messages with: " + friend);
        contentStream.setFont(PDType1Font.TIMES_ITALIC, 12);
        for (Message message : messageService.getRecords()) {
            if (
                    message.getTo() == id &&
                            message.getFrom() == friend.getId() &&
                            message.getDate().getTime() >= date_start.getTime() &&
                            message.getDate().getTime() <= date_end.getTime()) {
                String text = message.getMessage() + " " + message.getDate();
                text = text.replace("\n", "").replace("\r", "");
                contentStream.newLine();
                contentStream.showText(text);
            }
        }
        contentStream.endText();
        contentStream.close();

        pdDocument.save(file_dest);
        pdDocument.close();
    }
}
