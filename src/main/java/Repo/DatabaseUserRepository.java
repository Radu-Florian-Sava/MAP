package Repo;

import Domain.User;
import Exceptions.RepoException;

import java.sql.*;
import java.util.ArrayList;

/**
 *  specialised repository which contains users in a database
 */
public class DatabaseUserRepository implements Repository<Integer, User> {

    private final String url;
    private final String username;
    private final String password;

    /**
     * constructor
     * @param url the url of the database
     * @param username the username of the database
     * @param password the password of the database
     */
    public DatabaseUserRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * @param user the user that we want to add
     * @return the ID of the added user
     * @throws RepoException if the user with this ID already exists
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Integer add(User user) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO users (first_name, last_name, username, password) VALUES " +
                        "('" + user.getFirstName() +
                        "','" + user.getSurname() +
                        "','" + user.getUsername() +
                        "','" + user.getPassword() + "') RETURNING id");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("id");
    }

    /**
     * @param id of the element which will be deleted
     * @return the deleted user
     * @throws RepoException if the user with this ID doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public User delete(Integer id) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM users WHERE id =" + id + " RETURNING *");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next())
            throw new RepoException("Element inexistent\n");
        return new User(resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }

    /**
     * @param id   of the element which will be updated
     * @param user an user with the same ID which will replace the current user
     * @throws RepoException if the user with this ID doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public void update(Integer id, User user) throws RepoException, SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users " +
                "SET first_name='" + user.getFirstName() +
                "', last_name='" + user.getSurname() +
                "' WHERE id=" + id);
        if (preparedStatement.executeUpdate() == 0)
            throw new RepoException("Element inexistent\n");
    }

    /**
     * @param id of the element we are looking for
     * @return the element with the given ID or null if it doesn't exist
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public User find(Integer id) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE id = " + id);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (!resultSet.next())
            return null;
        return new User(resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );

    }


    /**
     * @return an iterable containing all the users
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public Iterable<User> getAll() throws SQLException {
        ArrayList<User> userArrayList = new ArrayList<>();
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            userArrayList.add(new User(resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("username"),
                    resultSet.getString("password")
            ));
        }
        return userArrayList;
    }

    /**
     * @return the number of users in the database
     * @throws SQLException if the database cannot be accessed
     */
    @Override
    public int size() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) as COUNT FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("COUNT");

    }

}
