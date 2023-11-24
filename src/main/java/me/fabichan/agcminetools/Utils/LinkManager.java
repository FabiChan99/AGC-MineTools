package me.fabichan.agcminetools.Utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

import static me.fabichan.agcminetools.Utils.DbUtil.*;

public class LinkManager {
    private static DbUtil dbclient;
    private static JavaPlugin BukkitPlugin;

    public LinkManager(JavaPlugin plugin) {
        dbclient = DbUtil.getInstance(plugin);
        BukkitPlugin = plugin;
    }

    private static void deleteExpiredCodes(UUID minecraftUuid) {
        try (Connection conn = DbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM linkcodes WHERE uuid = ? AND expires_at <= CURRENT_TIMESTAMP")) {

            pstmt.setString(1, minecraftUuid.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            
        }
    }

    public static boolean isPending(String linkCode) {
        return checkIfExists("SELECT * FROM linkcodes WHERE linkcode = ? AND expires_at > CURRENT_TIMESTAMP", linkCode);
    }

    public static UUID GetPendingUser(String linkCode) {
        if (linkCode == null) {
            return null;
        }

        Object result = executeQuery("SELECT uuid FROM linkcodes WHERE linkcode = ? AND expires_at > CURRENT_TIMESTAMP", "uuid", linkCode);
        return getUuid(result);
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
            BukkitPlugin.getLogger().severe("Fehler beim Überprüfen, ob ein Link-Code existiert!");
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
        executeUpdate("INSERT INTO mcusers (uuid, userid, linked_at) VALUES (?, ?, CURRENT_TIMESTAMP)", minecraftUuid.toString(), discordId);
        executeUpdate("DELETE FROM linkcodes WHERE uuid = ?", minecraftUuid.toString());
    }
    
    public static boolean isLinkCodeValid(String linkCode) {
        return checkIfExists("SELECT * FROM linkcodes WHERE linkcode = ?", linkCode);
    }

    public static void unlinkAccounts(long discordId) {
        executeUpdate("DELETE FROM mcusers WHERE userid = ?", discordId);
    }

    public static void unlinkAccounts(UUID minecraftUuid) {
        executeUpdate("DELETE FROM mcusers WHERE uuid = ?", minecraftUuid.toString());
    }

    public static UUID getMinecraftUuid(long discordId) {
        Object object = executeQuery("SELECT uuid FROM mcusers WHERE userid = ?", "uuid", discordId);
        return getUuid(object);
    }

    private static UUID getUuid(Object object) {
        if (object == null) {
            return null;
        }
        String dbEntry = object.toString();
        if (dbEntry.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(dbEntry);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static long getDiscordId(UUID minecraftUuid) {
        return (long) executeQuery("SELECT userid FROM mcusers WHERE uuid = ?", "userid", minecraftUuid.toString());
    }

    public static String getLinkCode(String minecraftUuid) {
        return (String) executeQuery("SELECT linkcode FROM linkcodes WHERE uuid = ?", "linkcode", minecraftUuid);
    }
    
    public static String GetUUIDByLinkCode(String linkCode) {
        return (String) executeQuery("SELECT uuid FROM linkcodes WHERE linkcode = ?", "uuid", linkCode);
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
    
    public static void LinkAndInvalidateCode(long discordId, UUID minecraftUuid, String linkCode) {
        linkAccounts(discordId, minecraftUuid);
        executeUpdate("DELETE FROM linkcodes WHERE linkcode = ?", linkCode);
    }



}
