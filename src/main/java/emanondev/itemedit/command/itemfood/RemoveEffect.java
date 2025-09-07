package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RemoveEffect extends SubCmd {
    public RemoveEffect(ItemFoodCommand itemFoodCommand) {
        super("removeeffect", itemFoodCommand, true, true);
    }


    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        String type = Aliases.CONSUMABLE_EFFECT.convertAlias(args[1]);
        if (type == null) {
            onWrongAlias(p, Aliases.CONSUMABLE_EFFECT);
            onFail(p, alias);
            return;
        }
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        item.getConsumeEffects();
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
