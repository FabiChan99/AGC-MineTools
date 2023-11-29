package me.fabichan.agcminetools.Utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PrefixUtil {

    private final Map<String, String> chatPrefixes = new HashMap<>();
    private final Map<String, String> tablistPrefixes = new HashMap<>();
    private final Map<String, String> chatColors = new HashMap<>();
    private final Map<String, String> permissions = new HashMap<>();

    public PrefixUtil(JavaPlugin plugin) {
        CustomConfigManager configManager = new CustomConfigManager(plugin, "prefixes.yml");
        configManager.saveDefaultConfig();
        loadConfig(configManager.getConfig());
    }

    private void loadConfig(FileConfiguration config) {
        for (String key : config.getKeys(false)) {
            chatPrefixes.put(key, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(key + ".Chat"))));
            tablistPrefixes.put(key, ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(key + ".Tablist"))));
            chatColors.put(key, config.getString(key + ".Chatcolor"));
            permissions.put(key, config.getString(key + ".Permission"));
        }
    }


    public String getChatPrefix(String group) {
        return chatPrefixes.getOrDefault(group, "");
    }

    public String getTablistPrefix(String group) {
        return tablistPrefixes.getOrDefault(group, "");
    }

    public String getChatColor(String group) {
        return chatColors.getOrDefault(group, "");
    }

    public String getPermission(String group) {
        return permissions.getOrDefault(group, "");
    }

    public Set<String> getGroupNames() {
        return chatPrefixes.keySet();
    }


}
