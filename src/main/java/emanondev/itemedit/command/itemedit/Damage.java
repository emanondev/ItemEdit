package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Damage extends SubCmd {

    public Damage(ItemEditCommand cmd) {
        super("damage", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            short amount = Short.parseShort(args[1]);
            amount = (short) Math.max(0, Math.min(amount, item.getType().getMaxDurability()));
            item.setDurability(amount);
            updateView(p);
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
                    return CompleteUtility.complete(args[1], "0", String.valueOf(max), String.valueOf(max / 2), String.valueOf(max / 4), String.valueOf(max / 4 * 3));
                }
            }
        }
        return Collections.emptyList();
    }

}
