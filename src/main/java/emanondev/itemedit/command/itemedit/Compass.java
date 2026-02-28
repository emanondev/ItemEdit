package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.NotNull;

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
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (!item.isMetaClass(CompassMeta.class)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_compass");
            return;
        }

        if (args.length < 2) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "set" -> compassSet(p, item, args);
            case "clear" -> compassClear(p, item, args);
            default -> onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], compassSub);
        }
        return List.of();
    }

    // lore set line text
    private void compassSet(Player p, ItemBuilder item, String[] args) {
        item.setCompassLodestone(false, p.getLocation()).build();
        onSubSuccess(p, "set",
                "%world%", p.getLocation().getWorld().getName(),
                "%x%", String.valueOf(p.getLocation().getBlockX()),
                "%y%", String.valueOf(p.getLocation().getBlockY()),
                "%z%", String.valueOf(p.getLocation().getBlockZ())
        );
        updateView(p);
    }

    private void compassClear(Player p, ItemBuilder item, String[] args) {
        item.setCompassLodestone(false, p.getLocation()).build();
        onSubSuccess(p, "clear");
        updateView(p);
    }
}