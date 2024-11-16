package com.github.zabbum.microppplugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

public final class Microppplugin extends JavaPlugin {
    @Getter
    private final Logger log = getSLF4JLogger();

    @Override
    public void onEnable() {
        getCommand("eventmessage").setExecutor(new EventMessageCommand());
        MessageSettings.getInstance().load();

        BukkitTask task = new MessageTask(this).runTaskTimer(this, 0, 20);

        log.info("Microppplugin enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log.info("Microppplugin disabled!");
    }

    public static Microppplugin getInstance() {
        return getPlugin(Microppplugin.class);
    }
}
