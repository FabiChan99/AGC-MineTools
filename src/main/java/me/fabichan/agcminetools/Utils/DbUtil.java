package me.fabichan.agcminetools.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class DbUtil {

    private static DbUtil instance;
    private static Connection connection;
    private final JavaPlugin javaPlugin;

    private DbUtil(JavaPlugin plugin) {
        javaPlugin = plugin;
        connectToDatabase();
    }

    public static synchronized DbUtil getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new DbUtil(plugin);
        }
        return instance;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance.connectToDatabase();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void initDatabase() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS mcusers (uuid VARCHAR(36) NOT NULL, userid BIGINT NOT NULL, linked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (uuid))");
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS linkcodes (" +
                            "uuid VARCHAR(36) NOT NULL, " +
                            "linkcode VARCHAR(36) NOT NULL, " +
                            "expires_at TIMESTAMP NOT NULL DEFAULT (NOW() + INTERVAL '10 minutes'), " +
                            "PRIMARY KEY (uuid))"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void executeUpdate(String query, Object... params) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            setParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Object executeQuery(String query, String columnName, Object... params) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.first()) {
                    return rs.getObject(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    private void connectToDatabase() {
        try {
            // import driver postgresql
            Class.forName("org.postgresql.Driver");

            String username = javaPlugin.getConfig().getString("database.username");
            String password = javaPlugin.getConfig().getString("database.password");
            String host = javaPlugin.getConfig().getString("database.host");
            String port = javaPlugin.getConfig().getString("database.port");
            String database = javaPlugin.getConfig().getString("database.database");
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
