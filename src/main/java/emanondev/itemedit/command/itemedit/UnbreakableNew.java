package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class UnbreakableNew extends SubCmd {

    public UnbreakableNew(ItemEditCommand cmd) {
        super("unbreakable", cmd, true, true);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length > 2)
                throw new IllegalArgumentException("Wrong param number");
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !meta.isUnbreakable());
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.BOOLEAN);
        return Collections.emptyList();
    }
}
