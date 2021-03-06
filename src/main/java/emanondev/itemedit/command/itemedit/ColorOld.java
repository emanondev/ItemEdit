package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collections;
import java.util.List;

public class ColorOld extends SubCmd {
    private final String leatherPerm;

    public ColorOld(ItemEditCommand cmd) {
        super("color", cmd, true, true);
        leatherPerm = getPermission() + ".leather";
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if ((item.getItemMeta() instanceof LeatherArmorMeta)) {
            if (!sender.hasPermission(leatherPerm)) {
                this.getCommand().sendPermissionLackMessage(leatherPerm, sender);
                return;
            }
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                Color color = Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                leatherMeta.setColor(color);
                item.setItemMeta(leatherMeta);
                p.updateInventory();
            } catch (Exception e) {
                onFail(p, alias);
            }
            return;
        }
        Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));

    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}