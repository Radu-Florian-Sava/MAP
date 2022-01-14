package Repo;

import Domain.Message;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseMessageRepoPaged extends DatabaseMessageRepository {

    private int nrOfRows = 4;
    /**
     * constructor
     *
     * @param url      the url of the database
     * @param username the username of the database
     * @param password the password of the database
     */
    public DatabaseMessageRepoPaged(String url, String username, String password) {
        super(url, username, password);
    }

    public List<Message> getAllForAUser(int page, int id_user_1, int id_user_2) throws SQLException {
        List<Message> messages = new ArrayList<>();
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
                        "INNER JOIN messages M ON M.id = U.id_message " +
                        "WHERE (U.id_from = " + id_user_1 + " AND U.id_to = " + id_user_2 + ") OR " +
                        "(U.id_from = " + id_user_2 + " AND U.id_to = " + id_user_1 + ") " +
                        "LIMIT " + nrOfRows + " OFFSET " + nrOfRows * page);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

            Integer id_reply = resultSet.getInt("id_reply");

            if(id_reply == 0)
                id_reply = null;

            messages.add(new Message(
                    resultSet.getInt("id"),
                    resultSet.getInt("id_user_from"),
                    resultSet.getInt("id_user_to"),
                    resultSet.getString("message"),
                    resultSet.getTimestamp("date"),
                    id_reply
            ));
        }
        return messages;
    }

    public int nrOfPages(int id_user_1, int id_user_2) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) as COUNT " +
                        "FROM messages_users U " +
                        "INNER JOIN messages M ON M.id = U.id_message " +
                        "WHERE (U.id_from = " + id_user_1 + " AND U.id_to = " + id_user_2 + ") OR " +
                        "(U.id_from = " + id_user_2 + " AND U.id_to = " + id_user_1 + ")");
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int ret = resultSet.getInt("COUNT");
        return  (ret % nrOfRows == 0)  ? ret / nrOfRows : ret / nrOfRows + 1;

    }

    public void setNrOfRows(int nrOfRows) {
        this.nrOfRows = nrOfRows;
    }
}
