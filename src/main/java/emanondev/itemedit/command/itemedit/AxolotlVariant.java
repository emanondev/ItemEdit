package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;

import java.util.Collections;
import java.util.List;

public class AxolotlVariant extends SubCmd {

    public AxolotlVariant(ItemEditCommand cmd) {
        super("axolotlvariant",
                cmd, true, true);
    }

    public void reload() {
        super.reload();
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof AxolotlBucketMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            AxolotlBucketMeta meta = (AxolotlBucketMeta) ItemUtils.getMeta(item);
            Axolotl.Variant type = Aliases.AXOLOTL_VARIANT.convertAlias(args[1]);
            if (type == null) {
                onWrongAlias("wrong-axolotl", p, Aliases.AXOLOTL_VARIANT);
                onFail(p, alias);
                return;
            }
            meta.setVariant(type);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Aliases.AXOLOTL_VARIANT);
        return Collections.emptyList();
    }
}