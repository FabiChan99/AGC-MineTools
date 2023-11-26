package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.McUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LookupCommandTabCompletor implements TabCompleter{

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("lookup")) {
            List<String> benutzerNamen = new ArrayList<>();
            List<OfflinePlayer> players = McUtil.getAllPlayersEverPlayedAsOfflinePlayer();
            for (OfflinePlayer player : players) {
                benutzerNamen.add(player.getName());
            }
            return benutzerNamen;
        }
        return null;
    }

}
