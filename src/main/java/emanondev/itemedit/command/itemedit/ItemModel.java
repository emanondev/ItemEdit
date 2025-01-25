package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ItemModel extends SubCmd {

    public ItemModel(ItemEditCommand cmd) {//1.21.2+
        super("itemmodel", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length == 1) {
                ItemMeta meta = ItemUtils.getMeta(item);
                meta.setItemModel(null);
                item.setItemMeta(meta);
                updateView(p);
                return;
            }
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            String[] rawKey = args[1].toLowerCase(Locale.ENGLISH).split(":");
            NamespacedKey key = rawKey.length == 1 ? new NamespacedKey(NamespacedKey.MINECRAFT, rawKey[0]) :
                    new NamespacedKey(rawKey[0], rawKey[1]);
            ItemMeta meta = ItemUtils.getMeta(item);
            meta.setItemModel(key);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Registry.ITEM.stream().collect(Collectors.toList()),
                    args[1].contains(":") ? (type) -> type.getKey().toString() : (type) -> type.getKey().getKey());
        return Collections.emptyList();
    }

}