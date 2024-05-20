package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class MaxDurability extends SubCmd {

    public MaxDurability(ItemEditCommand cmd) {
        super("maxdurability", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            ItemMeta meta = item.getItemMeta();
            if (!(meta instanceof Damageable)) {
                Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
                return;
            }
            int amount = Integer.parseInt(args[1]);
            Damageable damageable = (Damageable) meta;
            damageable.setMaxDamage(amount);
            item.setItemMeta(meta);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (sender instanceof Player) {
                ItemStack item = this.getItemInHand((Player) sender);
                if (item != null && item.getType().getMaxDurability() > 1) {
                    int max = item.getType().getMaxDurability();
                    return Util.complete(args[1], "1", String.valueOf(max), String.valueOf(max / 2), String.valueOf(max / 4), String.valueOf(max / 4 * 3));
                }
            }
        }
        return Collections.emptyList();
    }

}
