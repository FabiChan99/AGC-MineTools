package me.fabichan.agcminetools.Utils;

import net.dv8tion.jda.api.JDA;

public class JDAProvider {
    private static JDA jda;

    public static void initialize(JDA jdaInstance) {
        jda = jdaInstance;
    }

    public static JDA getJDA() {
        return jda;
    }
}

