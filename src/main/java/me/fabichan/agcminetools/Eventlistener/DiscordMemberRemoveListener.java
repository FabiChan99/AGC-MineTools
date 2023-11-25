package me.fabichan.agcminetools.Eventlistener;

import me.fabichan.agcminetools.Utils.DbUtil;
import me.fabichan.agcminetools.Utils.LinkManager;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.UUID;

public class DiscordMemberRemoveListener extends ListenerAdapter {

    static DbUtil dbclient;
    private final JavaPlugin plugin;

    public DiscordMemberRemoveListener(JavaPlugin plugin) {
        this.plugin = plugin;
        dbclient = DbUtil.getInstance(plugin);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        long discordId = event.getUser().getIdLong();
        Boolean linked = LinkManager.isLinked(discordId);
        if (linked) {
            UUID minecraftUuid = LinkManager.getMinecraftUuid(discordId);
            if (minecraftUuid != null) {
                // check if player is online
                if (plugin.getServer().getPlayer(minecraftUuid) != null) {
                    String kickmsg = "Du hast den Discord-Server verlassen. Um auf dem Minecraft-Server spielen zu können, musst du auf dem Discord-Server sein.";
                    plugin.getServer().getScheduler().runTask(plugin, () -> Objects.requireNonNull(plugin.getServer().getPlayer(minecraftUuid)).kickPlayer(kickmsg));
                }
            }
        }
    }

}
