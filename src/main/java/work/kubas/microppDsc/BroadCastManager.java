package work.kubas.microppDsc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import work.kubas.microppDsc.config.MessagesConfig;

import java.util.ArrayList;
import java.util.List;

public class BroadCastManager {

    private final Component helpMessage;

    private final List<Component> messages;

    private int messageIndex = 0;

    public BroadCastManager() {

        var messagesConfig = MicroppDsc.getInstance().getPluginConfig().getMessagesConfig();
        helpMessage = messagesConfig.getHelp();
        messages = messagesConfig.getCommunicates();

        MicroppDsc.getInstance().getServer().getScheduler().runTaskTimer(MicroppDsc.getInstance(), () -> {
            var message = messages.get(messageIndex);
            MicroppDsc.getInstance().getServer().broadcast(
                    message.append(Component.newline())
                            .append(helpMessage)
            );

            messageIndex = (messageIndex + 1) % messages.size();

        }, 0L, 20L * 60L * 30L);
    }
}
