package me.fabichan.agcminetools.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PrefixManager implements Listener {
    private final JavaPlugin plugin;
    private final PrefixUtil prefixUtil;

    public PrefixManager(JavaPlugin plugin, PrefixUtil prefixcfg) {
        this.plugin = plugin;
        this.prefixUtil = prefixcfg;
        PrefixUpdateTask();
    }

    public void PrefixUpdateTask() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboardForPlayer(player);
            }
        }, 0L, 200L);
    }

    private void updateScoreboardForPlayer(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        Scoreboard scoreboard = manager.getNewScoreboard();

        registerTeams(scoreboard);

        setPlayerInTeam(player, scoreboard);

        player.setScoreboard(scoreboard);
    }

    private void registerTeams(Scoreboard scoreboard) {
        for (String group : prefixUtil.getGroupNames()) {
            registerTeam(scoreboard, group, prefixUtil.getTablistPrefix(group));
        }
        registerTeam(scoreboard, "default", prefixUtil.getTablistPrefix("default"));
    }

    private void registerTeam(Scoreboard scoreboard, String name, String prefix) {
        Team team = scoreboard.registerNewTeam(name);
        team.setPrefix(prefix);
    }

    private void setPlayerInTeam(Player player, Scoreboard scoreboard) {
        boolean inTeam = false;
        List<String> reversedGroups = new ArrayList<>(prefixUtil.getGroupNames());
        Collections.reverse(reversedGroups);

        for (String group : reversedGroups) {
            if (player.hasPermission(prefixUtil.getPermission(group))) {
                Team team = getTeam(scoreboard, group);
                if (team != null) {
                    team.addPlayer(player);
                    String prefix = prefixUtil.getTablistPrefix(group);
                    if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                        prefix = PlaceholderAPI.setPlaceholders(player, prefixUtil.getTablistPrefix(group));
                    }
                    team.setPrefix(prefix);
                    player.setPlayerListName(prefix);
                    inTeam = true;
                    break;
                }
            }
        }

        if (!inTeam) {
            Team defaultTeam = getOrCreateTeam(scoreboard, "default");
            defaultTeam.addPlayer(player);
            String defaultPrefix = prefixUtil.getTablistPrefix("default");
            if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
                defaultPrefix = PlaceholderAPI.setPlaceholders(player, prefixUtil.getTablistPrefix("default"));
            }
            defaultTeam.setPrefix(defaultPrefix);
            player.setPlayerListName(defaultPrefix + player.getName());
        }
    }


    private Team getOrCreateTeam(Scoreboard scoreboard, String name) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
            team.setPrefix(prefixUtil.getTablistPrefix(name));
        }
        return team;
    }

    private Team getTeam(Scoreboard scoreboard, String name) {
        return scoreboard.getTeam(name);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateScoreboardForPlayer(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String prefix = "";
        String chatColor = "";
        boolean permissionFound = false;

        List<String> reversedGroups = new ArrayList<>(prefixUtil.getGroupNames());
        Collections.reverse(reversedGroups);

        for (String group : reversedGroups) {
            if (player.hasPermission(prefixUtil.getPermission(group))) {
                prefix = prefixUtil.getChatPrefix(group);
                chatColor = prefixUtil.getChatColor(group);
                permissionFound = true;
                break;
            }
        }
        if (!permissionFound) {
            return;
        }
        prefix = ChatColor.translateAlternateColorCodes('&', prefix);
        chatColor = ChatColor.translateAlternateColorCodes('&', chatColor);
        String format = prefix + chatColor + event.getMessage();
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            format = PlaceholderAPI.setPlaceholders(event.getPlayer(), format);
        }
        format = format.replaceAll("%", "%%");
        event.setFormat(format);
    }
}
