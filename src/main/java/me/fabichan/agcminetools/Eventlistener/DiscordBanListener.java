package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Utils.DatabaseClient;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBanListener {

    private final JavaPlugin plugin;

    public DiscordBanListener(JavaPlugin plugin) {
        this.plugin = plugin;
        new DatabaseClient(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password"));
        
    }
    
}
