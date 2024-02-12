package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Collections;
import java.util.List;

public class BookAuthor extends SubCmd {

    public BookAuthor(ItemEditCommand cmd) {
        super("bookauthor", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        BookMeta itemMeta = (BookMeta) item.getItemMeta();

        if (args.length == 1) {
            itemMeta.setAuthor(null);
            item.setItemMeta(itemMeta);
            updateView(p);
            return;
        }

        try {
            StringBuilder name = new StringBuilder(args[1]);
            for (int i = 2; i < args.length; i++)
                name.append(" ").append(args[i]);
            itemMeta.setAuthor(UtilsString.fix( name.toString(),null, true));
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.completePlayers(args[1]);
        return Collections.emptyList();
    }
}