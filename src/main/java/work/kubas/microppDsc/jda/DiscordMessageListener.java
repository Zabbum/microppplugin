package work.kubas.microppDsc.jda;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import work.kubas.microppDsc.MicroppDsc;

import java.sql.SQLException;

@Slf4j
public class DiscordMessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        if (event.getChannelType() == ChannelType.PRIVATE)
            handlePrivateMessage(event);
        else if (event.getChannel().getIdLong() == MicroppDsc.getInstance().getConfig().getLong("discord.channel-id"))
            handleChatMessage(event);
    }

    private void handleChatMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentDisplay();
        var attachments = event.getMessage().getAttachments();

        MicroppDsc.getInstance().getDiscordBridgeManager().sendDscToMc(event.getAuthor().getEffectiveName(), content, attachments);
    }

    private void handlePrivateMessage(MessageReceivedEvent event) {

        String content = event.getMessage().getContentRaw().trim();

        // Quick sanity check before touching the code map: expect 6 digits
        if (!content.matches("\\d{6}")) {
            event.getChannel().sendMessage("Niepoprawny kod.").queue();
            return;
        }

        var uuid = MicroppDsc.getInstance().getLinkCodesManager().match(content);

        if (uuid == null) {
            event.getChannel().sendMessage("Niepoprawny kod.").queue();
            return;
        }

        var discordId = event.getAuthor().getIdLong();
        try {
            MicroppDsc.getInstance().getLinksStore().link(uuid, discordId);
        } catch (SQLException e) {
            MicroppDsc.getInstance().getLogger().severe("Failed to link: " + e.getMessage());
            event.getChannel().sendMessage("Coś poszło nie tak. Spróbuj jeszcze raz lub skontaktuj się z administracją.").queue();
        }

        event.getChannel().sendMessage("Twoje konto Minecraft zostało połączone z twoim kontem Discord!").queue();

        log.info("Code valid.");

        Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
            log.info("Sending message.");
            var player = Bukkit.getPlayer(uuid);
//            if (player != null && player.isOnline()) {
//                player.sendMessage(
//                        Component.text("Twoje konto Minecraft zostało połączone z kontem Discord " + event.getAuthor().getName() + "!")
//                                .color(NamedTextColor.GREEN)
//                );
//            }
            MicroppDsc.getInstance().getDiscordBridgeManager().sync(player, discordId);
        });
    }
}
