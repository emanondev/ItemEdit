package emanondev.itemedit.command.serveritem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.LackManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;

public class Take extends SubCmd {

    public Take(ServerItemCommand cmd) {
        super("take", cmd, false, false);
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        try {
            // <id> [amount] [player] [silent]
            if (args.length < 2 || args.length > 5) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 5 ? (Aliases.BOOLEAN.convertAlias(args[4])) : ((Boolean) false);
            if (silent == null)
                silent = Boolean.valueOf(args[4]);
            int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
            if (amount < 1)
                throw new IllegalArgumentException("Wrong amount number");
            ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
            Player target = args.length >= 4 ? Bukkit.getPlayer(args[3]) : (Player) sender;
            if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                item.setItemMeta(meta);
            }
            amount = UtilsInventory.removeAmount(target, item, amount, LackManage.REMOVE_MAX_POSSIBLE);
            if (!silent)
                Util.sendMessage(target,
                        UtilsString.fix(this.getConfString("feedback"), target, true, "%id%", args[1].toLowerCase(),
                                "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                                String.valueOf(amount)));

            if (ItemEdit.get().getConfig().loadBoolean("log.action.take", true)) {
                String msg = UtilsString.fix(this.getConfString("log"), target, true, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%player_name%", target.getName());
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true))
                    Util.sendMessage(Bukkit.getConsoleSender(), msg);
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true))
                    Util.logToFile(msg);
            }
        } catch (Exception e) {
            onFail(sender);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        switch (args.length) {
            case 2:
                return Util.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3:
                return Util.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4:
                return Util.completePlayers(args[3]);
            case 5:
                return Util.complete(args[4], Aliases.BOOLEAN);
        }
        return Collections.emptyList();
    }

}
