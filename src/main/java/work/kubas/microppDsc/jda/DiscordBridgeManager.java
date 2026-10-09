package work.kubas.microppDsc.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import work.kubas.microppDsc.MicroppDsc;
import work.kubas.microppDsc.NoDiscordChannelException;
import work.kubas.microppDsc.storage.LinksStore;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.logging.Logger;

public class DiscordBridgeManager {

    private final JDA jda;
    private final long channelId;
    private final LinksStore linksStore;
    private final Logger log;
    private final boolean isDisabled;

    private static final Component attachmentMsg = Component.empty()
            .append(Component.text("[Załącznik] ", NamedTextColor.GRAY));

    public DiscordBridgeManager() {
        jda = MicroppDsc.getInstance().getJda();
        channelId = MicroppDsc.getInstance().getConfig().getLong("discord.channel-id");
        linksStore = MicroppDsc.getInstance().getLinksStore();
        log = MicroppDsc.getInstance().getLogger();
        isDisabled = false;
    }

    public DiscordBridgeManager(boolean isDisabled) {
        if (isDisabled) {
            jda = null;
            channelId = 0;
            linksStore = null;
            this.isDisabled = true;
        }
        else {
            jda = MicroppDsc.getInstance().getJda();
            channelId = MicroppDsc.getInstance().getConfig().getLong("discord.channel-id");
            linksStore = MicroppDsc.getInstance().getLinksStore();
            this.isDisabled = false;
        }
        log =  MicroppDsc.getInstance().getLogger();
    }

    public void sendToDsc(String message) {
        if (isDisabled) return;
        var channel = jda.getTextChannelById(channelId);

        if (channel == null) {
            log.warning("Discord channel " + channelId + " not found.");
            throw new NoDiscordChannelException(channelId);
        }

        channel.sendMessage(message)
                .setAllowedMentions(EnumSet.noneOf(Message.MentionType.class))
                .queue();
    }

    public void sendMcToDsc(Player player, String message) {
        if (isDisabled) return;
        try {
            var discordId = linksStore.getDiscordId(player.getUniqueId());

            Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
                if (discordId.isEmpty()) {
                    sendToDsc("***" + player.getName() + "*** `>` " + message);
                }
                else {
                    sendToDsc("<@" + discordId.get() + "> `>` " + message);
                }

            });

        } catch (SQLException e) {
            log.severe("Cannot send message to Discord:");
            log.severe(e.getMessage());
        }
    }

    public void sendDscToMc(String discordAuthor, String message, List<Message.Attachment> attachments) {
        if (isDisabled) return;
        Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
            Component mcMessage = Component.empty()
                    .append(Component.text("[", NamedTextColor.GRAY))
                    .append(Component.text("FF", NamedTextColor.GOLD))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(Component.text(discordAuthor, TextColor.fromHexString("#979c9f")))
                    .append(Component.text(" > ", NamedTextColor.GRAY));

            if (!attachments.isEmpty()) {
                mcMessage = mcMessage.append(attachmentMsg);
            }

            mcMessage = mcMessage.append(Component.text(message, NamedTextColor.WHITE));


            Bukkit.broadcast(mcMessage);
        });
    }


    public void achievementToDsc(Player player, String achievementTitle) {
        if (isDisabled) return;
        try {
            var discordId = linksStore.getDiscordId(player.getUniqueId());

            String playerName;

            if (discordId.isEmpty()) {
                playerName = "***" + player.getName() + "***";
            } else {
                playerName = "<@" + discordId.get() + ">";
            }

            sendToDsc("<:alekdr_sojak:1429971277527126238> " + playerName + " zdobył osiągnięcie **" + achievementTitle + "**!");

        } catch (SQLException e) {
            log.severe("Cannot send message to Discord:");
            log.severe(e.getMessage());
        }
    }

    public void sync(Player player, Long discordId) {
        if (isDisabled) {
            player.sendMessage(Component.text("Integracja z discord wyłączona!", NamedTextColor.RED));
            return;
        };

        var dscServer = MicroppDsc.getInstance().getJda().getGuildById(
                MicroppDsc.getInstance().getConfig().getLong("discord.server-id")
        );

        if (dscServer == null) {
            player.sendMessage(Component.text("Wystąpił błąd. Skontaktuj się z administracją.", NamedTextColor.RED));
            MicroppDsc.getInstance().getLogger().severe("Could not find the provided server.");
            return;
        }

        var member = dscServer.retrieveMemberById(discordId).complete();
        MicroppDsc.getInstance().getRoleSyncEngine().syncPlayer(player.getUniqueId(), member);

        MicroppDsc.getInstance().getColorManager().updateNameColor(player);

        if (player.isOnline()) {
            player.sendMessage(Component.text("")
                    .append(Component.text("Twoje id Discord to ", NamedTextColor.GRAY))
                    .append(Component.text(discordId, NamedTextColor.GREEN))
                    .append(Component.text(".", NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text("Twoje role zostały zsynchronizowane z serwerem ", NamedTextColor.GRAY))
                    .append(Component.text("FF", NamedTextColor.GOLD))
                    .append(Component.text(".", NamedTextColor.GRAY))
            );
        }
    }
}
