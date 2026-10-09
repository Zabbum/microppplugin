package work.kubas.microppDsc.listeners;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import work.kubas.microppDsc.MicroppDsc;

public class AchievementListener implements Listener {

    @EventHandler
    public void onAchievement(PlayerAdvancementDoneEvent event) {
        var player = event.getPlayer();
        var title = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().displayName());

        if (!event.getAdvancement().getKey().getKey().startsWith("recipes/"))
            Bukkit.getScheduler().runTaskAsynchronously(MicroppDsc.getInstance(), () -> {
                MicroppDsc.getInstance().getDiscordBridgeManager().achievementToDsc(player, title);
            });
    }
}
