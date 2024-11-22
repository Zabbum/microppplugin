package com.github.zabbum.microppplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

public class FirePreventionListener implements Listener {
    @EventHandler
    public void onBlockBurn(BlockSpreadEvent event) {
        if (event.getNewState().getType() == Material.FIRE) {
            Location location = event.getBlock().getLocation();

            if (shouldPrevent(location, PluginSettings.getInstance().getMaxDistanceFromCenter())) {
                event.setCancelled(true);
            }
        }
    }

    private boolean shouldPrevent(Location location, int maxDistance) {
        return location.getX() > maxDistance || location.getX() < -maxDistance ||
                location.getZ() > maxDistance || location.getZ() < -maxDistance;
    }
}
