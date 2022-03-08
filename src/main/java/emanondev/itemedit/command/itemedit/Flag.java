package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class Flag extends SubCmd {

    public Flag(ItemEditCommand cmd) {
        super("hide", cmd, true, true);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if ((args.length != 3) && (args.length != 2))
                throw new IllegalArgumentException("Wrong param number");

            ItemMeta itemMeta = item.getItemMeta();
            ItemFlag flag = Aliases.FLAG_TYPE.convertAlias(args[1]);
            if (flag == null)
                throw new IllegalArgumentException("Wrong flag name");
            if (args.length == 3) {
                if (Aliases.BOOLEAN.convertAlias(args[2]))
                    itemMeta.addItemFlags(flag);
                else
                    itemMeta.removeItemFlags(flag);
            } else {
                if (itemMeta.hasItemFlag(flag))
                    itemMeta.removeItemFlags(flag);
                else
                    itemMeta.addItemFlags(flag);
            }

            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p);
        }

    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.FLAG_TYPE);
        if (args.length == 3)
            return Util.complete(args[2], Aliases.BOOLEAN);
        return Collections.emptyList();
    }

}