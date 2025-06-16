package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SkullOwner extends SubCmd {

    public SkullOwner(ItemEditCommand cmd) {
        super("skullowner", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        ItemMeta rawMeta = ItemUtils.getMeta(item);
        if (!(rawMeta instanceof SkullMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }
        if (VersionUtils.isVersionUpTo(1, 12) && item.getDurability() != 3) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        SkullMeta meta = (SkullMeta) rawMeta;
        meta.setOwner(null);

        if (args.length == 1) {
            item.setItemMeta(meta);
            updateView(p);
            return;
        }
        try {
            StringBuilder name = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++) {
                name.append(" ").append(args[i]);
            }
            name = new StringBuilder(ChatColor.translateAlternateColorCodes('&', name.toString()));
            meta.setOwner(name.toString());
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.completePlayers(args[1]);
        }
        return Collections.emptyList();
    }

}
