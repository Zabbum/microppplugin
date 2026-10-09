package work.kubas.microppDsc.jda;

import net.dv8tion.jda.api.entities.Member;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.node.types.InheritanceNode;
import work.kubas.microppDsc.config.PluginConfig;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class RoleSyncEngine {

    private final LuckPerms luckPerms;
    private final PluginConfig mappingConfig;
    private final Logger log;

    public RoleSyncEngine(LuckPerms luckPerms, PluginConfig mappingConfig, Logger log) {
        this.luckPerms = luckPerms;
        this.mappingConfig = mappingConfig;
        this.log = log;
    }

    public void syncPlayer(UUID mcUuid, Member discordMember) {
        Set<Long> roleIds = new HashSet<>();
        discordMember.getRoles().forEach(role -> roleIds.add(role.getIdLong()));

        Set<String> expectedGroups = new HashSet<>();
        for (var entry : mappingConfig.getAllMappings().entrySet()) {
            if (roleIds.contains(entry.getKey())) {
                expectedGroups.add(entry.getValue());
            }
        }

        luckPerms.getUserManager().modifyUser(mcUuid, user -> {
            Set<String> allGroups = new HashSet<>(mappingConfig.getAllMappings().values());

            for (String group : allGroups) {
                boolean hasGroup = user.getNodes().stream()
                        .filter(node -> node instanceof InheritanceNode)
                        .map(node -> ((InheritanceNode) node).getGroupName())
                        .anyMatch(group::equalsIgnoreCase);

                boolean shouldHaveGroup = expectedGroups.contains(group);

                if (shouldHaveGroup && !hasGroup) {
                    user.data().add(InheritanceNode.builder(group).build());
                    log.info("Added group '" + group + "' to  player " + mcUuid);
                }
                else if (!shouldHaveGroup && hasGroup) {
                    user.data().remove(InheritanceNode.builder(group).build());
                    log.info("Removed group '" + group + "' from  player " + mcUuid);
                }
            }

        });

    }
}
