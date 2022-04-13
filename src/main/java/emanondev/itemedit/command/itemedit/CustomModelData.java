package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CustomModelData extends SubCmd {

    public CustomModelData(ItemEditCommand cmd) {//1.14+
        super("custommodeldata", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            int amount = Integer.parseInt(args[1]);
            if (amount < 0)
                throw new IllegalArgumentException("Wrong model value");
            ItemMeta meta = item.getItemMeta();
            meta.setCustomModelData(amount);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        return Collections.emptyList();
    }

}