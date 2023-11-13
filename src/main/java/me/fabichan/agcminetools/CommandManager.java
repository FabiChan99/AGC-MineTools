package me.fabichan.agcminetools;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.*;

public class CommandManager extends ListenerAdapter {
    private final Map<String, ICommand> commands = new HashMap<>();

    public Set<ICommand> getCommands() {
        return Collections.unmodifiableSet(new HashSet<>(commands.values()));
    }

    private void addCommand(ICommand command) {
        commands.put(command.getName(), command);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();

        if (commands.containsKey(name)) {
            commands.get(name).handle(event);
        }
    }
}
