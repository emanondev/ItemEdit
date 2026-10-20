package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class SetNick extends SubCmd {

    public SetNick(ServerItemCommand cmd) {
        super("setnick", cmd, false, false);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            String id = args[1].toLowerCase(Locale.ENGLISH);
            if (args.length == 2) {
                ItemEdit.get().getServerStorage().setNick(id, null);
            } else {
                StringBuilder builder = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    builder.append(" ").append(args[i]);
                }
                ItemEdit.get().getServerStorage().setNick(id, builder.toString());
            }
            sendLanguageString("success", p,
                    "%id%", id,
                    "%nick%", ItemEdit.get().getServerStorage().getNick(id));
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
        }
        return List.of();
    }
}
