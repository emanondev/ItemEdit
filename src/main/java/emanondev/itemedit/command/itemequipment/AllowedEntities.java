package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.TagContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedEntities extends SubCmd {
    public AllowedEntities(ItemEquipmentCommand command) {
        super("allowedentities", command, true, true);
    }

    //if nutrition <amount>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length == 1) {
            item.setEquippableAllowedEntities((EntityType) null).build();
            sendFeedback(p, "feedback-reset");
            return;
        }

        Set<EntityType> types = new HashSet<>();
        for (String arg : Arrays.copyOfRange(args, 1, args.length)) {
            EntityType entity = Aliases.ENTITY_TYPE.convertAlias(arg);
            if (entity != null && entity.isAlive()) {
                types.add(entity);
                continue;
            }
            TagContainer<EntityType> tag = Aliases.ENTITY_GROUPS.convertAlias(arg);
            if (tag != null && tag.getValues().stream().anyMatch(EntityType::isAlive)) {
                types.addAll(tag.getValues().stream().filter(EntityType::isAlive).toList());
                continue;
            }
            onWrongAlias(p, Aliases.ENTITY_TYPE);
            onWrongAlias(p, Aliases.ENTITY_GROUPS);
            sendFeedback(p, "invalid-type",
                    "%value%", arg);
            return;
        }
        item.setEquippableAllowedEntities(types).build();
        onSuccess(p, "%value%", types.stream().map(Enum::name).collect(Collectors.joining(", ")));

    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        List<String> res = CompleteUtility.complete(args[args.length - 1], Aliases.ENTITY_TYPE, EntityType::isAlive);
        res.addAll(CompleteUtility.complete(args[args.length - 1], Aliases.ENTITY_GROUPS,
                tag -> tag.getValues().stream().anyMatch(EntityType::isAlive)));
        return res;
    }
}
