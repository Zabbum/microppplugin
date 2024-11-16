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
        // Commands handling
        getCommand("eventmessage").setExecutor(new EventMessageCommand());
        getCommand("getstick").setExecutor(new GetStickCommand());
        // Load settings
        MessageSettings.getInstance().load();
        // Event handlers
        getServer().getPluginManager().registerEvents(new StickEventListener(), this);
        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);

        // Inform about event periodically
        BukkitTask task = new MessageTask(this).runTaskTimer(this, 0, 200);

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
