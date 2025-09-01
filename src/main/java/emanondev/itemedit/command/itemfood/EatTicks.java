package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.Keys;
import emanondev.itemedit.ParsedItem;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EatTicks extends SubCmd {
    public EatTicks(ItemFoodCommand itemFoodCommand) {
        super("eatticks", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 2) {
            sendFailFeedbackForSub(player, alias, "eatticks");
            return;
        }

        int val = Integer.parseInt(args[1]);
        if (val < 0) {
            val = 0;
        }

        ItemStack item = new ItemBuilder(getItemInHand(player)).setConsumeSeconds(val / 20F).build();
        setItemInHand(player, item);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], "1","20","32") : Collections.emptyList();
    }
}
