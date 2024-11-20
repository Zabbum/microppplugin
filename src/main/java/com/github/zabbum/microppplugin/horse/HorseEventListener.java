package com.github.zabbum.microppplugin.horse;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HorseEventListener implements Listener {
    private static final Logger log = Microppplugin.getInstance().getSLF4JLogger();

    @EventHandler
    public void onEquip(PlayerArmorChangeEvent event) {
        // Check if player is equipping the chestplate with horse
        Player player = event.getPlayer();
        ItemStack chest = event.getNewItem();

        if (chest.getType() == Material.NETHERITE_CHESTPLATE && chest.getItemMeta() != null) {
            ItemMeta meta = chest.getItemMeta();
            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theHorseChest");
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING) &&
                    Objects.equals(container.get(key, PersistentDataType.STRING), "001")
            ) {
                Bukkit.broadcast(
                        Component.text(player.getName())
                                .color(NamedTextColor.RED)
                                .decoration(TextDecoration.BOLD, true)
                                .append(Component.text(" założył KOŃSKI CYCNIK!")
                                        .color(NamedTextColor.GREEN)
                                )
                );

                // Revoke previous player's suffix and give it to new one
                LuckPerms luckPermsApi = LuckPermsProvider.get();

                try {
                    CompletableFuture<User> userFuture = luckPermsApi.getUserManager()
                            .loadUser(PluginSettings.getInstance().getHorseHolderUUID());
                    userFuture.thenAcceptAsync(oldUser -> {
                        oldUser.data().remove(Node.builder("group.horseholder").build());
                        luckPermsApi.getUserManager().saveUser(oldUser);
                    });
                }
                catch (NullPointerException e) {
                    log.warn("Skill issue, null pointer exception");
                }

                User newUser = luckPermsApi.getUserManager().getUser(player.getUniqueId());
                newUser.data().add(Node.builder("group.horseholder").build());
                luckPermsApi.getUserManager().saveUser(newUser);
                PluginSettings.getInstance().setHorseHolderUUID(player.getUniqueId());

                // If there exists the horse, kill it
                UUID oldHorseUUID = PluginSettings.getInstance().getHorseUUID();
                if (oldHorseUUID != null) {
                    for (Entity entity : Bukkit.getWorld("world").getEntities()) {
                        if (entity.getUniqueId().equals(oldHorseUUID)) {
                            // Fancy animation of removing
                            Location loc = entity.getLocation();

                            Firework firework = loc.getWorld().spawn(loc, Firework.class);
                            FireworkMeta fireworkMeta = firework.getFireworkMeta();

                            fireworkMeta.setPower(1);
                            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.GREEN, Color.YELLOW).flicker(false).build());

                            firework.setFireworkMeta(fireworkMeta);
                            firework.detonate();

                            entity.remove();
                            Microppplugin.getInstance().getSLF4JLogger().info("Horse with UUID: {} removed", oldHorseUUID);
                        }
                    }
                }

                ZombieHorse theHorse = (ZombieHorse) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE_HORSE);
                theHorse.customName(Component.text("SZALONY KOŃ")
                        .color(NamedTextColor.GREEN)
                        .decoration(TextDecoration.BOLD, true));
                theHorse.setTamed(true);
                theHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                theHorse.setJumpStrength(2);
                theHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(1.6);
                theHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(100);
                theHorse.setHealth(100);

                PluginSettings.getInstance().setHorseUUID(theHorse.getUniqueId());
            }
        }
    }
}
