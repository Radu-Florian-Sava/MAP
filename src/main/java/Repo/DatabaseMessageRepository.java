package Repo;

import Domain.Message;
import Exceptions.RepoException;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseMessageRepository implements Repository<Integer, Message> {
    private final String url;
    private final String username;
    private final String password;

    /**
     * @param url the url of the database
     * @param username the username of the database
     * @param password the password of the database
     */
    public DatabaseMessageRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * @param message the message we want to add
     * @throws SQLException database error
     * @return
     */
    @Override
    public Integer add(Message message) throws RepoException, SQLException {
        Integer id_reply = message.getIdReply();
        Connection connection = DriverManager.getConnection(url, username, password);
        if(message.getMessage() == null) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO messages_users (id_from, id_to, id_message, id_reply) VALUES " +
                            "(" +
                            message.getFrom() + ", " +
                            message.getTo() + ", " +
                            message.getId() + ", " +
                            id_reply + ") RETURNING id");


            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
        else {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO messages (message, date) VALUES " +
                            "(" + "'" +
                            message.getMessage() + "','" +
                            message.getDate() + "') RETURNING id");


            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("id");
        }
    }

    /**
     * @param integer the id of the message we want to delete
     * @return the message deleted
     * @throws RepoException if the message with that id doesn't exist
     * @throws SQLException database error
     */
    @Override
    public Message delete(Integer integer) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM messages WHERE id = " + integer + "RETURNING *"
        );
        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            throw new RepoException("Id invalid!\n");

        return new Message(
                resultSet.getInt("id"),
                0,
                0,
                resultSet.getString("message"),
                resultSet.getTimestamp("date"),
                null
        );

    }

    /**
     * @param integer the message's id we want to update
     * @param message the new message
     * @throws RepoException if the message with that id doesn't exist
     * @throws SQLException database error
     */
    @Override
    public void update(Integer integer, Message message) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE messages " +
                        "SET " +
                        "message = '" + message.getMessage() + "', " +
                        "WHERE id = " + integer);

        if (preparedStatement.executeUpdate() == 0)
            throw new RepoException("Element inexistent\n");
    }

    /**
     * @param integer the id of the message we want to find
     * @return the message with that id
     * @throws SQLException database error
     */
    @Override
    public Message find(Integer integer) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM messages WHERE id = " + integer);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return null;

        return new Message(
                resultSet.getInt("id"),
                0,
                0,
                resultSet.getString("message"),
                resultSet.getTimestamp("date"),
                null
        );
    }

    /**
     * @return all messages
     * @throws SQLException database error
     */
    @Override
    public Iterable<Message> getAll() throws SQLException {
        ArrayList<Message> friendshipArrayList = new ArrayList<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT " +
                        "M.id as id, " +
                        "M.message as message, " +
                        "M.date as date, " +
                        "U.id_from as id_user_from, " +
                        "U.id_to as id_user_to, " +
                        "U.id_reply as id_reply " +
                        "FROM messages_users U " +
                        "INNER JOIN messages M ON M.id = U.id_message");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            Integer id_reply = resultSet.getInt("id_reply");

            if(id_reply == 0)
                id_reply = null;

            friendshipArrayList.add(new Message(
                    resultSet.getInt("id"),
                    resultSet.getInt("id_user_from"),
                    resultSet.getInt("id_user_to"),
                    resultSet.getString("message"),
                    resultSet.getTimestamp("date"),
                    id_reply
            ));
        }
        return friendshipArrayList;
    }

    /**
     * @return the number of messages in total
     * @throws SQLException database error
     */
    @Override
    public int size() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) as COUNT FROM messages_users");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("COUNT");
    }
}
