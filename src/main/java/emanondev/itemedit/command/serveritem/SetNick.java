package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetNick extends SubCmd {

    public SetNick(final ServerItemCommand cmd) {
        super("setnick", cmd, false, false);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            if (args.length == 2) {
                ItemEdit.get().getServerStorage().setNick(args[1], null);
            }
            else {
                StringBuilder builder = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    builder.append(" ").append(args[i]);
                }
                ItemEdit.get().getServerStorage().setNick(args[1], builder.toString());
            }
            sendLanguageString("success", null, p, "%id%", args[1].toLowerCase(),
                    "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]));
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
        }
        return Collections.emptyList();
    }
}
