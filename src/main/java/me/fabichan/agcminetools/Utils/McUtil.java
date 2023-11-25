package me.fabichan.agcminetools.Utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class McUtil {
    private static DbUtil dbclient;
    private static JavaPlugin BukkitPlugin;

    public McUtil(JavaPlugin plugin) {
        dbclient = DbUtil.getInstance(plugin);
        BukkitPlugin = plugin;
    }

    public static String getNameByUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
            return offlinePlayer.getName();
        } else {
            return null;
        }
    }
}
