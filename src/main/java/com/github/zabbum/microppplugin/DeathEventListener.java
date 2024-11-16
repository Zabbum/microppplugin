package com.github.zabbum.microppplugin;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class DeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Location location = e.getPlayer().getLocation();

        Microppplugin.getInstance().getSLF4JLogger().info("Death.");

        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();

        meta.setPower(2);
        meta.addEffect(FireworkEffect.builder().withColor(Color.RED).flicker(false).withFade(Color.WHITE).build());

        firework.setFireworkMeta(meta);
        firework.detonate();
    }
}
