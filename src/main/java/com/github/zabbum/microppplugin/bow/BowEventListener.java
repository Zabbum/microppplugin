package com.github.zabbum.microppplugin.bow;

import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.slf4j.Logger;

import java.util.Objects;

public class BowEventListener implements Listener {
    private static final Logger log = Microppplugin.getInstance().getSLF4JLogger();

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        // Check if player is using THE BOW
        Arrow arrow = (Arrow) event.getProjectile();
        Player player = (Player) event.getEntity();
        ItemStack bow = event.getBow();

        if (bow.getType() == Material.BOW && bow.getItemMeta() != null) {
            ItemMeta meta = bow.getItemMeta();
            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theBow");
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING) && Objects.equals(container.get(key, PersistentDataType.STRING), "001")) {
                if (player != null) {
                    Bukkit.broadcast(
                            Component.text(player.getName())
                                    .color(NamedTextColor.RED)
                                    .decoration(TextDecoration.BOLD, true)
                                    .append(Component.text(" użył PIERUŃSKIEGO ŁUKU!")
                                            .color(NamedTextColor.GOLD)
                                    )
                    );

                    // Revoke previous player's suffix and give it to new one
                    LuckPerms luckPermsApi = LuckPermsProvider.get();

                    try {
                        User oldUser = luckPermsApi.getUserManager()
                                .getUser(PluginSettings.getInstance().getBowHolderUUID());
                        oldUser.data().remove(Node.builder("group.bowholder").build());
                        luckPermsApi.getUserManager().saveUser(oldUser);

                        log.info("Revoked bowholder status from player with UUID: {}", PluginSettings.getInstance().getBowHolderUUID());
                    }
                    catch (NullPointerException e) {
                        log.warn("Skill issue, null pointer exception");
                    }

                    User newUser = luckPermsApi.getUserManager().getUser(player.getUniqueId());
                    newUser.data().add(Node.builder("group.bowholder").build());
                    luckPermsApi.getUserManager().saveUser(newUser);
                    PluginSettings.getInstance().setBowHolderUUID(player.getUniqueId());

                    log.info("Set bowholde status to player with UUID: {}", player.getUniqueId());
                }

                // Launch a projectile
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
            hitLocation.getWorld().createExplosion(hitLocation, 3, true, false);

            AreaEffectCloud cloud = (AreaEffectCloud) hitLocation.getWorld().spawnEntity(hitLocation, EntityType.AREA_EFFECT_CLOUD);
            cloud.setBasePotionType(PotionType.STRONG_HARMING);
            cloud.setDuration(60);

            arrow.remove();
        }
    }
}
