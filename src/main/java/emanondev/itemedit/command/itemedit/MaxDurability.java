package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.IntParser;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MaxDurability extends SubCmd {

    public MaxDurability(ItemEditCommand cmd) {
        super("maxdurability", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInMainHand(p));
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        if (!item.isMetaClass(Damageable.class)) {
            getPlugin().getTranslator().send(p, "generic.wrong-material.damageable");
            return;
        }
        IntParser amount = new IntParser(args[1]);
        if (!amount.isNumberMin(1)) {
            onFail(p, alias);
            return;
        }
        item.setMaxDamage(amount.getValue()).build();
        onSuccess(p, "%value%", String.valueOf(amount.getValue()));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2 || !(sender instanceof Player player)) {
            return List.of();
        }
        ItemStack item = getItemInMainHand(player);
        if (item.getType().getMaxDurability() <= 1) {
            return List.of();
        }
        int max = item.getType().getMaxDurability();
        return CompleteUtility.complete(args[1],
                "1", String.valueOf(max), String.valueOf(max / 2), String.valueOf(max * 2)
        );
    }

}
