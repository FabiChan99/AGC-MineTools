package me.fabichan.agcminetools.Utils;

import net.dv8tion.jda.api.JDA;

public class ResolveJDA {
    private static JDA _jda;
    
    public ResolveJDA(JDA jda) {
            _jda = jda;
        }
    
    public static JDA getJDA() {
        return _jda;
    }
}
