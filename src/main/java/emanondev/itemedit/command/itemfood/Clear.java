package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Clear extends SubCmd {
    public Clear(ItemFoodCommand itemFoodCommand) {
        super("clear", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemBuilder builder = new ItemBuilder(getItemInHand(player)).clearFoodComponent();
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            setItemInHand(player, builder.build());
        } else {
            builder.clearConsumableComponent();
            builder.build();
        }
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
