package work.kubas.microppDsc.config;

import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessagesConfig {
    private Component help;
    private List<Component> communicates = new ArrayList<>();

    public MessagesConfig(FileConfiguration config) {
        var miniMessage = MiniMessage.miniMessage();

        help = miniMessage.deserialize(config.getString("messages.help"));

        var communicatesSection = config.getStringList("messages.communicates");
        for (var comm : communicatesSection) {
            communicates.add(miniMessage.deserialize(comm));
        }
    }
}
