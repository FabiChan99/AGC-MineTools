package me.fabichan.agcminetools.Utils;

import kotlin.reflect.TypeOfKt;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.Console;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.UUID;


public class LinkManager {
    static DatabaseClient dbclient;
    static JavaPlugin JavaPlugin;
    
    public LinkManager(JavaPlugin plugin) throws SQLException {
        JavaPlugin = plugin;
        dbclient = DatabaseClient.getInstance(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password")
        );
        // print dbclient
        System.out.println(dbclient);
        
        System.out.println("LinkManager wurde initialisiert!");
    }
    public static String generateLinkCode() {

        String code = "";
        for (int i = 0; i < 8; i++) {
            code += (int) (Math.random() * 10);
        }

        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM `linkcodes` WHERE `linkcode` = '" + code + "'").first()) {
                    return generateLinkCode();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
    
    
    public static boolean isLinked(long discordId) {
        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'userid' = '" + discordId + "'").first()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isLinked(UUID minecraftUuid) {
        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'uuid' = '" + minecraftUuid + "'").first()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void LinkAccounts(long discordId, UUID minecraftUuid) {
        try {
            if (dbclient.getConnection() != null) {
                dbclient.getConnection().createStatement().executeUpdate("INSERT INTO 'mcusers' ('uuid', 'userid') VALUES ('" + minecraftUuid + "', '" + discordId + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void UnlinkAccounts(long discordId){
        
    }
    
    public static void UnlinkAccounts(UUID minecraftUuid){
    }
    
    public static UUID getMinecraftUuid(long discordId) {
        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'userid' = '" + discordId + "'").first()) {
                    return UUID.fromString(dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'userid' = '" + discordId + "'").getString("uuid"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static long getDiscordId(UUID minecraftUuid) {
        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'uuid' = '" + minecraftUuid + "'").first()) {
                    return dbclient.getConnection().createStatement().executeQuery("SELECT * FROM 'mcusers' WHERE 'uuid' = '" + minecraftUuid + "'").getLong("userid");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public static String getLinkCode(UUID minecraftUuid) {
        try {
            if (dbclient.getConnection() != null) {
                Statement statement = dbclient.getConnection().createStatement();

                String query = "SELECT linkcode, expires_at FROM linkcodes WHERE uuid = '" + minecraftUuid.toString() + "'";
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.first()) {
                    String linkcode = resultSet.getString("linkcode");
                    Timestamp expiresAt = resultSet.getTimestamp("expires_at");
                    
                    long tenMinutesAgo = System.currentTimeMillis() - 600000; 
                    if (expiresAt.getTime() > tenMinutesAgo) {
                        return linkcode;
                    }
                }
                
                return generateLinkCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
