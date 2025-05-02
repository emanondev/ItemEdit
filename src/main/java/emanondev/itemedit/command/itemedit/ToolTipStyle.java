package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ToolTipStyle extends SubCmd {

    public ToolTipStyle(ItemEditCommand cmd) {
        super("tooltipstyle", cmd, true, true);
    }

    //ie tooltipstyle <style/clear>
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");

            ItemMeta meta = ItemUtils.getMeta(item);

            String value = args[1].toLowerCase(Locale.ENGLISH);
            if (value.equals("clear")) {
                meta.setTooltipStyle(null);
                item.setItemMeta(meta);
                return;
            }
            String pre;
            String post;
            if (!value.contains(":")) {
                pre = NamespacedKey.MINECRAFT;
                post = value;
            } else {
                pre = value.split(":")[0];
                post = value.split(":")[1];
            }
            meta.setTooltipStyle(new NamespacedKey(pre, post));
            item.setItemMeta(meta);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], "clear");
        }
        return Collections.emptyList();
    }
}