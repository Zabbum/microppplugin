package com.github.zabbum.microppplugin.apocalypse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ApocalypseCommand implements CommandExecutor, TabCompleter {
    private final ApocalypseEvent apocalypseEvent = new ApocalypseEvent();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return true;
        }

        if (args[0].equals("start")) {
            apocalypseEvent.start();
            return true;
        }

        if (args[0].equals("end")) {
            apocalypseEvent.end();
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return List.of("start", "end");
    }
}
