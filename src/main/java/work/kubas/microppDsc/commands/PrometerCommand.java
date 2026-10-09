package work.kubas.microppDsc.commands;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import work.kubas.microppDsc.MicroppDsc;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class PrometerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Tylko gracze mogą używać tej komendy!", NamedTextColor.RED));
            return true;
        }

        Objective counter = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("prometer");
        var store = MicroppDsc.getInstance().getSidebarStore();

        try {
            var isHidden = store.isHidden(player);

            // If player is not yet in the db.
            if (isHidden.isEmpty()) {
                store.insertPlayer(player);
                isHidden = Optional.of(Boolean.FALSE);
            }

            if (isHidden.get().equals(Boolean.FALSE)) {
                store.hideSidebar(player);
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                sender.sendMessage(
                        Component.empty()
                                .append(Component.text("Ukryto ", NamedTextColor.GOLD))
                                .append(Component.text("PROMETER", NamedTextColor.RED, TextDecoration.BOLD))
                                .append(Component.text("!", NamedTextColor.GOLD))
                );
            }
            else  {
                store.showSidebar(player);
                player.setScoreboard(counter.getScoreboard());
                sender.sendMessage(
                        Component.empty()
                                .append(Component.text("Pokazano ", NamedTextColor.GOLD))
                                .append(Component.text("PROMETER", NamedTextColor.RED, TextDecoration.BOLD))
                                .append(Component.text("!", NamedTextColor.GOLD))
                );
            }
            return true;
        } catch (SQLException e) {
            sender.sendMessage(Component.text("Wystąpił błąd. Skontaktuj się z administracją.", NamedTextColor.RED));
            log.error("/prometer command exception", e);
            return false;
        }
    }
}
