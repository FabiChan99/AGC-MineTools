package me.fabichan.agcminetools.Utils;

import me.fabichan.agcminetools.Main;


public class LinkCodeManager {
    private final Main plugin;
    private DatabaseClient dbclient;

    public LinkCodeManager(Main plugin) {
        this.plugin = plugin;
        DatabaseClient dbclient = new DatabaseClient(
                plugin.getConfig().getString("database.host"),
                plugin.getConfig().getString("database.port"),
                plugin.getConfig().getString("database.database"),
                plugin.getConfig().getString("database.username"),
                plugin.getConfig().getString("database.password")
        );
    }

    public String generate() {

        String code = "";
        for (int i = 0; i < 8; i++) {
            code += (int) (Math.random() * 10);
        }

        try {
            if (dbclient.getConnection() != null) {
                if (dbclient.getConnection().createStatement().executeQuery("SELECT * FROM `linkcodes` WHERE `linkcode` = '" + code + "'").first()) {
                    return generate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }
}
