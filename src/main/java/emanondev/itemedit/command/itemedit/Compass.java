package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class Compass extends SubCmd {

    private static final String[] compassSub = new String[]{"clear", "set"};

    public Compass(ItemEditCommand cmd) {
        super("compass", cmd, true, true);
    }


    public void reload() {
        super.reload();
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof CompassMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }
        if (args.length == 1) {
            onFail(p);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "set":
                compassSet(p, item, args);
                return;
            case "clear":
                compassClear(p, item, args);
                return;
            default:
                onFail(p);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], compassSub);

        return Collections.emptyList();
    }

    // lore set line text
    private void compassSet(Player p, ItemStack item, String[] args) {
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.setLodestoneTracked(false);
        meta.setLodestone(p.getLocation());
        item.setItemMeta(meta);
        Util.sendMessage(p, this.getConfString("set.feedback").replace("%world%", p.getLocation().getWorld().getName())
                .replace("%x%", String.valueOf(p.getLocation().getBlockX()))
                .replace("%y%", String.valueOf(p.getLocation().getBlockY()))
                .replace("%z%", String.valueOf(p.getLocation().getBlockZ()))
        );
        p.updateInventory();
    }

    private void compassClear(Player p, ItemStack item, String[] args) {
        CompassMeta meta = (CompassMeta) item.getItemMeta();
        meta.setLodestoneTracked(true);
        meta.setLodestone(p.getLocation());
        item.setItemMeta(meta);
        Util.sendMessage(p, this.getConfString("clear.feedback"));
        p.updateInventory();
    }
}