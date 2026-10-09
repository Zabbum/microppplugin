package work.kubas.microppDsc.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import work.kubas.microppDsc.MicroppDsc;

public class ChatListener implements Listener {

    private final LuckPerms luckPerms;

    public ChatListener(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {

        User user = luckPerms.getPlayerAdapter(Player.class).getUser(event.getPlayer());
        String prefixStr = user.getCachedData().getMetaData().getPrefix();
        final Component prefix;
        if (prefixStr != null) {
            prefix = MiniMessage.miniMessage().deserialize(prefixStr);
        } else {
            prefix = Component.text("Player");
        }

        String plainMessage = LegacyComponentSerializer.legacySection().serialize(event.message());

        event.renderer((
                (source, sourceDisplayName, message, viewer) ->
                        Component.text()
                                .append(Component.text("[", NamedTextColor.GRAY))
                                .append(prefix)
                                .append(Component.text("] ", NamedTextColor.GRAY))
                                .append(sourceDisplayName)
                                .append(Component.text(" > ", NamedTextColor.GRAY))
                                .append(message)
                                .build()
        ));

        MicroppDsc.getInstance().getDiscordBridgeManager().sendMcToDsc(event.getPlayer(), plainMessage);
    }
}
