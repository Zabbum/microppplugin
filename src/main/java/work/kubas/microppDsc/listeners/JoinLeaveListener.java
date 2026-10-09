package work.kubas.microppDsc.listeners;

import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import work.kubas.microppDsc.MicroppDsc;

import java.sql.SQLException;

@Slf4j
public class JoinLeaveListener implements Listener {

    private static final double STARTING_BALANCE = 1000.0;
    private final Economy economy;

    public JoinLeaveListener() {
        this.economy = MicroppDsc.getInstance().getEconomy();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();

        MicroppDsc.getInstance().getColorManager().updateNameColor(player);

        MicroppDsc.getInstance().getDiscordBridgeManager().sendToDsc(
            "<:f2_plus1:1425554580485181490> **" +  player.getName() + "** dołączył do serwera!"
        );

        // Hide prometer
        try {
            var isPrometerHidden = MicroppDsc.getInstance().getSidebarStore().isHidden(player);

            if (isPrometerHidden.isPresent() && isPrometerHidden.get()) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                player.sendMessage(Component.empty()
                        .append(Component.text("PROMETER", NamedTextColor.RED, TextDecoration.BOLD))
                        .append(Component.text(" ukryty!", NamedTextColor.GRAY))
                );
            }

        } catch (SQLException e) {
            log.error("Prometer init exception", e);
        }

        if (!event.getPlayer().hasPlayedBefore()) {
            if (!economy.hasAccount(event.getPlayer())) {
                economy.createPlayerAccount(event.getPlayer());
            }

            // Reset balance
            double balance = economy.getBalance(event.getPlayer());
            economy.withdrawPlayer(event.getPlayer(), balance);


            economy.depositPlayer(event.getPlayer(), STARTING_BALANCE);

            event.getPlayer().sendMessage(
                    Component.empty()
                            .append(Component.text("Witamy na serwerze μPP, ", NamedTextColor.WHITE, TextDecoration.BOLD))
                            .append(Component.text(event.getPlayer().getName(), NamedTextColor.YELLOW, TextDecoration.BOLD))
                            .append(Component.text("!", NamedTextColor.GRAY, TextDecoration.BOLD))
                            .append(Component.newline())
                            .append(Component.text("Jest to serwer stricte powiązany z serwerem Discord ", NamedTextColor.GRAY))
                            .append(Component.text("FF", NamedTextColor.GOLD, TextDecoration.BOLD))
                            .append(Component.text(".", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("Zachęcamy gorąco do połączenia konta Minecraft z kontem discord.", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("Aby to zrobić, proszę użyć komendy ", NamedTextColor.GRAY))
                            .append(Component.text("/kolor", NamedTextColor.GREEN))
                            .append(Component.text(", a następnie przesłać wygenerowany kod botowi ", NamedTextColor.GRAY))
                            .append(Component.text("μPP", NamedTextColor.GOLD))
                            .append(Component.text(" na Discord w wiadomości prywatnej.", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("Aby ponownie zsynchronizować role, użyj ponownie komendy ", NamedTextColor.GRAY))
                            .append(Component.text("/kolor", NamedTextColor.GREEN))
                            .append(Component.text(" (nie musisz wówczas ponownie wysyłać kodu botowi)", NamedTextColor.DARK_GRAY))
                            .append(Component.text(".", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("UWAGA!", NamedTextColor.RED))
                            .append(Component.text(" Czasem, aby poprawnie zadziałało zsynchronizowanie, trzeba wykonać komendę parę razy.", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("Na start otrzymujesz ", NamedTextColor.GRAY))
                            .append(Component.text(String.valueOf(STARTING_BALANCE), NamedTextColor.YELLOW))
                            .append(Component.text("μ", NamedTextColor.YELLOW))
                            .append(Component.text(".", NamedTextColor.GRAY))
                            .append(Component.newline())
                            .append(Component.text("Aby zyskać pomoc dotyczącą komend, użyj komendy ", NamedTextColor.GRAY))
                            .append(Component.text("/help", NamedTextColor.GREEN))
                            .append(Component.text(".", NamedTextColor.GRAY))
            );
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MicroppDsc.getInstance().getDiscordBridgeManager().sendToDsc(
                "<:f2_minus1:1429971281767436298> **" +  event.getPlayer().getName() + "** wyszedł z serwera!"
        );
    }
}
