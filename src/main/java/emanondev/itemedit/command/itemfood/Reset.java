package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Reset extends SubCmd {
    public Reset(ItemFoodCommand itemFoodCommand) {
        super("reset", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemBuilder builder = new ItemBuilder(getItemInHand(player)).clearFoodComponent();
        if (VersionUtils.isAfter(1, 21, 4)) {
            builder.clearConsumableComponent();
        }
        builder.build();
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
