package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Executors.KillCommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerDeathListener implements Listener {

    private final JavaPlugin plugin;
    private final KillCommandExecutor killCommandExecutor;

    public PlayerDeathListener(JavaPlugin plugin, KillCommandExecutor killCommandExecutor) {
        this.plugin = plugin;
        this.killCommandExecutor = killCommandExecutor;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (killCommandExecutor.wasKilledByCommand(event.getEntity().getName())) {
            String customDeathMessage = "Custom death message for " + event.getEntity().getName();  // Customize this message
            event.setDeathMessage(customDeathMessage);
        }
    }
}