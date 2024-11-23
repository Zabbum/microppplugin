package com.github.zabbum.microppplugin.villager;

import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.slf4j.Logger;

public class VillagerTradeLockListener implements Listener {
    private final Logger log = Microppplugin.getInstance().getSLF4JLogger();
    @EventHandler
    public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event) {
        Villager villager = (Villager) event.getEntity();

        log.info("Villager: {} attempted to acquire trade", villager.getUniqueId());

        if (villager.getUniqueId() == PluginSettings.getInstance().getVillagerUUID()) {
            villager.setRecipes(VillagerOffers.getOffers());
        }
    }
}
