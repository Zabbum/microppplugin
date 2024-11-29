package com.github.zabbum.microppplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.List;

public class EventMessageCommand implements CommandExecutor, TabExecutor {
    private final Logger log = Microppplugin.getInstance().getSLF4JLogger();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // Check if day is correct

        // Broadcast messages
        TextComponent eventMessage = Component.text("[").color(NamedTextColor.WHITE)
                .append(Component.text("micro").color(NamedTextColor.LIGHT_PURPLE))
                .append(Component.text("PP").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true))
                .append(Component.text("] ").color(NamedTextColor.WHITE))
                .append(Component.text("DZIEŃ " + (PluginSettings.getInstance().getDay() + 1) + ".").color(NamedTextColor.GREEN))
                .append(Component.text(" - ").color(NamedTextColor.WHITE))
                .append(Component.text(PluginSettings.getInstance().getEventMessage())
                        .color(NamedTextColor.AQUA)
                        .decoration(TextDecoration.BOLD, true));
        Bukkit.broadcast(eventMessage);

        try {
            TextComponent docsMessage = Component.text("[").color(NamedTextColor.WHITE)
                    .append(Component.text("micro").color(NamedTextColor.LIGHT_PURPLE))
                    .append(Component.text("PP").color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, true))
                    .append(Component.text("] ").color(NamedTextColor.WHITE))
                    .append(Component.text(PluginSettings.getInstance().getDocsMessage())
                            .color(NamedTextColor.YELLOW)
                            .decoration(TextDecoration.UNDERLINED, true)
                            .clickEvent(ClickEvent.openUrl(PluginSettings.getInstance().getDocsLink().toURL())));
            Bukkit.broadcast(docsMessage);
        } catch (MalformedURLException e) {
            log.error("Malformed docs URL: {}", e.getMessage());
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of();
    }
}
