package me.fabichan.agcminetools.Executors;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UnbanCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public UnbanCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("settings.chatprefix")));
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.noPermissions")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("unban.commandUsage")));
        String playerNotBanned = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("unban.playerNotBanned")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("unban.playerMessage")));

        if (!sender.hasPermission("agcminetools.unban")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }

        String target = args[0];

        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(target)) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(target);
            sender.sendMessage(chatprefix + playerMessage);
        } else {
            sender.sendMessage(chatprefix + playerNotBanned);
        }
        return true;
    }
}