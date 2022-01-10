package Testing;

import Domain.Friendship;
import Domain.Message;
import Domain.User;
import Exceptions.RepoException;
import Repo.DatabaseFriendshipRepository;
import Repo.DatabaseMessageRepository;
import Repo.DatabaseUserRepository;
import Repo.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

@Deprecated
public class TestsMesaje {
    @Test
    public void repo_test() throws SQLException, RepoException {
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
        assertEquals(message.getId_reply(), null);
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
}
