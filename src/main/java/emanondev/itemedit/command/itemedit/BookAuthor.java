package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class BookAuthor extends SubCmd {

    public BookAuthor(ItemEditCommand cmd) {
        super("bookauthor", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        BookMeta meta = (BookMeta) ItemUtils.getMeta(item);

        if (args.length == 1) {
            meta.setAuthor(null);
            item.setItemMeta(meta);
            updateView(p);
            return;
        }

        try {
            StringBuilder name = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++) {
                name.append(" ").append(args[i]);
            }
            meta.setAuthor(UtilsString.fix(name.toString(), null, true));
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