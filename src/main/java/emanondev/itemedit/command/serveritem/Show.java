package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.ShowServerItemsGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Show extends SubCmd {

    public Show(ServerItemCommand cmd) {
        super("show", cmd, true, false);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        try {
            int page = 1;
            if (args.length >= 2)
                page = Integer.parseInt(args[1]);
            p.openInventory(new ShowServerItemsGui(p, page).getInventory());
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
