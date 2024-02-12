package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class Hide extends SubCmd {

    public Hide(ItemEditCommand cmd) {
        super("hide", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if ((args.length != 3) && (args.length != 2))
                throw new IllegalArgumentException("Wrong param number");

            ItemMeta itemMeta = item.getItemMeta();
            ItemFlag flag = Aliases.FLAG_TYPE.convertAlias(args[1]);
            if (flag == null) {
                onWrongAlias("wrong-flag", p, Aliases.FLAG_TYPE);
                onFail(p, alias);
                return;
            }
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
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.FLAG_TYPE);
        if (args.length == 3)
            return Util.complete(args[2], Aliases.BOOLEAN);
        return Collections.emptyList();
    }

}