package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.MessageConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GameModeCommandExecutor implements CommandExecutor {

    private final JavaPlugin plugin;

    public GameModeCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.noPermissions")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("gamemode.commandUsage")));
        String playerNotFound = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.playerNotFound")));
        String invalidGameMode = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("gamemode.invalidGameMode")));
        String playerMessageSelf = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("gamemode.playerMessageSelf")));
        String playerMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("gamemode.playerMessage")));
        if (!sender.hasPermission("agcminetools.gamemode")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }

        GameMode gameMode;
        switch (args[0].toLowerCase()) {
            case "creative":
            case "c":
                gameMode = GameMode.CREATIVE;
                break;
            case "survival":
            case "s":
                gameMode = GameMode.SURVIVAL;
                break;
            case "adventure":
            case "a":
                gameMode = GameMode.ADVENTURE;
                break;
            case "spectator":
            case "sp":
                gameMode = GameMode.SPECTATOR;
                break;
            default:
                sender.sendMessage(chatprefix + invalidGameMode);
                return true;
        }

        if (args.length == 1 && sender instanceof Player) {
            Player player = (Player) sender;
            player.setGameMode(gameMode);
            player.sendMessage(chatprefix + playerMessageSelf + player.getGameMode());
            return true;
        } else if (args.length == 2) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(chatprefix + playerNotFound);
                return true;
            }
            target.setGameMode(gameMode);
            sender.sendMessage(chatprefix + playerMessage + target.getGameMode());
            return true;
        }

        return false;
    }
}