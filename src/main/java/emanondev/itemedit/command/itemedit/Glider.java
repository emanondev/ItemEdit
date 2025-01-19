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

public class Glider extends SubCmd {

    public Glider(ItemEditCommand cmd) {
        super("glider", cmd, true, true);
    }

    //ie glider [true/false]
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length > 2)
                throw new IllegalArgumentException("Wrong param number");
            ItemMeta meta = ItemUtils.getMeta(item);
            boolean value = args.length == 1 ? !meta.isGlider() : Aliases.BOOLEAN.convertAlias(args[1]);
            meta.setEnchantmentGlintOverride(value);
            item.setItemMeta(meta);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.BOOLEAN);
        }
        return Collections.emptyList();
    }
}