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

public class FireResistent extends SubCmd {

    public FireResistent(final ItemEditCommand cmd) {
        super("fireresistent", cmd, true, true);
    }

    //ie fireresistent <true/false>
    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length > 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            boolean value = args.length == 1 ? !meta.isFireResistant() : Aliases.BOOLEAN.convertAlias(args[1]);
            meta.setFireResistant(value);
            item.setItemMeta(meta);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.BOOLEAN);
        }
        return Collections.emptyList();
    }
}