package Repo;

import Domain.Event;
import Domain.Message;
import Exceptions.RepoException;
import Utils.StatusEventUser;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *  specialised repository which contains events created by users or only users
 *  who participate at the event in a database
 */
public class DatabaseEventRepository implements Repository<Integer, Event> {
    private final String url;
    private final String username;
    private final String password;

    /**
     * constructor
     * @param url the url of the database
     * @param username the username of the database
     * @param password the password of the database
     */
    public DatabaseEventRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * @param event the event we want to add
     * @throws SQLException if the database cannot be accessed
     * @return the ID of the added event
     */
    @Override
    public Integer add(Event event) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        if(event.getTitle() == null) {
            int user = event.getUsers().keySet().iterator().next();
            int status = event.getUsers().get(user).getKey() == StatusEventUser.PARTICIPANT ? 0 : 1;
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO events_users (id_event, id_user, status) VALUES " +
                            "(" +
                            event.getId() + ", " +
                            user + ", " +
                            status + ") RETURNING id");


            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
        else {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO events (title, description, date) VALUES " +
                            "('" +
                            event.getTitle() + "', '" +
                            event.getDescription() + "', '" +
                            event.getDate() + "') RETURNING id");


            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
    }

    /**
     * @param integer the id of the event we want to delete
     * @return the event deleted
     * @throws RepoException if the event with that id doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Event delete(Integer integer) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM events_users WHERE id = " + integer + " RETURNING *"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            throw new RepoException("Id invalid!\n");

        return new Event(
                resultSet.getInt("id_event"),
                null,
                null,
                null,
                resultSet.getInt("id_user"),
                resultSet.getInt("status") == 1 ? StatusEventUser.ORGANIZER : StatusEventUser.PARTICIPANT,
                resultSet.getInt("id")
        );

    }

    /**
     * @param integer the message's id we want to update
     * @param event the new event
     * @throws RepoException if the event with that id doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public void update(Integer integer, Event event) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE events " +
                        "SET " +
                        "title = '" + event.getTitle() + "', " +
                        "description = '" + event.getDescription() + "', " +
                        "date = " + event.getDate() + " " +
                        "WHERE id = " + integer);

        if (preparedStatement.executeUpdate() == 0)
            throw new RepoException("Element inexistent\n");
    }

    /**
     * @param integer the id of the event we want to find
     * @return the event with that id
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Event find(Integer integer) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT" +
                        "U.id as id," +
                        "E.title as title," +
                        "E.date as date," +
                        "E.description as description," +
                        "U.id_user as id_user," +
                        "U.status as status," +
                        "U.id_event as id_event " +
                        "FROM events_users U" +
                        "INNER JOIN events E ON E.id = U.id_event" +
                        " WHERE U.id = " + integer);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return null;

        return new Event(
                resultSet.getInt("id_event"),
                resultSet.getTimestamp("date"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                resultSet.getInt("id_user"),
                StatusEventUser.PARTICIPANT,
                resultSet.getInt("id")
        );
    }

    /**
     * @return an iterable containing all the events
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Iterable<Event> getAll() throws SQLException {
        ArrayList<Event> eventArrayList = new ArrayList<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        int old_id = -1;
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " +
                        "U.id as id, " +
                        "E.title as title, " +
                        "E.date as date, " +
                        "E.description as description, " +
                        "U.id_user as id_user, " +
                        "U.status as status, " +
                        "U.id_event as id_event " +
                        "FROM events_users U " +
                        "INNER JOIN events E ON E.id = U.id_event");
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next()) {
            old_id = resultSet.getInt("id_event");
            Event event = new Event(
                    resultSet.getInt("id_event"),
                    resultSet.getTimestamp("date"),
                    resultSet.getString("title"),
                    resultSet.getString("description"),
                    resultSet.getInt("id_user"),
                    resultSet.getInt("status") == 0
                            ? StatusEventUser.PARTICIPANT : StatusEventUser.ORGANIZER,
                    resultSet.getInt("id")
            );
            eventArrayList.add(event);
            while (resultSet.next()) {
                int id = resultSet.getInt("id_event");
                if (id == old_id) {
                    event.add(resultSet.getInt("id_user"), resultSet.getInt("status") == 0
                            ? StatusEventUser.PARTICIPANT : StatusEventUser.ORGANIZER, resultSet.getInt("id"));
                } else {
                    event = new Event(
                            resultSet.getInt("id_event"),
                            resultSet.getTimestamp("date"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getInt("id_user"),
                            resultSet.getInt("status") == 0
                                    ? StatusEventUser.ORGANIZER : StatusEventUser.PARTICIPANT,
                            resultSet.getInt("id")

                    );
                    eventArrayList.add(event);
                }
                old_id = id;
            }
        }
        return eventArrayList;
    }

    /**
     * @return the number of events in total
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public int size() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) as COUNT FROM events_users");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("COUNT");
    }
}
