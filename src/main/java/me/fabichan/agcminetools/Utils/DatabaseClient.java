package me.fabichan.agcminetools.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseClient {
    private Connection connection;

    public DatabaseClient(String host, String port, String database, String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void initDatabase() {
        try {
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS mcusers (uuid VARCHAR(36) NOT NULL, userid BIGINT NOT NULL, PRIMARY KEY (uuid))");
            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS linkcodes (" +
                            "uuid VARCHAR(36) NOT NULL, " +
                            "linkcode VARCHAR(36) NOT NULL, " +
                            "expires_at TIMESTAMP NOT NULL DEFAULT (NOW() + INTERVAL '10 minutes'), " +
                            "PRIMARY KEY (uuid))"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
