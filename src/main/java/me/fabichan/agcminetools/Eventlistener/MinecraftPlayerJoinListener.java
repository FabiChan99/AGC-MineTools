package me.fabichan.agcminetools.Eventlistener;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MinecraftPlayerJoinListener implements Listener {
    
    private final JavaPlugin plugin;
    
    public MinecraftPlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getPlayer().sendMessage("Test");
            }
        }.runTaskAsynchronously(plugin);
    }    
}
