package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RepairCost extends SubCmd {

    public RepairCost(ItemEditCommand cmd) {
        super("repaircost", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length > 2)
                throw new IllegalArgumentException("Wrong param number");
            if (!sender.hasPermission(this.getPermission() + ".without_durability") && item.getType().getMaxDurability() <= 1) {
                this.getCommand().sendPermissionLackMessage(this.getPermission() + ".without_durability", sender);
                return;
            }

            Repairable meta = (Repairable) item.getItemMeta();
            meta.setRepairCost(Integer.parseInt(args[1]));
            item.setItemMeta((ItemMeta) meta); //cast is required for old game version
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Arrays.asList("0", "1", "3", "7", "30", "40"));
        return Collections.emptyList();
    }

}
