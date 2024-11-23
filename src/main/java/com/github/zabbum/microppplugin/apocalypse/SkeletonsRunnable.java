package com.github.zabbum.microppplugin.apocalypse;

import com.github.zabbum.microppplugin.Microppplugin;
import com.github.zabbum.microppplugin.PluginSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class SkeletonsRunnable implements Runnable {
    private final Random random = new Random();
    private final Integer maxDistance = PluginSettings.getInstance().getMaxDistanceFromCenter();

    @Override
    public void run() {
        Location location = new Location(Bukkit.getWorlds().getFirst(),
                random.nextInt(maxDistance * 2) - maxDistance,
                random.nextInt(30) + 60,
                random.nextInt(maxDistance * 2) - maxDistance);

        // Spawn horse
        ZombieHorse zombieHorse = (ZombieHorse) location.getWorld().spawnEntity(location, EntityType.ZOMBIE_HORSE);
        zombieHorse.customName(Component.text("SZALONY KOŃ")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true));
        zombieHorse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        zombieHorse.setJumpStrength(1.3);
        zombieHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(1.2);
        zombieHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        zombieHorse.setHealth(20);

        // Spawn skeleton that will be riding this horse
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        skeleton.customName(Component.text("JEŹDZIEC APOKALIPSY")
                .color(NamedTextColor.DARK_PURPLE)
                .decoration(TextDecoration.BOLD, true)
        );
        skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        skeleton.setHealth(20);

        ItemStack chestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setEnchantmentGlintOverride(true);
        chestplate.setItemMeta(chestplateMeta);

        skeleton.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
        skeleton.getEquipment().setChestplate(chestplate);
        zombieHorse.addPassenger(skeleton);

        // Add the bow to skeleton
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setEnchantmentGlintOverride(true);
        NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theBow");
        PersistentDataContainer container = bowMeta.getPersistentDataContainer();
        container.set(key, PersistentDataType.STRING, "001");
        bow.setItemMeta(bowMeta);

        skeleton.getEquipment().setItemInMainHand(bow);
    }
}
