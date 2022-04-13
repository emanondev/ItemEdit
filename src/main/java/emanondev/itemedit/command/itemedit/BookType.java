package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Collections;
import java.util.List;

public class BookType extends SubCmd {

    public BookType(ItemEditCommand cmd) {
        super("booktype", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            BookMeta itemMeta = (BookMeta) item.getItemMeta();

            if (args.length == 1) {
                itemMeta.setGeneration(null);
                item.setItemMeta(itemMeta);
                p.updateInventory();
                return;
            }

            if (args.length != 2)
                throw new IllegalArgumentException();
            BookMeta.Generation type = Aliases.BOOK_TYPE.convertAlias(args[1]);
            if (type == null)
                throw new IllegalArgumentException();
            itemMeta.setGeneration(type);
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.BOOK_TYPE);
        return Collections.emptyList();
    }
}
