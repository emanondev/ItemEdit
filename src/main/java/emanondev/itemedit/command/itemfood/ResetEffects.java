package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class ResetEffects extends SubCmd {
    public ResetEffects(ItemFoodCommand itemFoodCommand) {
        super("reseteffects", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        new ItemBuilder(getItemInHand(player)).setConsumeEffects(null).build();
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        int size = new ItemBuilder(getItemInHand((Player) sender)).getConsumeEffects().size();
        return CompleteUtility.complete(args[args.length - 1], IntStream.rangeClosed(1, size)
                .mapToObj(String::valueOf).toList());
    }
}
