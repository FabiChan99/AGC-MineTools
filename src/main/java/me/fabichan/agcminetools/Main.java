package me.fabichan.agcminetools;

import me.fabichan.agcminetools.Utils.DatabaseClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public JDA jda;

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
            jda = JDABuilder.createDefault(botToken).build();
            jda.awaitReady();
            getLogger().info(String.format("Bot %s ist online!", jda.getSelfUser().getName()));
        } catch (Exception e) {
            getLogger().severe(String.format("Bot konnte nicht gestartet werden: %s", e.getMessage()));
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        DatabaseClient dbclient = new DatabaseClient(getConfig().getString("database.host"), getConfig().getString("database.port"), getConfig().getString("database.name"), getConfig().getString("database.user"), getConfig().getString("database.password"));
        if (dbclient.getConnection() == null) {
            getLogger().severe("Datenbankverbindung konnte nicht hergestellt werden!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        dbclient.initDatabase();
        CommandManager commandManager = new CommandManager();
        jda.addEventListener(commandManager);
        for (ICommand command : commandManager.getCommands()) {
            getLogger().info(String.format("Slash-Command %s wird registriert!", command.getName()));
            jda.upsertCommand(command.getCommandData()).queue();
            getLogger().info(String.format("Slash-Command %s wurde registriert!", command.getName()));
        }


    }


    @Override
    public void onDisable() {
        getLogger().info("MineTools werden gestoppt...");
        if (jda != null) {
            jda.shutdown();
        }
    }
}
