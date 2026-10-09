package work.kubas.microppDsc.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import work.kubas.microppDsc.LinkCodesManager;
import work.kubas.microppDsc.MicroppDsc;
import work.kubas.microppDsc.storage.LinksStore;

import java.sql.SQLException;
import java.util.List;

public class ColorCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        try {
            if (sender instanceof Player player) {
                if (args.length == 0)
                    genCode(player);
                else if (args[0].equals("reset"))
                    reset(player);
            }
        } catch (SQLException e) {
            sender.sendMessage(Component.text("Wystąpił błąd. Skontaktuj się z administracją.", NamedTextColor.RED));
            MicroppDsc.getInstance().getLogger().severe("An error occured while executing /kolor.");
            MicroppDsc.getInstance().getLogger().severe(e.getMessage());
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of("reset");
    }

    private void reset(Player player) throws SQLException {
        var linksStore = MicroppDsc.getInstance().getLinksStore();
        linksStore.unlink(player.getUniqueId());

        genCode(player);
    }

    private void genCode(Player player) throws SQLException {
        LinksStore linksStore = MicroppDsc.getInstance().getLinksStore();
        LinkCodesManager linkCodesManager = MicroppDsc.getInstance().getLinkCodesManager();

        var discordId = linksStore.getDiscordId(player.getUniqueId());

        if (discordId.isEmpty()) {

            String code = linkCodesManager.generateCode(player.getUniqueId());
            player.sendMessage(Component.text("")
                    .append(Component.text("Twój kod połączenia:", NamedTextColor.GRAY))
                    .append(Component.newline())
                    .append(Component.text(code, NamedTextColor.GREEN, TextDecoration.BOLD, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.copyToClipboard(code))
                            .hoverEvent(HoverEvent.showText(Component.text("Kliknij, aby skopiować.")))
                    )
                    .append(Component.newline())
                    .append(Component.newline())
                    .append(Component.text("Aby połączyć swoje konto minecraft z kontem discord, wyślij powyższy kod w wiadomości prywatnej do bota ", NamedTextColor.GRAY))
                    .append(Component.text("μPP", NamedTextColor.GOLD))
                    .append(Component.text(".", NamedTextColor.GRAY))
            );
        } else {

            Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
                MicroppDsc.getInstance().getDiscordBridgeManager().sync(player, discordId.get());
            });
        }
    }
}
