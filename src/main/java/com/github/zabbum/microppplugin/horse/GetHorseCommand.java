package com.github.zabbum.microppplugin.horse;

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

public class GetHorseCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {

            ItemStack horseChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
            ItemMeta meta = horseChestplate.getItemMeta();

            meta.displayName(Component.text("KOŃSKI CYCNIK")
                    .color(NamedTextColor.DARK_GREEN)
                    .decoration(TextDecoration.BOLD, true)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Wielki artefakt serwerowy")
                            .color(NamedTextColor.GREEN),
                    Component.text("Ugotowano: ")
                            .color(NamedTextColor.LIGHT_PURPLE)
                            .append(
                                    Component.text(LocalDate.now().toString())
                                            .color(NamedTextColor.GOLD)
                            )
            ));
            meta.addEnchant(Enchantment.PROTECTION, 10, true);
            meta.setUnbreakable(true);

            NamespacedKey key = new NamespacedKey(Microppplugin.getInstance(), "theHorseChest");
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(key, PersistentDataType.STRING, "001");

            horseChestplate.setItemMeta(meta);

            player.getInventory().addItem(horseChestplate);
            player.sendMessage(Component.text("Otrzymałeś wielki skarb, " + player.getName()).color(NamedTextColor.AQUA));
        }
        else {
            sender.sendMessage(Component.text("Tej komendy może użyć tylko gracz."));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
