package com.github.zabbum.microppplugin.apocalypse;

import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

@NoArgsConstructor
public class ApocalypseEvent {
    private final Microppplugin plugin = Microppplugin.getInstance();
    private final Logger log = plugin.getSLF4JLogger();
    private BukkitTask explosionsTask;
    private BukkitTask skeletonsTask;

    public synchronized void start() {
        if (explosionsTask != null) {
            log.info("ApocalypseEvent already started");
            return;
        }

        int explosionPeriod = PluginSettings.getInstance().getApocalypseFireworkInterval();
        int skeletonPeriod = PluginSettings.getInstance().getApocalypseSkeletonInterval();

        // Run apocalypse
        explosionsTask = Bukkit.getScheduler().runTaskTimer(plugin, new ExplosionsRunnable(), 0, explosionPeriod);
        skeletonsTask = Bukkit.getScheduler().runTaskTimer(plugin, new SkeletonsRunnable(), 0, skeletonPeriod);

        Bukkit.broadcast(Component.text("Rozpoczęła się apokalipsa!")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        );

        log.info("ApocalypseEvent started!");
    }

    public synchronized void end() {
        if (explosionsTask == null) {
            log.info("ApocalypseEvent is not started");
            return;
        }

        explosionsTask.cancel();
        explosionsTask = null;
        skeletonsTask.cancel();
        skeletonsTask = null;

        Bukkit.getServerTickManager().setTickRate(20);

        log.info("ApocalypseEvent stopped");

        Bukkit.broadcast(Component.text("Zakończyła się apokalipsa!")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        );
    }

}
