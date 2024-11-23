package com.github.zabbum.microppplugin.villager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerOffers {
    public static List<MerchantRecipe> getOffers() {
        List<MerchantRecipe> offers = new ArrayList<>();

        // 16 COAL -> 1 EMERALD
        MerchantRecipe coalToEmerald = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1),
                0, 16, true
        );
        coalToEmerald.addIngredient(new ItemStack(Material.COAL, 16));
        offers.add(coalToEmerald);

        // 32 STONE -> 1 EMERALD
        MerchantRecipe stoneToEmerald = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1),
                0, 16, true
        );
        stoneToEmerald.addIngredient(new ItemStack(Material.STONE, 32));
        offers.add(stoneToEmerald);

        // 4 GOLD INGOTS -> 1 EMERALD
        MerchantRecipe goldToEmerald = new MerchantRecipe(
                new ItemStack(Material.EMERALD, 1),
                0, 16, true
        );
        goldToEmerald.addIngredient(new ItemStack(Material.GOLD_INGOT, 4));
        offers.add(goldToEmerald);

        // 1 EMERALD -> 4 IRON
        MerchantRecipe emeraldToIron = new MerchantRecipe(
                new ItemStack(Material.IRON_INGOT, 4),
                0, 16, true
        );
        emeraldToIron.addIngredient(new ItemStack(Material.EMERALD, 1));
        offers.add(emeraldToIron);

        // 2 EMERALDS -> 1 DIAMOND
        MerchantRecipe emeraldsToDiamond = new MerchantRecipe(
                new ItemStack(Material.DIAMOND, 1),
                0, 16, true
        );
        emeraldsToDiamond.addIngredient(new ItemStack(Material.EMERALD, 2));
        offers.add(emeraldsToDiamond);

        return offers;
    }
}
