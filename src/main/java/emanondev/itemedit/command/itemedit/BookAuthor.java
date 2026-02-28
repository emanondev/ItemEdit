package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookAuthor extends SubCmd {

    public BookAuthor(ItemEditCommand cmd) {
        super("bookauthor", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (!(item.getType() == Material.WRITTEN_BOOK)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_written_book");
            return;
        }

        if (args.length == 1) {
            item.setBookAuthor(null).build();
            sendFeedback(p, "feedback-reset");
            updateView(p);
            return;
        }

        StringBuilder name = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            name.append(" ").append(args[i]);
        }
        item.setBookAuthor(UtilsString.fix(name.toString(), null, true)).build();
        updateView(p);
        onSuccess(p);
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.completePlayers(args[1]);
        }
        return List.of();
    }
}