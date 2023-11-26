package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.JDAProvider;
import me.fabichan.agcminetools.Utils.LinkManager;
import me.fabichan.agcminetools.Utils.McUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Objects;
import java.util.UUID;

public class LookupCommandExecutor implements CommandExecutor {
    private final JavaPlugin plugin;
    private JDA jda;
    
    public LookupCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jda = JDAProvider.getJDA();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0){
            sender.sendMessage(ChatColor.RED + "Bitte gib einen Spieler an!");
            return true;
        }
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                String name = args[0];
                UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
                String discorduserid = LinkManager.getDiscordId(uuid);
                if (discorduserid == null) {
                    sender.sendMessage(ChatColor.RED + "Dieser Spieler ist nicht verknüpft!");
                    return;
                }
                User user = jda.retrieveUserById(discorduserid).complete();
                String discordname = user.getName();
                String lastlogin = McUtil.getLastOnline(uuid);
                String registerdate = LinkManager.getLinkDate(uuid);
                StringBuilder message = new StringBuilder();
                message.append(ChatColor.GOLD).append("Informationen über ").append(ChatColor.GREEN).append(name).append(ChatColor.GOLD).append(":").append("\n");
                message.append(ChatColor.GOLD).append("Discord Name: ").append(ChatColor.GREEN).append(discordname).append("\n");
                message.append(ChatColor.GOLD).append("Discord UserID: ").append(ChatColor.GREEN).append(user.getId()).append("\n");
                message.append(ChatColor.GOLD).append("Letzter Login: ").append(ChatColor.GREEN).append(lastlogin).append("\n");
                message.append(ChatColor.GOLD).append("Registriert seit: ").append(ChatColor.GREEN).append(registerdate).append("\n");
                Bukkit.getServer().getScheduler().runTask(plugin, () -> sender.sendMessage(message.toString()));
            }
        };
        runnable.runTaskAsynchronously(plugin);
        return true;
    }
}
