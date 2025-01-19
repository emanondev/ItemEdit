package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Delete extends SubCmd {

    public Delete(ServerItemCommand cmd) {
        super("delete", cmd, false, false);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            if (ItemEdit.get().getServerStorage().getItem(args[1]) != null)
                ItemEdit.get().getServerStorage().remove(args[1]);
            else
                throw new IllegalArgumentException();
            //TODO feedback
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        if (args.length == 2)
            return CompleteUtility.complete(args[1], ItemEdit.get().getPlayerStorage().getIds((Player) sender));
        return Collections.emptyList();
    }

}
