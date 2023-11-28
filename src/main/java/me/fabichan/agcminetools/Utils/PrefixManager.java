package me.fabichan.agcminetools.Utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

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
        }, 0L, 400L);
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
        for (String group : prefixUtil.getGroupNames()) {
            if (player.hasPermission(prefixUtil.getPermission(group))) {
                Team team = getTeam(scoreboard, group);
                team.addPlayer(player);
                player.setDisplayName(team.getPrefix() + player.getName());
                return;
            }
        }
        Team defaultTeam = getTeam(scoreboard, "default");
        defaultTeam.addPlayer(player);
        player.setDisplayName(defaultTeam.getPrefix() + player.getName());
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

        for (String group : prefixUtil.getGroupNames()) {
            if (player.hasPermission(prefixUtil.getPermission(group))) {
                prefix = prefixUtil.getChatPrefix(group);
                chatColor = prefixUtil.getChatColor(group);
                break;
            }
        }

        if (prefix.isEmpty()) {
            prefix = prefixUtil.getChatPrefix("default");
            chatColor = prefixUtil.getChatColor("default");
        }

        String format = prefix + player.getName() + " " + chatColor + event.getMessage();
        event.setFormat(format);
    }



}
