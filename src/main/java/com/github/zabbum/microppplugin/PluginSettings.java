package com.github.zabbum.microppplugin;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
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
    private Integer maxDistanceDromCenter;
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
    private UUID horseUUID;
    @Getter
    private UUID stickHolderUUID;
    @Getter
    private UUID horseHolderUUID;

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
        maxDistanceDromCenter = config.getInt("max-distance-drom-center");
        day = config.getInt("day");
        eventStartTime = LocalTime.parse(config.getString("event-start-time"));
        noEventMessage = config.getString("messages.no-event-message");
        noEventUntilMessage = config.getString("messages.no-event-until-message");
        try {
            horseUUID = UUID.fromString(config.getString("horse-uuid"));
        } catch (NullPointerException e) {
            horseUUID = null;
        }
        try {
            stickHolderUUID = UUID.fromString(config.getString("stickholder-uuid"));
        } catch (NullPointerException e) {
            stickHolderUUID = null;
        }
        try {
            horseHolderUUID = UUID.fromString(config.getString("horseholder-uuid"));
        } catch (NullPointerException e) {
            horseHolderUUID = null;
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

    public void setHorseUUID(UUID uuid) {
        this.horseUUID = uuid;
        set("horse-uuid", uuid.toString());
    }

    public void setStickHolderUUID(UUID uuid) {
        this.stickHolderUUID = uuid;
        set("stickholder-uuid", uuid.toString());
    }

    public void setHorseHolderUUID(UUID uuid) {
        this.horseHolderUUID = uuid;
        set("horseholder-uuid", uuid.toString());
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
