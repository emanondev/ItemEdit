package emanondev.itemedit.command.itemstorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;

public class Delete extends SubCmd {

    public Delete(ItemStorageCommand cmd) {
        super("delete", cmd, true, false);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            if (ItemEdit.get().getPlayerStorage().getItem(p, args[1]) != null)
                ItemEdit.get().getPlayerStorage().remove(p, args[1]);
            else
                throw new IllegalArgumentException();
            Util.sendMessage(p, UtilsString.fix(this.getConfString("success"), p, true, "%id%",
                    args[1].toLowerCase()));
        } catch (Exception e) {
            onFail(p);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        switch (args.length) {
            case 2:
                return Util.complete(args[1], ItemEdit.get().getPlayerStorage().getIds((Player) sender));
        }
        return Collections.emptyList();
    }

}
