package emanondev.itemedit.command.itemstorage;

import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.ShowPlayerItemsGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Show extends SubCmd {

    public Show(ItemStorageCommand cmd) {
        super("show", cmd, true, false);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        try {
            int page = 1;
            if (args.length >= 2)
                page = Integer.parseInt(args[1]);
            p.openInventory(new ShowPlayerItemsGui(p, page).getInventory());
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}
