package me.fabichan.agcminetools.Eventlistener;

import com.google.errorprone.annotations.Var;
import me.fabichan.agcminetools.Utils.DatabaseClient;
import me.fabichan.agcminetools.Utils.LinkManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;

import java.util.UUID;


public class DiscordBanListener extends ListenerAdapter {

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
    
    @Override
    public void onGuildBan(GuildBanEvent event) {
        long discordId = event.getUser().getIdLong();
        UUID minecraftUuid = LinkManager.getMinecraftUuid(discordId);
        if (minecraftUuid != null) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "kick " + minecraftUuid + " Du wurdest von unserem Discord-Server gebannt! Es gibt keine Möglichkeit ohne Server-Mitgliedschaft auf dem Minecraft-Server zu spielen.");
        }
    }
    
}
