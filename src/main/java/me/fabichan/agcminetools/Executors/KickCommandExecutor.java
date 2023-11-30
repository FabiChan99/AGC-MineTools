package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.MessageConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KickCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public KickCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.noPermissions")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.commandUsage")));
        String noPlayer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.playerNotFound")));
        String kickMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.kickMessage")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.playerMessage")));
        String kickReason = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.kickReason")));
        String kickTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.kickTitle")));
        String noticeMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("kick.noticeReason")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));

        if (!sender.hasPermission("agcminetools.kick")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(chatprefix + noPlayer);
            return true;
        }

        String reason = String.join(" ", args).substring(args[0].length()).trim();
        String finalReason = "\n" + kickTitle + "\n\n" + kickMessage + "\n\n" + kickReason + ChatColor.DARK_RED + reason + "\n\n" + noticeMessage;

        target.kickPlayer(finalReason);
        sender.sendMessage(chatprefix + playerMessage);

        return true;
    }
}