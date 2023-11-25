package me.fabichan.agcminetools;

import me.fabichan.agcminetools.Commands.Discord.SendRegisterModal;
import me.fabichan.agcminetools.Eventlistener.*;
import me.fabichan.agcminetools.Utils.CommandManager;
import me.fabichan.agcminetools.Utils.DbUtil;
import me.fabichan.agcminetools.Utils.Interfaces.ICommand;
import me.fabichan.agcminetools.Utils.JDAProvider;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public final class Main extends JavaPlugin {
    public static JDA jda;

    public DbUtil dbclient;

    public Main() {
        dbclient = DbUtil.getInstance(this);
    }

    public static JDA getJDA() {
        return jda;
    }

    @Override
    public void onEnable() {
        getLogger().info("MineTools werden gestartet...");
        saveDefaultConfig();
        try {
            String botToken = getConfig().getString("bot.token");
            if (botToken == null || botToken.isEmpty() || botToken.equals("DISCORD_BOT_TOKEN")) {
                getLogger().severe("Bot-Token ist nicht gesetzt!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            jda = JDABuilder.createDefault(botToken)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.MESSAGE_CONTENT)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .build();
            jda.awaitReady();
            JDAProvider.initialize(jda);
            getLogger().info(String.format("Bot %s ist online!", jda.getSelfUser().getName()));
        } catch (Exception e) {
            getLogger().severe(String.format("Bot konnte nicht gestartet werden: %s", e.getMessage()));
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (dbclient == null) {
            getLogger().severe("Datenbankverbindung konnte nicht hergestellt werden!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        DbUtil.initDatabase();
        CommandManager commandManager = new CommandManager();
        jda.addEventListener(commandManager);
        try {
            jda.addEventListener(
                    new AccountLinkSubmitModalEvent(this),
                    new AccountLinkButtonClick(this),
                    new DiscordBanListener(this),
                    new DiscordMemberRemoveListener(this)
            );
        } catch (SQLException ignored) {
        }
        ICommand SendButtonCommand = new SendRegisterModal(this);
        commandManager.addCommand(SendButtonCommand);
        Guild guild = null;
        try {
            guild = jda.getGuildById(Objects.requireNonNull(getConfig().getString("bot.guildid")));
        } catch (Exception e) {
            //
        }
        if (guild == null) {
            getLogger().severe("Guild-ID ist nicht gesetzt oder der Bot ist nicht auf dem Server!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        for (ICommand command : commandManager.getCommands()) {
            getLogger().info(String.format("Slash-Command %s wird registriert!", command.getName()));
            guild.upsertCommand(command.getCommandData()).queue();
            getLogger().info(String.format("Slash-Command %s wurde registriert!", command.getName()));
        }
        registerMinecraftEvents();


    }

    private void registerMinecraftEvents() {
        getServer().getPluginManager().registerEvents(new MinecraftPlayerJoinListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("MineTools werden gestoppt...");
        // close database connection

        if (dbclient != null) {
            DbUtil.closeConnection();
        }

        if (jda != null) {
            jda.shutdown();
        }

    }
}
