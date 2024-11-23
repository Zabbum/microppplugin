package com.github.zabbum.microppplugin;

import com.github.zabbum.microppplugin.apocalypse.ApocalypseCommand;
import com.github.zabbum.microppplugin.horse.GetHorseCommand;
import com.github.zabbum.microppplugin.horse.HorseEventListener;
import com.github.zabbum.microppplugin.bow.GetBowCommand;
import com.github.zabbum.microppplugin.bow.BowEventListener;
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
        getCommand("getbow").setExecutor(new GetBowCommand());
        getCommand("gethorse").setExecutor(new GetHorseCommand());
        getCommand("apocalypse").setExecutor(new ApocalypseCommand());
        getCommand("setborder").setExecutor(new SetborderCommand());
        // Load settings
        PluginSettings.getInstance().load();
        // Event handlers
        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new FirePreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowEventListener(), this);
        getServer().getPluginManager().registerEvents(new HorseEventListener(), this);

        // Inform about event periodically
        BukkitTask task = new MessageTask(this).runTaskTimer(this, 0, PluginSettings.getInstance().getEventMessageInterval());



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
