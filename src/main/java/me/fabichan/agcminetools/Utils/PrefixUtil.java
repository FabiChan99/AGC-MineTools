package me.fabichan.agcminetools.Utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrefixUtil {
    
    private final Map<String, String> chatPrefixes = new HashMap<>();
    private final Map<String, String> tablistPrefixes = new HashMap<>();
    private final Map<String, String> chatColors = new HashMap<>();
    private final Map<String, String> permissions = new HashMap<>();

    public PrefixUtil(JavaPlugin plugin) {
        loadConfig(plugin.getConfig());
    }
    
    private void loadConfig(FileConfiguration config) {
        for (String key : config.getConfigurationSection("prefixes").getKeys(false)) {
            chatPrefixes.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("prefixes." + key + ".Chat")));
            tablistPrefixes.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("prefixes." + key + ".Tablist")));
            chatColors.put(key, config.getString("prefixes." + key + ".Chatcolor"));
            permissions.put(key, config.getString("prefixes." + key + ".Permission"));
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
