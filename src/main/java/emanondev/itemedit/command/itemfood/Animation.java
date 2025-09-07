package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.Keys;
import emanondev.itemedit.ParsedItem;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Animation extends SubCmd {
    public Animation(ItemFoodCommand itemFoodCommand) {
        super("animation", itemFoodCommand, true, true);
    }

    //if animation <animation>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 2) {
            onFail(player, alias);
            return;
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {

            String value = Aliases.ANIMATION_OLD.convertAlias(args[1]);
            if (value == null) {
                onWrongAlias(player, Aliases.ANIMATION);
                onFail(player, alias);
                return;
            }
            ParsedItem parsed = new ParsedItem(getItemInHand(player));
            parsed.set(value, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "animation");
            player.getInventory().setItemInMainHand(parsed.toItemStack());
            updateView(player);
            return;
        }
        ConsumableComponent.Animation value = Aliases.ANIMATION.convertAlias(args[1]);
        if (value == null) {
            onWrongAlias(player, Aliases.ANIMATION);
            onFail(player, alias);
        }
        new ItemBuilder(getItemInHand(player)).setConsumeAnimation(value).build();
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            return CompleteUtility.complete(args[1], Aliases.ANIMATION_OLD);
        }
        return CompleteUtility.complete(args[1], Aliases.ANIMATION);
    }
}
