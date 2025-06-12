package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Compass extends SubCmd {

    private static final String[] compassSub = new String[]{"clear", "set"};

    public Compass(ItemEditCommand cmd) {
        super("compass", cmd, true, true);
    }


    public void reload() {
        super.reload();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof CompassMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "set":
                compassSet(p, item, args);
                return;
            case "clear":
                compassClear(p, item, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], compassSub);
        return Collections.emptyList();
    }

    // lore set line text
    private void compassSet(Player p, ItemStack item, String[] args) {
        CompassMeta meta = (CompassMeta) ItemUtils.getMeta(item);
        meta.setLodestoneTracked(false);
        meta.setLodestone(p.getLocation());
        item.setItemMeta(meta);
        this.getLanguageString("set.feedback", null, p,
                "%world%", p.getLocation().getWorld().getName(),
                "%x%", String.valueOf(p.getLocation().getBlockX()),
                "%y%", String.valueOf(p.getLocation().getBlockY()),
                "%z%", String.valueOf(p.getLocation().getBlockZ())
        );
        updateView(p);
    }

    private void compassClear(Player p, ItemStack item, String[] args) {
        CompassMeta meta = (CompassMeta) ItemUtils.getMeta(item);
        meta.setLodestoneTracked(true);
        meta.setLodestone(p.getLocation());
        item.setItemMeta(meta);
        Util.sendMessage(p, this.getLanguageString("clear.feedback", null, p));
        updateView(p);
    }
}