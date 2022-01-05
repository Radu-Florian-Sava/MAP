package Testing;

import Control.Controller;
import Domain.Friendship;
import Domain.Message;
import Domain.User;
import Exceptions.BusinessException;
import Exceptions.RepoException;
import Exceptions.ValidateException;
import Repo.*;
import Service.MergedService;
import Service.Service;
import Service.UserService;
import Service.FriendshipService;
import Utils.FriendshipParser;
import Utils.TypeParser;
import Utils.UserParser;
import Validate.FriendshipValidator;
import Validate.UserValidator;
import Validate.Validator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * clasa de teste efectuate cu junit
 */
public class Tests {

    /**
     * teste pentru elementele din domeniul aplicatiei
     */

    @Test
    public void repoTest() throws SQLException, RepoException {
        Repository<Integer, Message> repoMesaje =
                new DatabaseMessageRepository(
                        "jdbc:postgresql://localhost:5432/tests", "postgres", "1234");

        Repository<Integer, Friendship> repoPrietenii =
                new DatabaseFriendshipRepository(
                        "jdbc:postgresql://localhost:5432/tests", "postgres", "1234");

        Repository<Integer, User> repoUtilizatori =
                new DatabaseUserRepository(
                        "jdbc:postgresql://localhost:5432/tests", "postgres", "1234");

        repoUtilizatori.add(new User("Dragos", "Spiridon"));
        repoUtilizatori.add(new User("Teodor", "Spiridon"));

        List<User> users = (List<User>) repoUtilizatori.getAll();
        String mes = "Saluttare :muscle:";
        assertEquals(repoMesaje.size(), 0);

        repoMesaje.add(new Message(
                users.get(0).getId(),
                users.get(1).getId(),
                mes,
                Timestamp.from(Instant.now()),
                null
        ));

        assertEquals(repoMesaje.size(), 1);
        List<Message> messages = (List<Message>) repoMesaje.getAll();
        Message message = repoMesaje.find(messages.get(0).getId());
        assertEquals(message.getMessage(), mes);
        assertNull(message.getId_reply());
        assertEquals(java.util.Optional.of(message.getFrom()), users.get(0).getId());
        assertEquals(java.util.Optional.of(message.getTo()), users.get(1).getId());

        mes = "PIZZA CAPRICIOSA!!!!!!!!!";

        repoMesaje.add(new Message(
                users.get(1).getId(),
                users.get(0).getId(),
                mes,
                Timestamp.from(Instant.now()),
                message.getId()
        ));

        assertEquals(repoMesaje.size(), 2);
        messages = (List<Message>) repoMesaje.getAll();
        Message message1 = repoMesaje.find(messages.get(1).getId());
        assertEquals(message1.getMessage(), mes);
        assertEquals(message1.getId_reply(), message.getId());
        assertEquals(java.util.Optional.of(message1.getFrom()), users.get(1).getId());
        assertEquals(java.util.Optional.of(message1.getTo()), users.get(0).getId());

        Message message2 = repoMesaje.find(message1.getId_reply());

        assertEquals(message2.getMessage(), message.getMessage());
        assertEquals(message2.getId_reply(), message.getId_reply());
        assertEquals(message2.getFrom(), message.getFrom());
        assertEquals(message2.getTo(), message.getTo());

        Message delete_message = repoMesaje.delete(message2.getId());

        assertEquals(delete_message.getMessage(), message.getMessage());
        assertEquals(delete_message.getId_reply(), message.getId_reply());
        assertEquals(delete_message.getFrom(), message.getFrom());
        assertEquals(delete_message.getTo(), message.getTo());

        try {
            repoMesaje.delete(-1);
            fail();
        } catch(RepoException err) {
            assertTrue(true);
        }

        Message find_message = repoMesaje.find(-1);
        assertNull(find_message);

        repoUtilizatori.delete(users.get(0).getId());
        repoUtilizatori.delete(users.get(1).getId());
    }

    @Test
    public void testDomain() {
        User user1 = new User(1, "Stan", "Castan");
        assertEquals(java.util.Optional.ofNullable(user1.getId()), 1);
        assertEquals(user1.getFirstName(), "Stan");
        assertEquals(user1.getSurname(), "Castan");

        user1.setFirstName("Ion");
        user1.setSurname("Camion");
        assertEquals(user1.getFirstName(), "Ion");
        assertEquals(user1.getSurname(), "Camion");

        User user2 = new User(1, "TotStan", "Castan");
        assertEquals(user1, user2);

        assertEquals(user1.toString(), "1;Ion;Camion\n");

        user2 = new User(2, "TotStan", "Castan");

        Friendship friendship1 = new Friendship(1, user1.getId(), user2.getId());
        assertEquals(java.util.Optional.ofNullable(friendship1.getId()), 1);
        assertEquals(java.util.Optional.of(friendship1.getOne()), user1.getId());
        assertEquals(java.util.Optional.of(friendship1.getTwo()), user2.getId());

        assertEquals(friendship1.toString(), "1;1;2\n");

        Friendship friendship2 = new Friendship(1, 1, 4);
        assertEquals(friendship1, friendship2);

        friendship2 = new Friendship(3, 1, 2);
        assertEquals(friendship1, friendship2);
    }

    /**
     * teste pentru exceptiile creeate de utilizator
     */
    @Test
    public void testException() {
        RepoException repoException;
        repoException = new RepoException("Element existent!\n");
        assertEquals(repoException.getMessage(), "Element existent!\n");

        ValidateException validateException;
        validateException = new ValidateException("Id invalid!\n");
        assertEquals(validateException.getMessage(), "Id invalid!\n");

        BusinessException businessException;
        businessException = new BusinessException("Numar invalid de parametri\n");
        assertEquals(businessException.getMessage(), "Numar invalid de parametri\n");
    }

    /**
     * teste pentru elementele validatori
     */
    @Test
    public void testValidate() {
        User user1 = new User(1, "Stan", "Castan");
        Validator<Integer, User> validator = new Validator<>(new UserValidator());
        try {
            validator.validate(user1);
        } catch (ValidateException validateException) {
            fail();
        }
        User user2 = new User(-7, "", "");
        try {
            validator.validate(user2);
            fail();
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Id invalid!\nPrenume invalid!\nNume invalid!\n");
        }

        Friendship friendship1 = new Friendship(1, 1, 2);
        Validator<Integer, Friendship> validator1 = new Validator<>(new FriendshipValidator());
        try {
            validator1.validate(friendship1);
        } catch (ValidateException validateException) {
            fail();
        }
        Friendship friendship2 = new Friendship(0, -7, 0);
        try {
            validator1.validate(friendship2);
            fail();
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Id invalid!\nPrimul utlilizator nu e valid!\nAl doilea utlilizator nu e valid!\n");
        }
    }

    /**
     * teste pentru repozitoriu
     */
    @Test
    public void testInMemoryRepo() {
        InMemoryRepository<Integer, User> inMemoryRepository1 = new InMemoryUserRepository();
        assertEquals(inMemoryRepository1.size(), 0);
        try {
            inMemoryRepository1.add(new User(1, "Sigma", "Male"));
            inMemoryRepository1.add(new User(2, "Alpha", "Male"));
            inMemoryRepository1.add(new User(3, "In dev", "Male"));
        } catch (RepoException repoException) {
            fail();
        }

        assertEquals(inMemoryRepository1.size(), 3);
        assertEquals(inMemoryRepository1.find(1), new User(1, "Castan", "Stan"));
        Iterable<User> iterable1 = inMemoryRepository1.getAll();
        int counter = 1;
        for (User user : iterable1)
            assertEquals(java.util.Optional.ofNullable(user.getId()), counter++);
        try {
            assertEquals(java.util.Optional.ofNullable(inMemoryRepository1.delete(1).getId()), 1);
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(inMemoryRepository1.size(), 2);
        assertNull(inMemoryRepository1.find(7));

        try {
            inMemoryRepository1.update(2, new User(2, "Shrigma", "Male"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(inMemoryRepository1.find(2).toString(), "2;Shrigma;Male\n");

        try {
            inMemoryRepository1.add(new User(2, "Beta", "Male"));
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element existent\n");
        }

        try {
            inMemoryRepository1.delete(7);
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            inMemoryRepository1.update(1, new User(1, "Beta", "Male"));
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        InMemoryRepository<Integer,Friendship> inMemoryRepository2=new InMemoryFriendshipRepository();
        try {
            inMemoryRepository2.add(new Friendship(1,1,2));
            inMemoryRepository2.add(new Friendship(2,2,3));
            inMemoryRepository2.add(new Friendship(3,3,4));
        } catch (RepoException e) {
            e.printStackTrace();
        }

        assertEquals(inMemoryRepository2.size(),3);
        counter=1;
        for(Friendship friendship: inMemoryRepository2.getAll()){
            assertEquals(java.util.Optional.ofNullable(friendship.getId()),counter);
            assertEquals(friendship.getOne(),counter);
            assertEquals(friendship.getTwo(),counter+1);
            counter++;
        }

    }

    @Test
    public void testFileRepo() {
        FileRepository<Integer, User> fileRepository1 = new FileUserRepository("test.txt", new UserParser());
        assertEquals(fileRepository1.size(), 0);
        try {
            fileRepository1.add(new User(1, "Stan", "Castan"));
            fileRepository1.add(new User(2, "Ion", "Camion"));
            fileRepository1.add(new User(3, "Ceva", "Altceva"));
        } catch (RepoException repoException) {
            fail();
        }

        try {
            fileRepository1.add(new User(1, "Man", "Castan"));
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element existent\n");
        }

        try {
            fileRepository1.delete(7);
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            fileRepository1.update(701, new User(701, "Ogre", "Ogre"));
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            fileRepository1.update(3, new User(3, "John", "Lennon"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(fileRepository1.size(), 3);
        try {
            fileRepository1.delete(1);
            fileRepository1.delete(2);
            fileRepository1.delete(3);
        } catch (RepoException repoException) {
            fail();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("test.txt"));
            bufferedWriter.write("1;Stan;Castan\n");
            bufferedWriter.write(("2;Ion;Camion\n"));
            bufferedWriter.write("Castan");
            bufferedWriter.close();
            FileRepository<Integer, User> fileRepository2 = new FileRepository<>("test.txt", new UserParser());
            assertEquals(fileRepository2.size(), 2);
            File f = new File("test.txt");
            f.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * teste pentru elementele utilitare din cadrul aplicatiei
     */
    @Test
    public void testUtils() {
        String[] strings1 = {"1", "Stan", "Castan"};
        UserParser userParser = new UserParser();
        User user1 = userParser.parse(strings1);
        assertEquals(java.util.Optional.ofNullable(user1.getId()), 1);
        assertEquals(user1.getFirstName(), "Stan");
        assertEquals(user1.getSurname(), "Castan");

        String[] strings2 = {"1", "2", "3"};
        FriendshipParser friendshipParser = new FriendshipParser();
        Friendship friendship1 = friendshipParser.parse(strings2);
        assertEquals(java.util.Optional.ofNullable(friendship1.getId()), 1);
        assertEquals(friendship1.getOne(), 2);
        assertEquals(friendship1.getTwo(), 3);

        String[] gresit1 = {"1", "2"}, gresit2 = {"3", "4"};
        assertNull(userParser.parse(gresit1));
        assertNull(friendshipParser.parse(gresit2));
    }

    /**
     * teste pentru nivelul de servicii al aplicatiei
     */
    @Test
    public void testMergeService() throws SQLException {
        TypeParser userTypeParser = new UserParser();
        Repository userRepository = new FileUserRepository("userTest.txt", userTypeParser);
        TypeParser friendshipTypeParser = new FriendshipParser();
        Repository friendshipRepository = new FileFriendshipRepository("friendshipTest.txt", friendshipTypeParser);
        MergedService service = new MergedService(userRepository, friendshipRepository);
        assertEquals(service.numberOfFriendships(), 0);
        assertEquals(service.numberOfUsers(), 0);

        try {
            service.addUser("Stan", "Castan");
            service.addUser("Ion", "Camion");
            service.addUser("Simga", "Male");
        } catch (ValidateException | RepoException validateException) {
            fail();
        }

        assertEquals(service.numberOfUsers(), 3);

        try {
            service.addUser("", "");
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Prenume invalid!\nNume invalid!\n");
        } catch (RepoException repoException) {
            fail();
        }

        assertEquals(service.findUser(1).toString(), "1;Stan;Castan\n");

        try {
            service.addFriendship(1, 2);
            service.addFriendship(1, 3);
            service.addFriendship(2, 3);
        } catch (ValidateException | RepoException validateException) {
            fail();
        }

        try {
            service.updateFriendship(1, 4, 5);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Pereche id invalida\n");
        } catch (RepoException repoException) {
            fail();
        }

        try {
            service.updateFriendship(1,1,3);
            fail();
         } catch (RepoException repoException) {
           assertEquals(repoException.getMessage(),"Prietenie deja stabilita\n");
        } catch (ValidateException validateException) {
            fail();
        }

        try {
            service.addFriendship(2, 1);
        } catch (ValidateException ignored) {
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element existent\n");
        }

        try {
            service.addFriendship(1, 1);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Pereche id invalida\n");
        } catch (RepoException ignored) {
        }

        assertEquals(service.numberOfFriendships(), 3);

        Iterable<User> userIterable = service.getUsers();
        int counter = 1;
        for (User user : userIterable) {
            assertEquals(java.util.Optional.ofNullable(user.getId()), counter++);
        }

        Iterable<Friendship> friendshipIterable = service.getFriendships();
        counter = 1;
        for (Friendship friendship : friendshipIterable) {
            assertEquals(java.util.Optional.ofNullable(friendship.getId()), counter++);
        }

        assertEquals(service.findFriendship(1).toString(), "1;1;2\n");

        try {
            service.addUser("Shrigma", "Male");
        } catch (ValidateException | RepoException ignored) {
        }

        try {
            service.updateFriendship(3, 3, 4);
        } catch (ValidateException | RepoException ignored) {
        }

        try {
            service.updateFriendship(3, 1, 1);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Utilizatorii au id identic!\n");
        } catch (RepoException ignored) {
        }

        try {
            service.updateUser(3, "Beta", "Male");
        } catch (ValidateException | RepoException ignored) {
        }

        try {
            service.deleteUser(1);
        } catch (RepoException ignored) {
        }

        try {
            service.deleteFriendship(5);
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        assertEquals(service.numberOfFriendships(), 1);
        assertEquals(service.numberOfUsers(), 3);

        try {
            service.deleteUser(2);
            service.addUser("Man", "Castan");
            service.addUser("Simion", "Camion");
        } catch (ValidateException | RepoException ignored) {
        }

        assertEquals(service.findUser(1).toString(), "1;Man;Castan\n");
        assertEquals(service.findUser(2).toString(), "2;Simion;Camion\n");

        try {
            service.deleteUser(2);
            service.addUser("Berli", "Boss");
        } catch (ValidateException | RepoException ignored) {
        }

        assertEquals(service.findUser(2).toString(), "2;Berli;Boss\n");
        File usersFile = new File("userTest.txt");
        File friendshipsFile = new File("friendshipTest.txt");
        usersFile.deleteOnExit();
        friendshipsFile.deleteOnExit();
    }


    /**
     * teste pentru algoritmii de grafuri neorientate neponderate implemenentati in cadrul aplicatiei
     */
    @Test
    public void testMergeGraphs() throws SQLException {
        MergedService service;
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("usersGraph.txt"));
            bufferedWriter.write("""
                    1;a;a
                    2;b;b
                    3;c;c
                    4;d;d
                    5;e;e
                    6;f;f
                    7;g;g
                    8;h;h
                    9;i;i
                    10;j;j
                    """);
            bufferedWriter.close();
            bufferedWriter = new BufferedWriter(new FileWriter("friendshipsGraph.txt"));
            bufferedWriter.write("""
                    1;1;2
                    2;2;3
                    3;3;4
                    4;4;5
                    5;5;1
                    6;6;7
                    7;7;8
                    8;9;6
                    9;9;10
                    """);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TypeParser userTypeParser = new UserParser();
        Repository userRepository = new FileUserRepository("usersGraph.txt", userTypeParser);
        TypeParser friendshipTypeParser = new FriendshipParser();
        Repository friendshipRepository = new FileFriendshipRepository("friendshipsGraph.txt", friendshipTypeParser);
        service = new MergedService(userRepository, friendshipRepository);
        assertEquals(service.numberOfCommunities(), 2);
        String[] checks = {"1;a;a\n", "2;b;b\n", "3;c;c\n", "4;d;d\n", "5;e;e\n"};
        int counter = 0;
        for (User user : service.mostSocial()) {
            assertEquals(user.toString(), checks[counter++]);
        }
        File usersFile = new File("usersGraph.txt");
        File friendshipsFile = new File("friendshipsGraph.txt");
        usersFile.deleteOnExit();
        friendshipsFile.deleteOnExit();
    }

    @Test
    public void testGraphs() throws SQLException {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("usersGraph.txt"));
            bufferedWriter.write("""
                    1;a;a
                    2;b;b
                    3;c;c
                    4;d;d
                    5;e;e
                    6;f;f
                    7;g;g
                    8;h;h
                    9;i;i
                    10;j;j
                    """);
            bufferedWriter.close();
            bufferedWriter = new BufferedWriter(new FileWriter("friendshipsGraph.txt"));
            bufferedWriter.write("""
                    1;1;2
                    2;2;3
                    3;3;4
                    4;4;5
                    5;5;1
                    6;6;7
                    7;7;8
                    8;9;6
                    9;9;10
                    """);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Validator userValidator = new Validator(new UserValidator());
        TypeParser<Integer, User> userTypeParser = new UserParser();
        Repository userRepository = new FileUserRepository("usersGraph.txt", userTypeParser);
        Service userService = new UserService(userRepository, userValidator);

        Validator friendshipValidator = new Validator(new FriendshipValidator());
        TypeParser friendshipTypeParser = new FriendshipParser();
        Repository friendshipRepository = new FileFriendshipRepository("friendshipsGraph.txt", friendshipTypeParser);
        Service friednshipService = new FriendshipService(friendshipRepository, friendshipValidator);

        Controller controller = new Controller(userService, friednshipService);
        assertEquals(controller.numberOfCommunities(), 2);
        String[] checks = {"1;a;a\n", "2;b;b\n", "3;c;c\n", "4;d;d\n", "5;e;e\n"};
        int counter = 0;
        for (User user : controller.mostSocial()) {
            assertEquals(user.toString(), checks[counter++]);
        }
        File usersFile = new File("usersGraph.txt");
        File friendshipsFile = new File("friendshipsGraph.txt");
        usersFile.deleteOnExit();
        friendshipsFile.deleteOnExit();
    }

    @Test
    public void testController() throws SQLException {
        Validator userValidator = new Validator(new UserValidator());
        TypeParser<Integer, User> userTypeParser = new UserParser();
        Repository userRepository = new FileUserRepository("userControllerTest.txt", userTypeParser);
        Service userService = new UserService(userRepository, userValidator);


        Validator friendshipValidator = new Validator(new FriendshipValidator());
        TypeParser friendshipTypeParser = new FriendshipParser();
        Repository friendshipRepository = new FileFriendshipRepository("friendshipControllerTest.txt", friendshipTypeParser);
        Service friednshipService = new FriendshipService(friendshipRepository, friendshipValidator);

        Controller controller = new Controller(userService, friednshipService);
        assertEquals(controller.numberOfFriendships(), 0);
        assertEquals(controller.numberOfUsers(), 0);

        try {
            controller.addUser("Stan", "Castan");
            controller.addUser("Ion", "Camion");
            controller.addUser("Simga", "Male");
        } catch (ValidateException | RepoException | BusinessException validateException) {
            fail();
        }

        assertEquals(controller.numberOfUsers(), 3);

        try {
            controller.addUser("", "");
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Prenume invalid!\nNume invalid!\n");
        } catch (RepoException | BusinessException repoXbusinessException) {
            fail();
        }

        assertEquals(controller.findUser(1).toString(), "1;Stan;Castan\n");

        try {
            controller.addFriendship(1, 2);
            controller.addFriendship(1, 3);
            controller.addFriendship(2, 3);
        } catch (ValidateException | RepoException | BusinessException validateException) {
            fail();
        }

        try {
            controller.updateFriendship(1, 4, 5);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Pereche id invalida\n");
        } catch (RepoException | BusinessException repoException) {
            fail();
        }

        try {
            controller.updateFriendship(1,1,3);
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(),"Prietenie deja stabilita\n");
        } catch (ValidateException | BusinessException validateException) {
            fail();
        }

        try {
            controller.addFriendship(2, 1);
        } catch (ValidateException | BusinessException ignored) {
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element existent\n");
        }

        try {
            controller.addFriendship(1, 1);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Pereche id invalida\n");
        } catch (RepoException | BusinessException ignored) {
        }

        assertEquals(controller.numberOfFriendships(), 3);

        Iterable<User> userIterable = controller.getUsers();
        int counter = 1;
        for (User user : userIterable) {
            assertEquals(java.util.Optional.ofNullable(user.getId()), counter++);
        }

        Iterable<Friendship> friendshipIterable = controller.getFriendships();
        counter = 1;
        for (Friendship friendship : friendshipIterable) {
            assertEquals(java.util.Optional.ofNullable(friendship.getId()), counter++);
        }

        assertEquals(controller.findFriendship(1).toString(), "1;1;2\n");

        try {
            controller.addUser("Shrigma", "Male");
        } catch (ValidateException | RepoException | BusinessException ignored) {
        }

        try {
            controller.updateFriendship(3, 3, 4);
        } catch (ValidateException | RepoException | BusinessException ignored) {
        }

        try {
            controller.updateFriendship(3, 1, 1);
        } catch (ValidateException validateException) {
            assertEquals(validateException.getMessage(), "Utilizatorii au id identic!\n");
        } catch (RepoException | BusinessException ignored) {
        }

        try {
            controller.updateUser(3, "Beta", "Male");
        } catch (ValidateException | RepoException | BusinessException ignored) {
        }

        try {
            controller.deleteUser(1);
        } catch (RepoException ignored) {
        }

        try {
            controller.deleteFriendship(5);
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        assertEquals(controller.numberOfFriendships(), 1);
        assertEquals(controller.numberOfUsers(), 3);

        try {
            controller.deleteUser(2);
            controller.addUser("Man", "Castan");
            controller.addUser("Simion", "Camion");
        } catch (ValidateException | RepoException | BusinessException ignored) {
        }

        assertEquals(controller.findUser(1).toString(), "1;Man;Castan\n");
        assertEquals(controller.findUser(2).toString(), "2;Simion;Camion\n");

        try {
            controller.deleteUser(2);
            controller.addUser("Berli", "Boss");
        } catch (ValidateException | RepoException | BusinessException ignored) {
        }

        assertEquals(controller.findUser(2).toString(), "2;Berli;Boss\n");
        File usersFile = new File("userControllerTest.txt");
        File friendshipsFile = new File("friendshipControllerTest.txt");
        usersFile.deleteOnExit();
        friendshipsFile.deleteOnExit();

        Repository<Integer, User> databaseUserRepository;
        databaseUserRepository = new DatabaseUserRepository("jdbc:postgresql://localhost:5432/test", "postgres", "alpha109");
        assertEquals(databaseUserRepository.size(), 0);
        try {
            databaseUserRepository.add(new User(1, "Sigma", "Male"));
            databaseUserRepository.add(new User(2, "Alpha", "Male"));
            databaseUserRepository.add(new User(3, "In dev", "Male"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.size(), 3);
        assertEquals(databaseUserRepository.find(1), new User(1, "Castan", "Stan"));
        Iterable<User> iterable2 = databaseUserRepository.getAll();
        counter = 1;
        for (User user : iterable2)
            assertEquals(java.util.Optional.ofNullable(user.getId()), counter++);
        try {
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(1).getId()), 1);
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.size(), 2);
        assertNull(databaseUserRepository.find(7));

        try {
            databaseUserRepository.update(2, new User(2, "Shrigma", "Male"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.find(2).toString(), "2;Shrigma;Male\n");

        try {
            databaseUserRepository.delete(7);
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            databaseUserRepository.update(1, new User(1, "Beta", "Male"));
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(2).getId()), 2);
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(3).getId()), 3);
        } catch (RepoException repoException) {
            fail();
        }

    }

    @Test
    public void testDatabaseRepo() throws SQLException {
        Repository<Integer, User> databaseUserRepository;
        databaseUserRepository = new DatabaseUserRepository("jdbc:postgresql://localhost:5432/test", "postgres", "alpha109");
        assertEquals(databaseUserRepository.size(), 0);
        try {
            databaseUserRepository.add(new User(1, "Sigma", "Male"));
            databaseUserRepository.add(new User(2, "Alpha", "Male"));
            databaseUserRepository.add(new User(3, "In dev", "Male"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.size(), 3);
        assertEquals(databaseUserRepository.find(1), new User(1, "Castan", "Stan"));
        Iterable<User> iterable2 = databaseUserRepository.getAll();
        int counter = 1;
        for (User user : iterable2)
            assertEquals(java.util.Optional.ofNullable(user.getId()), counter++);
        try {
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(1).getId()), 1);
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.size(), 2);
        assertNull(databaseUserRepository.find(7));

        try {
            databaseUserRepository.update(2, new User(2, "Shrigma", "Male"));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseUserRepository.find(2).toString(), "2;Shrigma;Male\n");

        try {
            databaseUserRepository.delete(7);
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            databaseUserRepository.update(1, new User(1, "Beta", "Male"));
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(2).getId()), 2);
            assertEquals(java.util.Optional.ofNullable(databaseUserRepository.delete(3).getId()), 3);
        } catch (RepoException repoException) {
            fail();
        }

        Repository<Integer, Friendship> databaseFriendshipRepository;
        databaseFriendshipRepository = new DatabaseFriendshipRepository("jdbc:postgresql://localhost:5432/test", "postgres", "alpha109");
        assertEquals(databaseFriendshipRepository.size(), 0);
        try {
            databaseFriendshipRepository.add(new Friendship(1, 1, 2));
            databaseFriendshipRepository.add(new Friendship(2, 2, 3));
            databaseFriendshipRepository.add(new Friendship(3, 3, 4));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseFriendshipRepository.size(), 3);
        assertEquals(databaseFriendshipRepository.find(1), new Friendship(1, 1, 2));
        Iterable<Friendship> iterable1 = databaseFriendshipRepository.getAll();
        counter = 1;
        for (Friendship friendship : iterable1)
            assertEquals(java.util.Optional.ofNullable(friendship.getId()), counter++);
        try {
            assertEquals(java.util.Optional.ofNullable(databaseFriendshipRepository.delete(1).getId()), 1);
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseFriendshipRepository.size(), 2);
        assertNull(databaseFriendshipRepository.find(7));

        try {
            databaseFriendshipRepository.update(2, new Friendship(2, 4, 7));
        } catch (RepoException repoException) {
            fail();
        }
        assertEquals(databaseFriendshipRepository.find(2).toString(), "2;4;7\n");

        try {
            databaseFriendshipRepository.delete(7);
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            databaseFriendshipRepository.update(1, new Friendship(1, 2, 7));
            fail();
        } catch (RepoException repoException) {
            assertEquals(repoException.getMessage(), "Element inexistent\n");
        }

        try {
            assertEquals(java.util.Optional.ofNullable(databaseFriendshipRepository.delete(2).getId()), 2);
            assertEquals(java.util.Optional.ofNullable(databaseFriendshipRepository.delete(3).getId()), 3);
        } catch (RepoException repoException) {
            fail();
        }
    }
}
