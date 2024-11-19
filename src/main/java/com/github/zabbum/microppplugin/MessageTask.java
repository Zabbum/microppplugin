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
        PluginSettings pluginSettings = PluginSettings.getInstance();

        pluginSettings.setDay((int) ChronoUnit.DAYS.between(pluginSettings.getStartDate(), LocalDate.now()));

        plugin.getSLF4JLogger().info("Start date: {}", PluginSettings.getInstance().getStartDate().toString());
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "eventmessage");
    }
}
