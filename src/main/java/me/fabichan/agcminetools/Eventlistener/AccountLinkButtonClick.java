package me.fabichan.agcminetools.Eventlistener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.plugin.java.JavaPlugin;

public class AccountLinkButtonClick extends ListenerAdapter {
    
        private final JavaPlugin plugin;
    
        public AccountLinkButtonClick(JavaPlugin plugin) {
            this.plugin = plugin;
        }
        
        @Override
        public void onButtonInteraction(ButtonInteractionEvent event) {
            if (event.getComponentId().equals("mcregister")) {
                TextInput input = TextInput.create("linkcode", "Link-Code eingeben", TextInputStyle.SHORT)
                        .setRequiredRange(8, 8)
                        .build();

                Modal modal = Modal.create("meinModalId", "Zahl eingeben")
                        .addActionRow(input)
                        .build();
                event.replyModal(modal).queue();
            }
        }
        
        
    
}
