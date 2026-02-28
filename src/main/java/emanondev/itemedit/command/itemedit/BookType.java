package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class BookType extends SubCmd {

    public BookType(@NotNull final ItemEditCommand cmd) {
        super("booktype", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {

        Player p = (Player) sender;

        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (item.getType() != Material.WRITTEN_BOOK) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_written_book");
            return;
        }

        // Reset generation if only the command is provided
        if (args.length == 1) {
            item.setBookGeneration(null).build();
            sendFeedback(p, "feedback-reset");
            updateView(p);
            return;
        }

        // Only accept exactly 2 arguments beyond the command
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }

        // Convert alias to BookMeta.Generation
        BookMeta.Generation type = Aliases.BOOK_TYPE.convertAlias(args[1]);
        if (type == null) {
            onWrongAlias(p, Aliases.BOOK_TYPE);
            onFail(p, alias);
            return;
        }

        // Apply the generation type and update the view
        item.setBookGeneration(type).build();
        onSuccess(p, "%generation%", args[1].toLowerCase(Locale.ENGLISH));
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.BOOK_TYPE);
        }
        return List.of();
    }
}
