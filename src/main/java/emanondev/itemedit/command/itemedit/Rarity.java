package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class Rarity extends SubCmd {

    public Rarity(ItemEditCommand cmd) {
        super("rarity", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length > 2)
                throw new IllegalArgumentException("Wrong param number");
            ItemRarity rarity = args.length == 1 ? null : Aliases.RARITY.convertAlias(args[1]);
            if (rarity == null && args.length != 1) {
                onWrongAlias("wrong-rarity", p, Aliases.RARITY);
                onFail(p, alias);
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            meta.setRarity(rarity);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Aliases.RARITY);
        return Collections.emptyList();
    }

}
