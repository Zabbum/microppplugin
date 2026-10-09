package work.kubas.microppDsc.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import work.kubas.microppDsc.MicroppDsc;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        var message = PlainTextComponentSerializer.plainText().serialize(e.deathMessage());

        Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
            MicroppDsc.getInstance().getDiscordBridgeManager().sendToDsc(
                    "<:kugo_o_jasny_chuj:1434277050558054461> **" +
                            e.getPlayer().getName() + "** " +
                            message.replaceFirst("^\\S+\\s*", "")
            );

        });
    }
}
