package com.github.zabbum.microppplugin;

import com.github.zabbum.microppplugin.apocalypse.ApocalypseCommand;
import com.github.zabbum.microppplugin.aquakit.ElytraEventListener;
import com.github.zabbum.microppplugin.aquakit.GetAquakitCommand;
import com.github.zabbum.microppplugin.bow.BowEventListener;
import com.github.zabbum.microppplugin.bow.GetBowCommand;
import com.github.zabbum.microppplugin.horse.GetHorseCommand;
import com.github.zabbum.microppplugin.horse.HorseEventListener;
import com.github.zabbum.microppplugin.villager.SpawnvillagerCommand;
import com.github.zabbum.microppplugin.villager.VillagerTradeLockListener;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.slf4j.Logger;

@Getter
public final class Microppplugin extends JavaPlugin {
    private final Logger log = getSLF4JLogger();

    @Override
    public void onEnable() {

        // Commands handling
        getCommand("eventmessage").setExecutor(new EventMessageCommand());
        getCommand("getbow").setExecutor(new GetBowCommand());
        getCommand("gethorse").setExecutor(new GetHorseCommand());
        getCommand("apocalypse").setExecutor(new ApocalypseCommand());
        getCommand("setborder").setExecutor(new SetborderCommand());
        getCommand("spawnvillager").setExecutor(new SpawnvillagerCommand());
        getCommand("getaquakit").setExecutor(new GetAquakitCommand());
        // Load settings
        PluginSettings.getInstance().load();
        // Event handlers
        getServer().getPluginManager().registerEvents(new DeathEventListener(), this);
        getServer().getPluginManager().registerEvents(new FirePreventionListener(), this);
        getServer().getPluginManager().registerEvents(new BowEventListener(), this);
        getServer().getPluginManager().registerEvents(new HorseEventListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerTradeLockListener(), this);
        getServer().getPluginManager().registerEvents(new ElytraEventListener(), this);

        // Inform about event periodically
        BukkitTask task = new MessageTask(this).runTaskTimer(this, 0, PluginSettings.getInstance().getEventMessageInterval());

        // Death count scoreboard
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard board = scoreboardManager.getMainScoreboard();
        Objective prometer = board.getObjective("prometer");
        if (prometer == null) {
            prometer = board.registerNewObjective(
                    "prometer",
                    Criteria.DEATH_COUNT,
                    Component.text("--<")
                            .color(NamedTextColor.GOLD)
                            .append(
                                    Component.text(" PROMETER ")
                                            .color(NamedTextColor.RED)
                                            .decoration(TextDecoration.BOLD, true)
                            )
                            .append(
                                    Component.text(">--")
                                    .color(NamedTextColor.GOLD)
                            )
            );

            prometer.setDisplaySlot(DisplaySlot.SIDEBAR);
            log.info("PROMETER initialized.");
        }

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
