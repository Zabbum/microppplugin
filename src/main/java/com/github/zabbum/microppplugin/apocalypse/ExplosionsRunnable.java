package com.github.zabbum.microppplugin.apocalypse;

import com.github.zabbum.microppplugin.PluginSettings;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class ExplosionsRunnable implements Runnable {
    private final Random random = new Random();
    private final Integer maxDistance = PluginSettings.getInstance().getMaxDistanceFromCenter();

    @Override
    public void run() {
        Location location = new Location(Bukkit.getWorlds().getFirst(),
                random.nextInt(maxDistance * 2) - maxDistance,
                random.nextInt(50) + 60,
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

        if (Bukkit.getWorlds().getFirst().getTime() < 12600) {
            Bukkit.getServerTickManager().setTickRate(80);
        }
        else {
            Bukkit.getServerTickManager().setTickRate(20);
        }
    }
}
