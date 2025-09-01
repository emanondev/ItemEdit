package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ClearEffects extends SubCmd {
    public ClearEffects(ItemFoodCommand itemFoodCommand) {
        super("cleareffects",itemFoodCommand,true,true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        new ItemBuilder(getItemInHand(player)).setConsumeEffects(null).build();

    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
