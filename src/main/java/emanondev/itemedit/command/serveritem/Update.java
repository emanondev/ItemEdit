package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Update extends SubCmd {

    public Update(ServerItemCommand cmd) {
        super("update", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            if (ItemEdit.get().getServerStorage().getItem(args[1]) != null) {
                ItemEdit.get().getServerStorage().setItem(args[1], this.getItemInHand(p).clone());
            } else {
                throw new IllegalArgumentException();
            }
            //TODO feedback
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
        }
        return Collections.emptyList();
    }

}
