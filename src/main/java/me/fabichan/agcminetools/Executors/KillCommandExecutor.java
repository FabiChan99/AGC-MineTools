package me.fabichan.agcminetools.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class KillCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;
    private final Set<String> killedPlayers = new HashSet<>();

    public KillCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.noPermissions")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("settings.chatprefix")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("kill.commandUsage")));
        String playerNotFound = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("messages.playerNotFound")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("kill.playerMessage")));

        if (!sender.hasPermission("agcminetools.kill")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(chatprefix + playerNotFound);
            return true;
        }

        killedPlayers.add(target.getName());
        target.setHealth(0.0);
        sender.sendMessage(chatprefix + playerMessage);
        return true;
    }

    public boolean wasKilledByCommand(String playerName) {
        return killedPlayers.remove(playerName);
    }
}