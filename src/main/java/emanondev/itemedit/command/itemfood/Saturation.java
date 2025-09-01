package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Saturation extends SubCmd {
    public Saturation(ItemFoodCommand itemFoodCommand) {
        super("saturation", itemFoodCommand, true, true);
    }

    //if saturation <amount>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 2) {
            onFail(player, alias);
            return;
        }
        ItemStack item = getItemInHand(player);
        float val = Float.parseFloat(args[1]);//TODO handle parsing fail? should be positive?
        item = new ItemBuilder(item).setSaturation(val).build();
        setItemInHand(player, item);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], "1", "20") : null;
    }
}
