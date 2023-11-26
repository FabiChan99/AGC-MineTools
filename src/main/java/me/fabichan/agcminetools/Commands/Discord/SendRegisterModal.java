package me.fabichan.agcminetools.Commands.Discord;

import me.fabichan.agcminetools.Utils.Interfaces.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;

public class SendRegisterModal implements ICommand {

    private final JavaPlugin plugin;

    public SendRegisterModal(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "sendregistermodal";
    }

    @Override
    public String getDescription() {
        return "Sende den Registrierungs-Modal in den aktuellen Channel";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!hasRequiredPermissions(event)) {
            event.reply("You do not have the required permissions to use this command.").setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        EmbedBuilder embed = new EmbedBuilder();
        String registerEmbedMessage = plugin.getConfig().getString("messages.registerembedmessage");
        embed.setTitle("Minecraft Registrierung");
        embed.setDescription(registerEmbedMessage);
        embed.setColor(0x00ff00);

        Button button = Button.primary("mcregister", "Registrieren");
        event.getHook().sendMessageEmbeds(embed.build()).addActionRow(button).queue();
    }

    @Override
    public List < Permission > getRequiredPermissions() {
        return Collections.singletonList(Permission.ADMINISTRATOR);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getRequiredPermissions()));
    }

}