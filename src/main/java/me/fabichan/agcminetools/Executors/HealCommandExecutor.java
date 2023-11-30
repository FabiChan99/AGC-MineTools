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

public class HealCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public HealCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("general.chatprefix"))));
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("error.noPermissions"))));
        String playerMessageSelf = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("heal.playerMessageSelf"))));
        String playerNotFound = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("error.playerNotFound"))));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("heal.playerMessage"))));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage(("heal.commandUsage"))));
        if (!sender.hasPermission("agcminetools.heal")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.sendMessage(chatprefix + playerMessageSelf);
            return true;
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(chatprefix + playerNotFound);
                return true;
            }
            target.setHealth(target.getMaxHealth());
            target.setFoodLevel(20);
            sender.sendMessage(chatprefix + playerMessage);
            return true;
        } else {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }
    }
}