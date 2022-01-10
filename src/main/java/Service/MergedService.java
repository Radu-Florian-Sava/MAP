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
 * @deprecated folositi Controller impreuna cu UserService si FriendshipService
 * permite aplicarea anumitor operatii obiectelor din aplicatie si realizeaza anumite statistici pe baza informatiilor
 * extrase
 */
@Deprecated
public class MergedService {
    private Repository<Integer, User> userRepository;
    private Repository<Integer, Friendship> friendshipRepository;
    private UserValidator userValidator;
    private FriendshipValidator friendshipValidator;

    /**
     * @param userRepository       numele fisierului in care sunt stocati utilizatorii
     * @param friendshipRepository numele fisierului in care sunt stocate relatiile de prietenie
     */
    public MergedService(Repository userRepository, Repository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        userValidator = new UserValidator();
        friendshipValidator = new FriendshipValidator();
    }

    /**
     * @param repository repozitoriul pentru care va fi generat id-ul
     * @param <T> clasa derivata din Identifiable
     * @return cel mai mic id numar intreg strict pozitiv care poate fi asignat
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
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
     * @param firstName numele utilizatorului care va fi adaugat
     * @param surname   prenumele utilizatorului care va fi adaugat
     * @throws ValidateException arunca exceptie daca numele/ prenumele sunt vide
     * @throws RepoException     arunca exceptie daca id-ul asignat este duplicat
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public void addUser(String firstName, String surname) throws ValidateException, RepoException, SQLException {
        User user = new User(generateId(userRepository), firstName, surname);
        userValidator.genericValidate(user);
        userRepository.add(user);
    }

    /**
     * @param id este id-ul elementului cautat (relatie de prietenie)
     * @return returneaza o lista cu 2 id-uri a utilizatorilor prieteni corespunzatori relatiei de prietenie
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
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
     * @param id reprezinta id-ul utilizatorului care va fi sters
     * @return returneaza utilizatorul cu id-ul id care a fost sters
     * @throws RepoException arunca exceptie daca elementul cu id-ul dat nu exista
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public User deleteUser(int id) throws RepoException, SQLException {
        for (int friendshipId : friendsOf(id)) {
            friendshipRepository.delete(friendshipId);
        }
        return userRepository.delete(id);
    }

    /**
     * @param id este id-ul utilizatorului cautat
     * @return returneaza utilizatorul cu id-ul dat sau null daca nu exista
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public User findUser(int id) throws SQLException {
        return userRepository.find(id);
    }

    /**
     * @return returneaza o lista iterabila cu toti utilizatorii
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public Iterable<User> getUsers() throws SQLException {
        return userRepository.getAll();
    }

    /**
     * @return returneaza numarul de utilizatori din aplicatie
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public int numberOfUsers() throws SQLException {
        return userRepository.size();
    }

    /**
     * @param id este id-ul utilizatorului care va fi modificat
     * @param newFirstName este noul nume al utilizatorului
     * @param newSurname   este noul prenume al utilizatorului
     * @throws ValidateException arunca exceptie daca numele/prenumele este vid
     * @throws RepoException     arunca exceptie daca id-ul nu corespunde unui utilizator
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public void updateUser(int id, String newFirstName, String newSurname) throws ValidateException, RepoException, SQLException {
        User user = new User(id, newFirstName, newSurname);
        userValidator.genericValidate(user);
        userRepository.update(id, user);
    }

    /**
     * @param one id-ul unui utilizator participant la relatia de prietenie
     * @param two id-ul celui de-al doilea utilizator participant la relatia de prietenie
     * @throws ValidateException arunca exceptie daca id-urile nu corespund unor utilizatori sau daca sunt egale
     * @throws RepoException     arunca exceptie daca exista deja un element cu id-ul nou generat
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
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
     * @param id este id-ul relatiei de prietenie care va fi stearsa
     * @return returneza relatia de prietenie stearsa
     * @throws RepoException arunca exceptie daca nu exista o relatie de prietenie cu id-ul dat
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public Friendship deleteFriendship(int id) throws RepoException, SQLException {
        return friendshipRepository.delete(id);
    }

    /**
     * @param id este id-ul relatiei de prietenie pe care o cautam
     * @return returneaza relatia de prietenie cu id-ul dat sau null daca nu exista
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public Friendship findFriendship(int id) throws SQLException {
        return friendshipRepository.find(id);
    }

    /**
     * @return returneaza o lista iterabila cu toate relatiile de prietenie
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public Iterable<Friendship> getFriendships() throws SQLException {
        return friendshipRepository.getAll();
    }

    /**
     * @return returneaza numarul de relatii de prietenie existente dintre utilizatorii aplicatiei
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public int numberOfFriendships() throws SQLException {
        return friendshipRepository.size();
    }

    /**
     * @param id     id-ul relatiei de prietenie pe care dorim sa o modificam
     * @param newOne este id-ul noului utilizator care ia parte la relatia de prietenie
     * @param newTwo este id-ul celuilalt nou utilizator care ia parte la relatia de prietenie
     * @throws ValidateException arunca exceptie daca id-urile nu corespund unor utilizatori ai aplicatiei
     * @throws RepoException     arunca exceptie daca nu exista o relatie de prietenie cu id-ul dat
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
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
     * @return returneaza numarul de comunitati din aplicatie in functie de realtiile de prietenie dintre utilizatori
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public int numberOfCommunities() throws SQLException {
        MergeGraph mergeGraph = new MergeGraph(this);
        return mergeGraph.connected();
    }

    /**
     * @return returneaza utiilizatorii membri din cea mai activa comunitate
     * @throws SQLException arunca exceptie daca apar probleme cu conexiunea bazei de date
     */
    public ArrayList<User> mostSocial() throws SQLException {
        MergeGraph mergeGraph = new MergeGraph(this);
        return mergeGraph.socialComponent();
    }
}
