package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
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
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }

        if (!item.isMetaClass(Damageable.class)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_damageable");
            return;
        }
        try {
            int amount = Integer.parseInt(args[1]);//TODO check valid amount >0
            item.setMaxDamage(amount).build();
            onSuccess(p, "%value%", String.valueOf(amount));
        } catch (NumberFormatException e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2 || !(sender instanceof Player player)) {
            return List.of();
        }
        ItemStack item = getItemInHand(player);
        if (item.getType().getMaxDurability() <= 1) {
            return List.of();
        }
        int max = item.getType().getMaxDurability();
        return CompleteUtility.complete(args[1],
                "1", String.valueOf(max), String.valueOf(max / 2), String.valueOf(max * 2)
        );
    }

}
