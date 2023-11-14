package me.fabichan.agcminetools;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface ICommand  {
    String getName();
    String getDescription();

    void handle(SlashCommandInteractionEvent event);

    CommandData getCommandData();
    List<Permission> getRequiredPermissions();
    default boolean hasRequiredPermissions(SlashCommandInteractionEvent event) {
        if (getRequiredPermissions().isEmpty()) {
            return true;
        }
        return event.getMember().hasPermission(getRequiredPermissions());
    }
}
