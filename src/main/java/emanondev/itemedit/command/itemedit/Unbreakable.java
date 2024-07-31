package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilLegacy;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class Unbreakable extends SubCmd {

    public Unbreakable(ItemEditCommand cmd) {
        super("unbreakable", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length > 2) {
            onFail(p, alias);
            return;
        }
        try {
            ItemMeta meta = item.getItemMeta();
            boolean value = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !UtilLegacy.isUnbreakable(meta);
            meta = UtilLegacy.setUnbreakable(meta, value);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.BOOLEAN);
        return Collections.emptyList();
    }
}
