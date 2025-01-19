package emanondev.itemedit.command.itemstorage;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.InventoryUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Get extends SubCmd {

    public Get(ItemStorageCommand cmd) {
        super("get", cmd, true, false);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        try {
            if (args.length != 2 && args.length != 3)
                throw new IllegalArgumentException("Wrong param number");
            int amount = 1;
            if (args.length == 3)
                amount = Integer.parseInt(args[2]);
            if (amount < 1)
                throw new IllegalArgumentException("Wrong amount number");
            ItemStack item = ItemEdit.get().getPlayerStorage().getItem(p, args[1]);
            int given = InventoryUtils.giveAmount(p, item, amount, InventoryUtils.ExcessMode.DELETE_EXCESS);
            if (given == 0)
                sendLanguageString("no-inventory-space", null, p);
            else
                sendLanguageString("success", null, p, "%id%",
                        args[1].toLowerCase(Locale.ENGLISH), "%amount%", String.valueOf(given));
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();
        switch (args.length) {
            case 2:
                return Util.complete(args[1], ItemEdit.get().getPlayerStorage().getIds((Player) sender));
            case 3:
                return Util.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
        }
        return Collections.emptyList();
    }

}
