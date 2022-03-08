package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.ChatColor;

public class SkullOwner extends SubCmd {

    public SkullOwner(ItemEditCommand cmd) {
        super("skullowner", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof SkullMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }
        if ((ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_12_R")) && (!(item.getDurability() == 3))) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }

        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        itemMeta.setOwner(null);

        if (args.length == 1) {

            item.setItemMeta(itemMeta);
            p.updateInventory();
            return;
        }
        try {
            String name = args[1];
            for (int i = 2; i < args.length; i++)
                name = name + " " + args[i];
            name = ChatColor.translateAlternateColorCodes('&', name);
            itemMeta.setOwner(name);
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p);
            return;
        }
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.completePlayers(args[1]);
        return Collections.emptyList();
    }

}
