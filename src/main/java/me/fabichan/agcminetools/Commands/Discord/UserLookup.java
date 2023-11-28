package me.fabichan.agcminetools.Commands.Discord;

import me.fabichan.agcminetools.Utils.Interfaces.ICommand;
import me.fabichan.agcminetools.Utils.LinkManager;
import me.fabichan.agcminetools.Utils.McUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserLookup implements ICommand {

    private final JavaPlugin plugin;

    public UserLookup(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "userlookup";
    }

    @Override
    public String getDescription() {
        return "Lookup a user's Minecraft data";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        if (!hasRequiredPermissions(event)) {
            event.reply("Du darfst diesen Befehl nicht ausführen.").setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        User user = Objects.requireNonNull(event.getOption("user")).getAsUser();
        boolean isLinked = LinkManager.isLinked(user.getIdLong());
        if (!isLinked) {
            event.getHook().sendMessage("Dieser Nutzer scheint mit keinem Account verknüpft zu sein.").queue();
            return;
        }
        String mcuuid = LinkManager.getMinecraftUuid(user.getIdLong()).toString();
        UUID uuid = UUID.fromString(mcuuid);
        String minecraftName = McUtil.getNameByUUID(LinkManager.getMinecraftUuid(user.getIdLong())) == null ? "Unbekannt" : McUtil.getNameByUUID(LinkManager.getMinecraftUuid(user.getIdLong()));
        String registerDateFormatted = LinkManager.getLinkDate(uuid);
        boolean isOnline = plugin.getServer().getPlayer(uuid) != null;
        String LastOnline = McUtil.getLastOnline(uuid);
        String onlineStatus = isOnline ? "Online" : "Offline";

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Minecraft User Lookup");
        embed.setDescription("Hier sind die Daten des Users " + user.getAsMention() + " aufgelistet.");
        embed.setColor(0x1E90FF);
        embed.setThumbnail(user.getAvatarUrl());
        embed.setAuthor("Minecraft Data Lookup", null, "https://cdn.discordapp.com/icons/750365461945778209/f17d819976c04c9f08cf0485ba653c40?size=512");
        embed.addField("Minecraft UUID", "<:uuidbook:1178782369138884738> • `" + mcuuid + "`", false);
        embed.addField("Minecraft Name", "<:nametag:1178783957182070836> • `" + minecraftName + "`", false);
        embed.addField("Registrierungsdatum", "`" + registerDateFormatted + "`", false);
        embed.addField("Letzter Login", "`" + LastOnline + "`", false);
        embed.addField("Online Status", "`" + onlineStatus + "`", false);
        embed.setFooter("Requested by " + event.getUser().getName(), event.getUser().getAvatarUrl());
        embed.setTimestamp(Instant.now());
        event.getHook().sendMessageEmbeds(embed.build()).queue();
    }

    @Override
    public List<Permission> getRequiredPermissions() {
        return Collections.singletonList(Permission.KICK_MEMBERS);
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(getRequiredPermissions())).addOption(OptionType.USER, "user", "The user to lookup", true);
    }
}