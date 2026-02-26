package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Nutrition extends SubCmd {
    public Nutrition(ItemFoodCommand itemFoodCommand) {
        super("nutrition", itemFoodCommand, true, true);
    }

    //if nutrition <amount>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 2) {
            onFail(player, alias);
            return;
        }
        int val = Integer.parseInt(args[1]);//TODO handle parsing fail? should be positive?
        setItemInHand(player, new ItemBuilder(getItemInHand(player)).setNutrition(val).build());
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], "1", "20") : List.of();
    }
}
