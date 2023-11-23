package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Utils.JDAProvider;
import me.fabichan.agcminetools.Utils.LinkManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MinecraftPlayerJoinListener implements Listener {

    private final JavaPlugin plugin;
    private final JDA jda;

    public MinecraftPlayerJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jda = JDAProvider.getJDA();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Überprüfung, ob jda null ist
                if (jda == null) {
                    plugin.getLogger().severe("JDA ist nicht initialisiert!");
                    return;
                }

                Player player = event.getPlayer();
                UUID playerUuid = player.getUniqueId();
                if (!LinkManager.isLinked(playerUuid)) {
                    String linkCode = LinkManager.generateLinkCode(playerUuid);
                    Guild guild = jda.getGuildById(plugin.getConfig().getString("bot.guildid"));
                    if (guild == null) {
                        plugin.getLogger().severe("Guild-ID ist nicht gesetzt oder der Bot ist nicht auf dem Server!");
                        return;
                    }

                    Channel linkChannel = guild.getTextChannelById(plugin.getConfig().getString("bot.registerchannelid"));
                    if (linkChannel == null) {
                        plugin.getLogger().severe("LinkChannel ist nicht gesetzt oder existiert nicht auf dem Server!");
                        return;
                    }

                    String linkChannelName = linkChannel.getName();
                    String kickMessage = "Bitte verbinde deinen Discord-Account mit dem Minecraft-Account!\n\nKlicke dazu in " + linkChannelName + " den Button \"Verlinken\" und gebe dort den Code " + linkCode + "` ein. Der Code ist 10 Minuten ab der Erstellung gültig.";

                    // Verschiebe das Kicken des Spielers auf den Hauptthread
                    Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(kickMessage));
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
