package emanondev.itemedit.command.itemstorage;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class Update extends SubCmd {

    public Update(ItemStorageCommand cmd) {
        super("update", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            if (ItemEdit.get().getPlayerStorage().getItem(p, args[1]) != null)
                ItemEdit.get().getPlayerStorage().setItem(p, args[1], this.getItemInHand(p).clone());
            else
                throw new IllegalArgumentException();
            sendLanguageString("success", null, p, "%id%",
                    args[1].toLowerCase());
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        if (args.length == 2)
            return Util.complete(args[1], ItemEdit.get().getPlayerStorage().getIds((Player) sender));
        return Collections.emptyList();
    }
}
