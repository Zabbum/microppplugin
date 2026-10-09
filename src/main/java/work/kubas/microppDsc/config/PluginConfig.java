package work.kubas.microppDsc.config;

import lombok.Getter;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PluginConfig {

    private final Map<Long, String> roleToGroup = new HashMap<>();
    private final Map<String, TextColor> groupToColor = new HashMap<>();
    @Getter
    private final MessagesConfig messagesConfig;

    public PluginConfig(FileConfiguration config) {
        messagesConfig = new MessagesConfig(config);

        var mappingSection = config.getConfigurationSection("role-mappings");
        if (mappingSection == null) return;

        for (String roleId : mappingSection.getKeys(false)) {
            String group = mappingSection.getString(roleId);
            if (group != null) {
                roleToGroup.put(Long.parseLong(roleId), group);
            }
        }


        var colorsSection = config.getConfigurationSection("role-colors");
        if (colorsSection == null) return;

        for (String group : colorsSection.getKeys(false)) {
            String color = colorsSection.getString(group);
            if (color != null) {
                groupToColor.put(group, TextColor.fromHexString(color));

            }
        }
    }

    public Optional<String> getGroupForRole(long roleId) {
        return Optional.ofNullable(roleToGroup.get(roleId));
    }

    public Optional<TextColor> getColorForGroup(String group) {
        return Optional.ofNullable(groupToColor.get(group));
    }

    public Map<Long, String> getAllMappings() {
        return roleToGroup;
    }

    public Map<String, TextColor> getAllColorMappings() {
        return groupToColor;
    }
}
