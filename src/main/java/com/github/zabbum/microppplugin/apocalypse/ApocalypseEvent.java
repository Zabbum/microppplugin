package com.github.zabbum.microppplugin.apocalypse;

import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitTask;
import org.slf4j.Logger;

import java.util.Random;

@NoArgsConstructor
public class ApocalypseEvent {
    private final Microppplugin plugin = Microppplugin.getInstance();
    private final Logger log = plugin.getSLF4JLogger();
    private final Random random = new Random();
    private BukkitTask task;

    public synchronized void start() {
        if (task != null) {
            log.info("ApocalypseEvent already started");
            return;
        }

        int period = PluginSettings.getInstance().getApocalypseFireworkInterval();

        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Generate and explode firework
            Integer maxDistance = PluginSettings.getInstance().getMaxDistanceFromCenter();

            Location location = new Location(Bukkit.getWorlds().getFirst(),
                    random.nextInt(maxDistance * 2) - maxDistance,
                    random.nextInt(30) + 60,
                    random.nextInt(maxDistance * 2) - maxDistance);

            Firework firework = location.getWorld().spawn(location, Firework.class);
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            fireworkMeta.setPower(3);
            fireworkMeta.addEffect(
                    FireworkEffect.builder()
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.RED, Color.PURPLE, Color.BLACK)
                            .flicker(true)
                            .build()
            );

            firework.setFireworkMeta(fireworkMeta);
            firework.detonate();

            location.getWorld().createExplosion(location, 5, true, true);

            log.info("Firework detonated at: x: {}, y: {}, z: {}", location.getX(), location.getY(), location.getZ());
            log.info("Time: {}", Bukkit.getWorlds().getFirst().getTime());

            if (Bukkit.getWorlds().getFirst().getTime() < 12600) {
                Bukkit.getWorlds().getFirst().setTime(Bukkit.getWorlds().getFirst().getTime() + 20);
            }
        }, 0, period);

        Bukkit.broadcast(Component.text("Rozpoczęła się apokalipsa!")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        );
    }

    public synchronized void end() {
        if (task == null) {
            log.info("ApocalypseEvent is not started");
            return;
        }

        task.cancel();
        task = null;
        log.info("ApocalypseEvent stopped");

        Bukkit.broadcast(Component.text("Zakończyła się apokalipsa!")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        );
    }

}
