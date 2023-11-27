package me.fabichan.agcminetools.Executors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BanCommandTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("ban") && args.length == 1) {
            String partOfName = args[0].toLowerCase();

            List<String> allPlayers = Stream.concat(
                    Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName),
                    Bukkit.getOnlinePlayers().stream().map(Player::getName)
            ).distinct().collect(Collectors.toList());

            suggestions = allPlayers.stream()
                    .filter(name -> name != null && name.toLowerCase().startsWith(partOfName))
                    .collect(Collectors.toList());
        }

        return suggestions;
    }
}