package me.fabichan.agcminetools.Executors;

import me.fabichan.agcminetools.Utils.JDAProvider;
import me.fabichan.agcminetools.Utils.LinkManager;
import me.fabichan.agcminetools.Utils.McUtil;
import me.fabichan.agcminetools.Utils.MessageConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LookupCommandExecutor implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final JDA jda;

    public LookupCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
        this.jda = JDAProvider.getJDA();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private static ItemStack createItem(Material material, String name, String lore, String discordId) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(name);
        List<String> lores = new ArrayList<>();
        lores.add(lore);
        if (!discordId.isEmpty()) {
            lores.add(ChatColor.GRAY + "ID: " + discordId);
        }
        meta.setLore(lores);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String noPermissions = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.noPermissions")));
        String commandUsage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.commandUsage")));
        String notLinked = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.notLinked")));
        String playerOnlyCommand = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("error.playerOnlyCommand")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));
        if (!sender.isOp() && !sender.hasPermission("agcminetools.team")) {
            sender.sendMessage(chatprefix + noPermissions);
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(chatprefix + commandUsage);
            return true;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                String name = args[0];
                UUID uuid = plugin.getServer().getOfflinePlayer(name).getUniqueId();
                OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(uuid);
                String discorduserid = LinkManager.getDiscordId(uuid);
                if (discorduserid == null) {
                    Bukkit.getScheduler().runTask(plugin, () -> sender.sendMessage(chatprefix + notLinked));
                    return;
                }
                User user = jda.retrieveUserById(discorduserid).complete();
                String discordname = user.getName();
                String lastLoginDate = reformatDateString(McUtil.getLastOnline(uuid));
                String registerDate = reformatDateString(LinkManager.getLinkDate(uuid));

                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (sender instanceof Player) {
                        openProfileGUI((Player) sender, discordname, lastLoginDate, registerDate, discorduserid, target);
                    } else {
                        sender.sendMessage(chatprefix + playerOnlyCommand);
                    }
                });
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }

    private void openProfileGUI(Player player, String discordName, String lastLogin, String registerDate, String discorduserid, OfflinePlayer target) {
        String inventoryTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.inventoryTitle")));
        String discordTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.discordTitle")));
        String lastLoginTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.lastLoginTitle")));
        String registeredTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.registeredTitle")));
        String discordIdTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.discordIdTitle")));
        String discordIdDesc = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.discordIdDesc")));
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));
        Inventory inv = Bukkit.createInventory(null, 27, chatprefix + inventoryTitle);

        ItemStack glassPane = createItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + "", "", "");
        for (int i = 0; i < inv.getSize(); i++) {
            if (i != 10 && i != 13 && i != 16) {
                inv.setItem(i, glassPane);
            }
        }

        ItemStack discordItem = createItem(Material.PAPER, discordTitle, ChatColor.GRAY + discordName, "");
        ItemStack lastLoginItem = createItem(Material.CLOCK, lastLoginTitle, ChatColor.GRAY + lastLogin, "");
        ItemStack registerDateItem = createItem(Material.BOOK, registeredTitle, ChatColor.GRAY + registerDate, "");
        ItemStack discordUserId = createItem(Material.MAP, discordIdTitle, discordIdDesc, discorduserid);

        if (target.isOnline() && player.hasPermission("agcminetools.admin") || player.hasPermission("agcminetools.lookup.ip")) {
            String ipAddressTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.ipAddressTitle")));
            String ipAddress = getPlayerIpAddress(target.getName());
            ItemStack ipAddressItem = createItem(Material.NAME_TAG, ipAddressTitle, ChatColor.GRAY + ipAddress, "");
            inv.setItem(25, ipAddressItem);
        } else {
            String ipAddressTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.ipAddressTitle")));
            String ipAddress = "Offline or Unknown";
            ItemStack ipAddressItem = createItem(Material.NAME_TAG, ipAddressTitle, ChatColor.GRAY + ipAddress, "");
            inv.setItem(25, ipAddressItem);
        }

        inv.setItem(10, discordItem);
        inv.setItem(13, lastLoginItem);
        inv.setItem(16, registerDateItem);
        inv.setItem(19, discordUserId);

        player.openInventory(inv);
    }

    private String getPlayerIpAddress(String playerName) {
        try {

            Player target = Bukkit.getServer().getPlayer(playerName);
            if (target != null) {
                return Objects.requireNonNull(Objects.requireNonNull(target.getAddress()).getAddress().getHostAddress());
            }
        } catch (Exception e) {
            return "Offline or Unknown";
        }
        return "Offline or Unknown";
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String chatprefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("general.chatprefix")));
        String copyIdChatMessage = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessageConfigManager.getMessage("lookup.copyIdChatMessage")));
        String copyIdChatHover = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull((MessageConfigManager.getMessage("lookup.copyIdChatHover"))));
        String inventoryTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull((MessageConfigManager.getMessage("lookup.inventoryTitle"))));
        if (event.getInventory().getHolder() == null && event.getView().getTitle().equals(chatprefix + inventoryTitle)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.MAP) {
                Player player = (Player) event.getWhoClicked();
                player.closeInventory();

                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (Objects.requireNonNull(meta).hasLore()) {
                    List<String> lore = meta.getLore();
                    String textToCopy = Objects.requireNonNull(lore).size() > 1 ? lore.get(1).substring(6) : "";

                    TextComponent lineAbove = new TextComponent(ChatColor.GRAY + "--------------------------------");
                    player.spigot().sendMessage(lineAbove);

                    TextComponent message = new TextComponent(chatprefix + copyIdChatMessage);
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, textToCopy));
                    message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(copyIdChatHover + textToCopy)));
                    player.spigot().sendMessage(message);

                    TextComponent lineBelow = new TextComponent(ChatColor.GRAY + "--------------------------------");
                    player.spigot().sendMessage(lineBelow);
                }
            }
        }
    }

    private String reformatDateString(String dateString) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            plugin.getLogger().severe(e.getMessage());
            return "Unknown";
        }
    }
}