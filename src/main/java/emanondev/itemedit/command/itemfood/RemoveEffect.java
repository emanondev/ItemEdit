package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
        //parse to int
        try {
            int line = Integer.parseInt(args[1])-1;


            ItemBuilder item = new ItemBuilder(getItemInHand(p));
            List<ConsumableEffect> effects = new ArrayList<>(item.getConsumeEffects());
            effects.remove(line);
            item.setConsumeEffects(effects);

            return;
        } catch (NumberFormatException ignored) {

        }

        //parse to type
        NamespacedKey type = Aliases.CONSUMABLE_EFFECT.convertAlias(args[1]);
        if (type == null) {
            onWrongAlias(p, Aliases.CONSUMABLE_EFFECT);
            onFail(p, alias);
            return;
        }
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        List<ConsumableEffect> effects = new ArrayList<>(item.getConsumeEffects());

        effects.removeIf(effect->{
            NamespacedKey type2 = Aliases.CONSUMABLE_EFFECT.convertInstance(effect);
            return  type.equals(type2);
        });
        item.setConsumeEffects(effects);
        setItemInHand(p,item.build());
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
