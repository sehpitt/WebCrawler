package linkscraper;

import java.sql.*;

public class Database {

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private final String USERNAME,
            PASSWORD,
            URL;

    public Database(String URL, String userName, String password) {
        this.URL = URL;
        this.USERNAME = userName;
        this.PASSWORD = password;
    }

    public void databaseConnect(String connect) {

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(connect, USERNAME, PASSWORD);
            statement = connection.createStatement();

            insertEmails();

        } catch (ClassNotFoundException | SQLException e) {

        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException ex) {

            }
        }
    }

    private void insertEmails() throws SQLException {
        for (Object emailAddress : LinkScraper.emailBufferList) {
            String email = (String) emailAddress;

            String insertQuery = String.format("INSERT INTO Emails VALUES('%s')", email);

            statement.executeUpdate(insertQuery);
        }
    }
}
