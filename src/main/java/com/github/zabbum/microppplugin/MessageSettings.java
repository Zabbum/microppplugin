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

@NoArgsConstructor
public class MessageSettings {
    private final Logger log = Microppplugin.getInstance().getLog();
    @Getter
    private final static MessageSettings instance = new MessageSettings();
    private File configFile;
    private YamlConfiguration config;

    @Getter
    private Integer day;
    @Getter
    private LocalTime eventStartTime;
    @Getter
    private LocalDate startDate;
    @Getter
    private String noEventMessage;
    private String noEventUntilMessage;

    public void load() {
        configFile = new File(Microppplugin.getInstance().getDataFolder(), "messages-config.yml");

        // If there is no config file, create it
        if (!configFile.exists()) {
            Microppplugin.getInstance().saveResource("messages-config.yml", false);
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
        day = config.getInt("day");
        eventStartTime = LocalTime.parse(config.getString("event-start-time"));
        noEventMessage = config.getString("messages.no-event-message");
        noEventUntilMessage = config.getString("messages.no-event-until-message");
        log.info("Event messages loaded.");

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

    public String getNoEventUntilMessage() {
        return noEventUntilMessage+eventStartTime.toString()+".";
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
