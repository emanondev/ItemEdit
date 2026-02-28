package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
        int line = Integer.parseInt(args[1]) - 1;

        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        List<ConsumableEffect> effects = new ArrayList<>(item.getConsumeEffects());
        effects.remove(line);
        item.setConsumeEffects(effects).build();
        onSuccess(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
