package emanondev.itemedit.command.itemstorage;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Delete extends SubCmd {

    public Delete(final ItemStorageCommand cmd) {
        super("delete", cmd, true, false);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            if (ItemEdit.get().getPlayerStorage().getItem(p, args[1]) != null)
                ItemEdit.get().getPlayerStorage().remove(p, args[1]);
            else
                throw new IllegalArgumentException();
            sendLanguageString("success", null, p, "%id%",
                    args[1].toLowerCase(Locale.ENGLISH));
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        if (args.length == 2)
            return CompleteUtility.complete(args[1], ItemEdit.get().getPlayerStorage().getIds((Player) sender));
        return Collections.emptyList();
    }

}
