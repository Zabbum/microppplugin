package com.github.zabbum.microppplugin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
public class PluginSettings {
    private final Logger log = Microppplugin.getInstance().getLog();
    @Getter
    private final static PluginSettings instance = new PluginSettings();
    private File configFile;
    private YamlConfiguration config;

    @Getter
    private Integer eventMessageInterval;
    @Getter
    private Integer maxDistanceFromCenter;
    @Getter
    private Integer day;
    @Getter
    private LocalTime eventStartTime;
    @Getter
    private LocalDate startDate;
    @Getter
    private String noEventMessage;
    private String noEventUntilMessage;
    @Getter
    private String docsMessage;
    @Getter
    private URI docsLink;
    @Getter
    private UUID horseUUID;
    @Getter
    private UUID bowHolderUUID;
    @Getter
    private UUID horseHolderUUID;
    @Getter
    private UUID villagerUUID;
    @Getter
    private UUID elytraHolderUUID;
    @Getter
    private Integer apocalypseFireworkInterval;
    @Getter
    private Integer apocalypseSkeletonInterval;

    public void load() {
        configFile = new File(Microppplugin.getInstance().getDataFolder(), "config.yml");

        // If there is no config file, create it
        if (!configFile.exists()) {
            Microppplugin.getInstance().saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        // Load config
        try {
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        // Add start day to yml
        String tmpDateString = config.getString("start-date");
        if (tmpDateString == null) {
            LocalDate today = LocalDate.now();
            tmpDateString = today.toString();
            set("start-date", tmpDateString);
        }
        startDate = LocalDate.parse(tmpDateString);

        // Load data from yml
        eventMessageInterval = config.getInt("event-message-interval");

        maxDistanceFromCenter = config.getInt("max-distance-from-center");
        // Set world border
        WorldBorder worldBorder = Bukkit.getWorlds().getFirst().getWorldBorder();
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(PluginSettings.getInstance().getMaxDistanceFromCenter() * 2);

        day = config.getInt("day");
        eventStartTime = LocalTime.parse(config.getString("event-start-time"));
        noEventMessage = config.getString("messages.no-event-message");
        noEventUntilMessage = config.getString("messages.no-event-until-message");
        docsMessage = config.getString("messages.docs-message");
        try {
            docsLink = new URI(config.getString("messages.docs-link"));
        } catch (URISyntaxException e) {
            docsLink = null;
        }
        apocalypseFireworkInterval = config.getInt("apocalypse.firework-interval");
        apocalypseSkeletonInterval = config.getInt("apocalypse.skeleton-interval");
        try {
            horseUUID = UUID.fromString(config.getString("horse-uuid"));
        } catch (NullPointerException e) {
            horseUUID = null;
        }
        try {
            bowHolderUUID = UUID.fromString(config.getString("bowholder-uuid"));
        } catch (NullPointerException e) {
            bowHolderUUID = null;
        }
        try {
            horseHolderUUID = UUID.fromString(config.getString("horseholder-uuid"));
        } catch (NullPointerException e) {
            horseHolderUUID = null;
        }
        try {
            villagerUUID = UUID.fromString(config.getString("villager-uuid"));
        } catch (NullPointerException e) {
            villagerUUID = null;
        }
        try {
            elytraHolderUUID = UUID.fromString(config.getString("elytra-uuid"));
        } catch (NullPointerException e) {
            elytraHolderUUID = null;
        }
        log.info("Settings loaded.");

    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }

    public void setDay(int day) {
        this.day = day;
        set("day", day);
    }

    public void setMaxDistanceFromCenter(int maxDistanceFromCenter) {
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        set("max-distance-from-center", maxDistanceFromCenter);
        WorldBorder worldBorder = Bukkit.getWorlds().getFirst().getWorldBorder();
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(maxDistanceFromCenter * 2);
    }

    public void setHorseUUID(UUID uuid) {
        this.horseUUID = uuid;
        set("horse-uuid", uuid.toString());
    }

    public void setBowHolderUUID(UUID uuid) {
        this.bowHolderUUID = uuid;
        set("bowholder-uuid", uuid.toString());
    }

    public void setHorseHolderUUID(UUID uuid) {
        this.horseHolderUUID = uuid;
        set("horseholder-uuid", uuid.toString());
    }

    public void setVillagerUUID(UUID uuid) {
        this.villagerUUID = uuid;
        set("villager-uuid", uuid.toString());
    }

    public void setElytraHolderUUID(UUID uuid) {
        this.elytraHolderUUID = uuid;
        set("elytra-uuid", uuid.toString());
    }

    public String getNoEventUntilMessage() {
        return noEventUntilMessage + eventStartTime.toString() + ".";
    }

    public String getEventMessage() {
        return switch (day) {
            case 0, 1 -> getNoEventMessage();
            default -> {
                if (LocalTime.now().isBefore(eventStartTime)) {
                    yield getNoEventUntilMessage();
                }
                yield "TMP";
            }
        };
    }
}
