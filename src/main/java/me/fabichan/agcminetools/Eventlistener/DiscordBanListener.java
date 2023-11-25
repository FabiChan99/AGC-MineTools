package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Utils.DbUtil;
import me.fabichan.agcminetools.Utils.LinkManager;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;


public class DiscordBanListener extends ListenerAdapter {

    static DbUtil dbclient;
    private final JavaPlugin plugin;

    public DiscordBanListener(JavaPlugin plugin) throws SQLException {
        this.plugin = plugin;
        dbclient = DbUtil.getInstance(plugin);

    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        long discordId = event.getUser().getIdLong();
        UUID minecraftUuid = LinkManager.getMinecraftUuid(discordId);
        if (minecraftUuid != null) {
            // check if player is online
            if (plugin.getServer().getPlayer(minecraftUuid) != null) {
                Objects.requireNonNull(plugin.getServer().getPlayer(minecraftUuid)).kickPlayer("Du wurdest von unserem Discord-Server gebannt! Es gibt keine Möglichkeit ohne Server-Mitgliedschaft auf dem Minecraft-Server zu spielen.");
            }
        }
    }

}
