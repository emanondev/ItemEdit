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

import java.util.List;

public class Sound extends SubCmd {
    public Sound(ItemFoodCommand itemFoodCommand) {
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
        ItemStack item = getItemInHand(player);
        org.bukkit.Sound value = Aliases.SOUND.convertAlias(args[1]);
        if (value == null) {
            onWrongAlias(sender, Aliases.SOUND);
            onFail(player, alias);
            return;
        }
        item = new ItemBuilder(item).setConsumeSound(value).build();
        setItemInHand(player, item);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], Aliases.SOUND) : null;
    }
}
