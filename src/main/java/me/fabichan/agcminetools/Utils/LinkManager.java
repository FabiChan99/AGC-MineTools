package me.fabichan.agcminetools.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

public class LinkManager {
    private static DbUtil dbclient;

    public LinkManager(JavaPlugin plugin) {
        dbclient = DbUtil.getInstance(plugin);
        System.out.println("LinkManager wurde initialisiert!");
    }

    private static void deleteExpiredCodes(UUID minecraftUuid) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM linkcodes WHERE uuid = ? AND expires_at <= CURRENT_TIMESTAMP")) {

            pstmt.setString(1, minecraftUuid.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String generateLinkCode(UUID minecraftUuid) {
        // Überprüfe zuerst, ob ein gültiger Code existiert
        String existingCode = checkForExistingCode(minecraftUuid);
        if (existingCode != null) {
            return existingCode;
        }

        deleteExpiredCodes(minecraftUuid);

        String code = "";
        for (int i = 0; i < 8; i++) {
            code += (int) (Math.random() * 10);
        }

        long expiresAt = System.currentTimeMillis() + 600000; // 600000 Millisekunden = 10 Minuten
        executeUpdate("INSERT INTO linkcodes (uuid, linkcode, expires_at) VALUES (?, ?, ?)", minecraftUuid.toString(), code, new Timestamp(expiresAt));

        return code;
    }


    private static String checkForExistingCode(UUID minecraftUuid) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT linkcode FROM linkcodes WHERE uuid = ? AND expires_at > CURRENT_TIMESTAMP")) {

            pstmt.setString(1, minecraftUuid.toString());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("linkcode");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean isLinked(long discordId) {
        return checkIfExists("SELECT * FROM mcusers WHERE userid = ?", discordId);
    }

    public static boolean isLinked(UUID minecraftUuid) {
        return checkIfExists("SELECT * FROM mcusers WHERE uuid = ?", minecraftUuid.toString());
    }

    public static void linkAccounts(long discordId, UUID minecraftUuid) {
        executeUpdate("INSERT INTO mcusers (uuid, userid) VALUES (?, ?)", minecraftUuid.toString(), discordId);
    }

    public static void unlinkAccounts(long discordId) {
        executeUpdate("DELETE FROM mcusers WHERE userid = ?", discordId);
    }

    public static void unlinkAccounts(UUID minecraftUuid) {
        executeUpdate("DELETE FROM mcusers WHERE uuid = ?", minecraftUuid.toString());
    }

    public static UUID getMinecraftUuid(long discordId) {
        return (UUID) executeQuery("SELECT uuid FROM mcusers WHERE userid = ?", "uuid", discordId);
    }

    public static long getDiscordId(UUID minecraftUuid) {
        return (long) executeQuery("SELECT userid FROM mcusers WHERE uuid = ?", "userid", minecraftUuid.toString());
    }

    public static String getLinkCode(UUID minecraftUuid) {
        return (String) executeQuery("SELECT linkcode FROM linkcodes WHERE uuid = ?", "linkcode", minecraftUuid.toString());
    }

    private static boolean checkIfExists(String query, Object... params) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

            setParameters(pstmt, params);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.first();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void executeUpdate(String query, Object... params) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            setParameters(pstmt, params);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Object executeQuery(String query, String columnName, Object... params) {
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

    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

}
