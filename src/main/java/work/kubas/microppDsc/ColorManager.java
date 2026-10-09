package work.kubas.microppDsc;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ColorManager {

    private final LuckPerms luckPerms;

    public ColorManager(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
//        scoreboardInit();
    }

    /**
     * This method requires colors to be castable to NamedTextColor
     */
    @Deprecated
    private void scoreboardInit() {
        var groups = MicroppDsc.getInstance().getPluginConfig().getAllColorMappings();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (var entry : groups.entrySet()) {
            var group = entry.getKey();
            var color = entry.getValue();

            Team team = scoreboard.getTeam(group);
            if (team == null) {
                team = scoreboard.registerNewTeam(group);
                team.color((NamedTextColor) color);
                team.prefix(Component.text("", color));
            }

        }
    }

    public void updateNameColor(Player player) {
        var user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        List<String> groups = user.getInheritedGroups(user.getQueryOptions())
                .stream().map(Group::getName)
                .toList();


        List<TextColor> colors = new ArrayList<>();

        for (String group : groups) {
            var possiblyColor = MicroppDsc.getInstance().getPluginConfig().getColorForGroup(group);
            possiblyColor.ifPresent(colors::add);
        }

        if (colors.isEmpty())
                colors.add(NamedTextColor.WHITE);

        if  (colors.size() == 1) {
            player.displayName(Component.text(player.getName(), colors.getFirst()));
            player.playerListName(Component.text(player.getName(), colors.getFirst()));
        }
        else {
            StringBuilder gradient = new StringBuilder("<gradient");

            for (TextColor color : colors) {
                gradient
                        .append(":")
                        .append(color.asHexString());
            }

            gradient
                    .append(">")
                    .append(player.getName())
                    .append("</gradient>");

            player.displayName(MiniMessage.miniMessage().deserialize(gradient.toString()));
            player.playerListName(MiniMessage.miniMessage().deserialize(gradient.toString()));
        }
    }
}
