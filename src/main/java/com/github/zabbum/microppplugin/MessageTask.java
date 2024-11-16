package com.github.zabbum.microppplugin;

import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
public class MessageTask extends BukkitRunnable {
    private final JavaPlugin plugin;

    @Override
    public void run() {
        MessageSettings messageSettings = MessageSettings.getInstance();

        messageSettings.setDay((int) ChronoUnit.DAYS.between(messageSettings.getStartDate(), LocalDate.now()));

        plugin.getSLF4JLogger().info("Start date: {}", MessageSettings.getInstance().getStartDate().toString());
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "eventmessage");
    }
}
