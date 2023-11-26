package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.JDAProvider;
import me.fabichan.agcminetools.Utils.LinkManager;
import me.fabichan.agcminetools.Utils.McUtil;
import net.dv8tion.jda.api.JDA;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.dv8tion.jda.api.entities.User;
import java.util.UUID;

public class LookupCommandExecutor implements CommandExecutor {
    private final JavaPlugin plugin;
    private final JDA jda;

    public LookupCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jda = JDAProvider.getJDA();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Bitte gib einen Spieler an!");
            return true;
        }
        new BukkitRunnable() {
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

                TextComponent message = new TextComponent(ChatColor.DARK_AQUA + "════════════════════════\n" +
                        ChatColor.AQUA + " Profil von " + ChatColor.YELLOW + name + ChatColor.AQUA + " " + ChatColor.DARK_AQUA + "══════════\n" +
                        ChatColor.WHITE + "Discord Name: " + ChatColor.GRAY + discordname + "\n" +
                        ChatColor.WHITE + "Letzter Login: " + ChatColor.GRAY + lastlogin + "\n" +
                        ChatColor.WHITE + "Registriert seit: " + ChatColor.GRAY + registerdate + "\n");

                TextComponent userIDComponent = new TextComponent(ChatColor.WHITE + "Discord UserID: " + ChatColor.GRAY + "[Klicken zum Kopieren]");
                userIDComponent.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, user.getId()));
                message.addExtra(userIDComponent);

                Bukkit.getServer().getScheduler().runTask(plugin, () -> {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.spigot().sendMessage(message);
                    } else {
                        sender.sendMessage(message.toLegacyText());
                    }
                });
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
