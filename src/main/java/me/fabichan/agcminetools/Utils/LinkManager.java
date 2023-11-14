package me.fabichan.agcminetools.Utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class LinkManager {
    DatabaseClient dbclient;
    JavaPlugin plugin;
    
    public LinkManager(JavaPlugin plugin) {
        this.plugin = plugin;
        dbclient = new DatabaseClient(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password")
        );
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
    
    
}
