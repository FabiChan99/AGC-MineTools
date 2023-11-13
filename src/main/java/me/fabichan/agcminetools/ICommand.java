package me.fabichan.agcminetools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public interface ICommand {
    String getName();

    void handle(SlashCommandInteractionEvent event);

    CommandData getCommandData();
}
