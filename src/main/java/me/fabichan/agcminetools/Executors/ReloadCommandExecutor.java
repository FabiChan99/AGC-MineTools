package me.fabichan.agcminetools.Executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public ReloadCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("settings.noPermissions")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("settings.chatprefix")));
        String reloadMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("reload.reloadMessage")));

        if (!sender.hasPermission("agcminetools.reload") && !sender.hasPermission("agcminetools.owner")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(chatprefix + reloadMessage);
        return true;
    }
}