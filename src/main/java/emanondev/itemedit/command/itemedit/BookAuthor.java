package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.ChatColor;

public class BookAuthor extends SubCmd {

    public BookAuthor(ItemEditCommand cmd) {
        super("bookauthor", cmd, true, true);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }

        BookMeta itemMeta = (BookMeta) item.getItemMeta();

        if (args.length == 1) {
            itemMeta.setAuthor(null);
            item.setItemMeta(itemMeta);
            p.updateInventory();
            return;
        }

        try {
            String name = args[1];
            for (int i = 2; i < args.length; i++)
                name = name + " " + args[i];
            name = ChatColor.translateAlternateColorCodes('&', name);
            itemMeta.setAuthor(name);
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p);
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