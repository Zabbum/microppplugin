package com.github.zabbum.microppplugin.aquakit;

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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;

import java.util.Objects;

public class ElytraEventListener implements Listener {
    private static final Logger log = Microppplugin.getInstance().getSLF4JLogger();

    @EventHandler
    public void onElytraEquip(PlayerArmorChangeEvent event) {
        // Check if player is equipping the elytra
        Player player = event.getPlayer();
        ItemStack chest = event.getNewItem();

        if (chest.getType() == Material.ELYTRA && chest.getItemMeta() != null) {
            ItemMeta meta = chest.getItemMeta();
            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theElytra");
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING) &&
                    Objects.equals(container.get(key, PersistentDataType.STRING), "001")) {

                Bukkit.broadcast(
                        Component.text(player.getName())
                                .color(NamedTextColor.RED)
                                .decoration(TextDecoration.BOLD, true)
                                .append(Component.text(" założył ELYTRĘ I CHUJ!")
                                        .color(NamedTextColor.DARK_AQUA)
                                )
                );

                // Revoke previous player's suffix and give it to new one
                LuckPerms luckPermsApi = LuckPermsProvider.get();

                try {
                    User oldUser = luckPermsApi.getUserManager()
                            .getUser(PluginSettings.getInstance().getElytraHolderUUID());
                    oldUser.data().remove(Node.builder("group.elytraholder").build());
                    luckPermsApi.getUserManager().saveUser(oldUser);
                } catch (NullPointerException e) {
                    log.warn("Skill issue, null pointer exception");
                }

                // Change weather
                Bukkit.getWorlds().getFirst().setStorm(true);

                PluginSettings.getInstance().setElytraHolderUUID(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack chest = player.getInventory().getChestplate();

            if (chest != null && chest.getType() == Material.ELYTRA && chest.getItemMeta() != null) {
                ItemMeta meta = chest.getItemMeta();
                NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theElytra");
                PersistentDataContainer container = meta.getPersistentDataContainer();

                if (container.has(key, PersistentDataType.STRING) &&
                        Objects.equals(container.get(key, PersistentDataType.STRING), "001")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onElytraUnequip(PlayerArmorChangeEvent event) {
        Player player = event.getPlayer();
        ItemStack chest = event.getOldItem();

        if (chest.getType() == Material.ELYTRA && chest.getItemMeta() != null) {
            ItemMeta meta = chest.getItemMeta();
            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theElytra");
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.STRING) &&
                    Objects.equals(container.get(key, PersistentDataType.STRING), "001")) {

                Bukkit.broadcast(
                        Component.text(player.getName())
                                .color(NamedTextColor.RED)
                                .decoration(TextDecoration.BOLD, true)
                                .append(Component.text(" ściągnął ELYTRĘ I CHUJ!")
                                        .color(NamedTextColor.DARK_AQUA)
                                )
                );

                // Change weather
                Bukkit.getWorlds().getFirst().setStorm(false);
            }
        }
    }
}
