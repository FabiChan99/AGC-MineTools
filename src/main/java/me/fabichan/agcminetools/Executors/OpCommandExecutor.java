package me.fabichan.agcminetools.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class OpCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public OpCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("settings.chatprefix"));
        String consoleOnly = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("op.consoleOnly"));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("op.commandUsage"));
        String noPlayer = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("op.noPlayerFound"));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("op.playerMessage"));

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

        target.setOp(true);
        sender.sendMessage(chatprefix + playerMessage);
        return true;
    }
}
