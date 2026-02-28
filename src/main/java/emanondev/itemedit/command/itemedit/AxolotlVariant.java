package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class AxolotlVariant extends SubCmd {

    public AxolotlVariant(ItemEditCommand cmd) {
        super("axolotlvariant", cmd, true, true);
    }

    public void reload() {
        super.reload();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (!(item.isMetaClass(AxolotlBucketMeta.class))) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_axolotl_bucket");
            return;
        }

        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        Axolotl.Variant type = Aliases.AXOLOTL_VARIANT.convertAlias(args[1]);
        if (type == null) {
            onWrongAlias(p, Aliases.AXOLOTL_VARIANT);
            onFail(p, alias);
            return;
        }
        item.setAxolotlVariant(type).build();
        onSuccess(p, "%variant%", args[1].toLowerCase(Locale.ENGLISH));
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.AXOLOTL_VARIANT);
        }
        return List.of();
    }
}