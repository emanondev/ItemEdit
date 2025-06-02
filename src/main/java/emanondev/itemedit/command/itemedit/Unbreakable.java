package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class Unbreakable extends SubCmd {

    public Unbreakable(final ItemEditCommand cmd) {
        super("unbreakable", cmd, true, true);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length > 2) {
            onFail(p, alias);
            return;
        }
        try {
            ItemMeta meta = ItemUtils.getMeta(item);
            boolean value = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !ItemUtils.isUnbreakable(meta);
            ItemUtils.setUnbreakable(meta, value);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Aliases.BOOLEAN);
        return Collections.emptyList();
    }
}
