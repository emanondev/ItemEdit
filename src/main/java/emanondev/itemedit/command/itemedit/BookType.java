package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
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

public class BookType extends SubCmd {

    public BookType(@NotNull final ItemEditCommand cmd) {
        super("booktype", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull final CommandSender sender,
                          @NotNull final String alias,
                          final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            BookMeta itemMeta = (BookMeta) ItemUtils.getMeta(item);

            if (args.length == 1) {
                itemMeta.setGeneration(null);
                item.setItemMeta(itemMeta);
                updateView(p);
                return;
            }

            if (args.length != 2)
                throw new IllegalArgumentException();
            BookMeta.Generation type = Aliases.BOOK_TYPE.convertAlias(args[1]);
            if (type == null) {
                onWrongAlias("wrong-generation", p, Aliases.BOOK_TYPE);
                onFail(p, alias);
                return;
            }
            itemMeta.setGeneration(type);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(@NotNull final CommandSender sender,
                                   final String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Aliases.BOOK_TYPE);
        return Collections.emptyList();
    }
}
