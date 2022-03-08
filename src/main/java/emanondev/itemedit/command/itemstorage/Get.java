package emanondev.itemedit.command.itemstorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;

public class Get extends SubCmd {

    public Get(ItemStorageCommand cmd) {
        super("get", cmd, true, false);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
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
            int given = UtilsInventory.giveAmount(p, item, amount, ExcessManage.DELETE_EXCESS);
            if (given == 0)
                Util.sendMessage(p,
                        this.getConfString("no-inventory-space"));
            else
                Util.sendMessage(p, UtilsString.fix(this.getConfString("success"), p, true, "%id%",
                        args[1].toLowerCase(), "%amount%", String.valueOf(given)));
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
            case 3:
                return Util.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
        }
        return Collections.emptyList();
    }

}
