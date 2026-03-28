package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        ItemUseAnimation value = Aliases.ANIMATION.convertAlias(args[1]);
        if (value == null) {
            onWrongAlias(player, Aliases.ANIMATION);
            onFail(player, alias);
        }
        new ItemBuilder(getItemInHand(player)).setConsumeAnimation(value).build();
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return CompleteUtility.complete(args[1], Aliases.ANIMATION);
    }
}
