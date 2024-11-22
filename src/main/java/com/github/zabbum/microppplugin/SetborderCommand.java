package com.github.zabbum.microppplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SetborderCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        try {
            int newBorder = Integer.parseInt(args[0])/2;
            PluginSettings.getInstance().setMaxDistanceFromCenter(newBorder);

            Bukkit.broadcast(
                    Component.text("Od teraz świat ma rozmiar: ")
                            .color(NamedTextColor.AQUA)
                            .append(
                                    Component.text((newBorder*2)+"x"+(newBorder*2))
                                            .color(NamedTextColor.GOLD)
                                            .decoration(TextDecoration.BOLD, true)
                            )
                            .append(
                                    Component.text(".")
                                            .color(NamedTextColor.AQUA)
                            )
            );

            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
