package me.fabichan.agcminetools.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class LinkManager {
    static DatabaseClient dbclient;
    static JavaPlugin JavaPlugin;
    
    public LinkManager(JavaPlugin plugin) {
        JavaPlugin = plugin;
        dbclient = new DatabaseClient(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password")
        );
    }
    public String generateLinkCode() {

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
    
    
    public boolean isLinked(long discordId) {
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
    
    public boolean isLinked(UUID minecraftUuid) {
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
    
    public void LinkAccounts(long discordId, UUID minecraftUuid) {
        try {
            if (dbclient.getConnection() != null) {
                dbclient.getConnection().createStatement().executeUpdate("INSERT INTO 'mcusers' ('uuid', 'userid') VALUES ('" + minecraftUuid + "', '" + discordId + "')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void UnlinkAccounts(long discordId){
        
    }
    
    public void UnlinkAccounts(UUID minecraftUuid){
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
    
    public long getDiscordId(UUID minecraftUuid) {
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
    
}
