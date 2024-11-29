package com.github.zabbum.microppplugin.aquakit;

import com.github.zabbum.microppplugin.Microppplugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

public class GetAquakitCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player player) {

            // Trident
            ItemStack trident = new ItemStack(Material.TRIDENT);
            ItemMeta tridentMeta = trident.getItemMeta();

            tridentMeta.displayName(Component.text("OKOŃSKI WIDELEC")
                    .color(NamedTextColor.DARK_AQUA)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false));
            tridentMeta.lore(List.of(
                    Component.text("Wielki artefakt serwerowy")
                            .color(NamedTextColor.GREEN),
                    Component.text("Ugotowano: ")
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .append(
                                    Component.text(LocalDate.now().toString())
                                            .color(NamedTextColor.GOLD)
                            )
            ));
            tridentMeta.addEnchant(Enchantment.RIPTIDE, 5, true);
            tridentMeta.setUnbreakable(true);

            NamespacedKey tridentKey = new NamespacedKey(Microppplugin.getInstance(), "theTrident");
            PersistentDataContainer tridentContainer = tridentMeta.getPersistentDataContainer();
            tridentContainer.set(tridentKey, PersistentDataType.STRING, "001");

            trident.setItemMeta(tridentMeta);

            // Elytra
            ItemStack elytra = new ItemStack(Material.ELYTRA);
            ItemMeta elytraMeta = elytra.getItemMeta();

            elytraMeta.displayName(Component.text("ELYTRA I CHUJ")
                    .color(NamedTextColor.DARK_AQUA)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false));
            elytraMeta.lore(List.of(
                    Component.text("Wielki artefakt serwerowy")
                            .color(NamedTextColor.GREEN),
                    Component.text("Ugotowano: ")
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .append(
                                    Component.text(LocalDate.now().toString())
                                            .color(NamedTextColor.GOLD)
                            )
            ));
            elytraMeta.setEnchantmentGlintOverride(true);
            elytraMeta.setUnbreakable(true);

            NamespacedKey elytraKey = new NamespacedKey(Microppplugin.getInstance(), "theElytra");
            PersistentDataContainer elytraContainer = elytraMeta.getPersistentDataContainer();
            elytraContainer.set(elytraKey, PersistentDataType.STRING, "001");

            elytra.setItemMeta(elytraMeta);

            // Give artifacts to the player
            player.getInventory().addItem(trident);
            player.getInventory().addItem(elytra);
            player.sendMessage(Component.text("Otrzymałeś wielki skarb, " + player.getName()).color(NamedTextColor.AQUA));

        } else {
            sender.sendMessage(Component.text("Tej komendy może użyć tylko gracz."));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
