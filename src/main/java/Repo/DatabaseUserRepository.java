package Repo;

import Domain.User;
import Exceptions.RepoException;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseUserRepository implements Repository<Integer, User> {

    private final String url;
    private final String username;
    private final String password;

    public DatabaseUserRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

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

    @Override
    public int size() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) as COUNT FROM users");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("COUNT");

    }

}
