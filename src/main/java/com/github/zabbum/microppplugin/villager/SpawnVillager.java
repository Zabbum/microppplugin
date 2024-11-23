package com.github.zabbum.microppplugin.villager;

import com.github.zabbum.microppplugin.PluginSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class SpawnVillager {
    public static void spawnVillager(Location location) {
        Villager villager = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);

        villager.customName(
                Component.text("GEORGE SOROS")
                        .color(NamedTextColor.DARK_BLUE)
                        .decoration(TextDecoration.BOLD, true)
        );
        villager.setProfession(Villager.Profession.TOOLSMITH);
        villager.setVillagerLevel(5);
        villager.setVillagerExperience(0);
        villager.setVillagerType(Villager.Type.JUNGLE);
        villager.setRecipes(VillagerOffers.getOffers());

        PluginSettings.getInstance().setVillagerUUID(villager.getUniqueId());

        Bukkit.broadcast(
                Component.text("NA MAPIE ZESPAWNOWAŁ SIĘ SPECJALNY VILLAGER!")
                        .color(NamedTextColor.DARK_BLUE)
                        .decoration(TextDecoration.BOLD, true)
        );
    }
}
