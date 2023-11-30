package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.MessageConfigManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class BanCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public BanCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.noPermissions")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.commandUsage")));
        String noPlayer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.playerNotFound")));
        String banMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.banMessage")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.playerMessage")));
        String banReason = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.banReason")));
        String noticeMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.noticeMessage")));
        String socialLink = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.socialLink")));
        String banTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("ban.banTitle")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));
        if (!sender.isOp() && !sender.hasPermission("agcminetools.ban")) {
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

        StringBuilder reasonBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            reasonBuilder.append(args[i]).append(" ");
        }
        String reason = reasonBuilder.toString().trim();
        String finalReason = "\n" + banTitle + "\n\n" + banMessage + "\n\n" + banReason + ChatColor.DARK_RED + reason + "\n\n" + noticeMessage + "\n" + socialLink;
        Bukkit.getBanList(BanList.Type.NAME).addBan(target.getName(), finalReason, null, null);

        target.kickPlayer(finalReason);
        sender.sendMessage(chatprefix + playerMessage);

        return true;
    }
}