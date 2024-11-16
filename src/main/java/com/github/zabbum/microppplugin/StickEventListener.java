package com.github.zabbum.microppplugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

import java.util.Objects;

public class StickEventListener implements Listener {

    @EventHandler
    public void onStickShoot(PlayerInteractEvent event) {
        // Check if player is using THE STICK
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.STICK && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theStick");
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING) && Objects.equals(container.get(key, PersistentDataType.STRING), "001")) {
                Arrow arrow = player.launchProjectile(Arrow.class);
                arrow.setMetadata("theArrow", new FixedMetadataValue(Microppplugin.getInstance(), true));
                arrow.setVelocity(player.getLocation().getDirection().multiply(2));
            }
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && arrow.hasMetadata("theArrow")) {
            Location hitLocation = arrow.getLocation();
            hitLocation.getWorld().strikeLightning(hitLocation);
            hitLocation.getWorld().createExplosion(hitLocation, 4);

            AreaEffectCloud cloud = (AreaEffectCloud) hitLocation.getWorld().spawnEntity(hitLocation, EntityType.AREA_EFFECT_CLOUD);
            cloud.setBasePotionType(PotionType.STRONG_HARMING);
            cloud.setDuration(60);

            arrow.remove();
        }
    }
}
