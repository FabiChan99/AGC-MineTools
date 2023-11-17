package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Utils.LinkManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import me.fabichan.agcminetools.Utils.ResolveJDA;

import java.util.UUID;

public class MinecraftPlayerJoinListener implements Listener {
    
    private final JavaPlugin plugin;
    private final JDA jda;
    
    public MinecraftPlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jda = ResolveJDA.getJDA();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (!LinkManager.isLinked(player.getUniqueId())) {
                    String linkCode = LinkManager.generateLinkCode();
                    JDA jda = ResolveJDA.getJDA();
                    Guild guild = jda.getGuildById(plugin.getConfig().getString("bot.guildid"));
                    if (guild == null) {
                        plugin.getLogger().severe("Guild-ID ist nicht gesetzt oder der Bot ist nicht auf dem Server!");
                        return;
                    }
                    Channel linkChannel = guild.getTextChannelById(plugin.getConfig().getString("bot.registerchannelid"));
                    assert linkChannel != null;
                    String LinkChannelName = linkChannel.getName();
                    player.kickPlayer("Bitte verbinde deinen Discord-Account mit dem Minecraft-Account!\n\nKlicke dazu in " + LinkChannelName + " den Button \"Verlinken\" und gebe dort den Code " + linkCode + "` ein. Der Code ist 10 Minuten ab der erstellung gültig.");
                }
            }
        }.runTaskAsynchronously(plugin);
    }    
}
