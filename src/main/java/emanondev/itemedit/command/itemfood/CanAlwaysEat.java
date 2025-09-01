package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CanAlwaysEat extends SubCmd {
    public CanAlwaysEat(ItemFoodCommand itemFoodCommand) {
        super("canalwayseat", itemFoodCommand, true, true);
    }

    //if canalwayseat [value=toggle]
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (args.length != 1 && args.length != 2) {
            onFail(player, alias);
            return;
        }
        ItemBuilder itemBuilder = new ItemBuilder(item);
        Boolean value = args.length == 1 ? (Boolean) !itemBuilder.canAlwaysEat() : Aliases.BOOLEAN.convertAlias(args[1]);
        if (value == null) {
            onWrongAlias(player, Aliases.BOOLEAN);
            onFail(player, alias);
            return;
        }
        item = itemBuilder.setCanAlwaysEat(value).build();
        setItemInHand(player, item);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], Aliases.BOOLEAN) : Collections.emptyList();
    }
}
