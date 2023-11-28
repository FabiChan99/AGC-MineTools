package me.fabichan.agcminetools.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeopCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public DeopCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("settings.chatprefix")));
        String consoleOnly = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("deop.consoleOnly")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("deop.commandUsage")));
        String noPlayer = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("deop.noPlayerFound")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(plugin.getConfig().getString("deop.playerMessage")));

        if (sender instanceof Player) {
            sender.sendMessage(chatprefix + consoleOnly);
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(chatprefix + noPlayer);
            return true;
        }

        target.setOp(false);
        sender.sendMessage(chatprefix + playerMessage);
        return true;
    }
}
