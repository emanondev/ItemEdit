package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Take extends SubCmd {

    public Take(final ServerItemCommand cmd) {
        super("take", cmd, false, false);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        try {
            // <id> [amount] [player] [silent]
            if (args.length < 2 || args.length > 5) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 5 ? (Aliases.BOOLEAN.convertAlias(args[4])) : ((Boolean) false);
            if (silent == null) {
                silent = Boolean.valueOf(args[4]);
            }
            int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
            if (amount < 1) {
                throw new IllegalArgumentException("Wrong amount number");
            }
            ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
            Player target = args.length >= 4 ? Bukkit.getPlayer(args[3]) : (Player) sender;
            if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
                ItemMeta meta = ItemUtils.getMeta(item);
                meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                item.setItemMeta(meta);
            }
            amount = InventoryUtils.removeAmount(target, item, amount, InventoryUtils.LackMode.REMOVE_MAX_POSSIBLE);
            if (!silent) {
                sendLanguageString("feedback", null, target, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount));
            }

            if (ItemEdit.get().getConfig().loadBoolean("log.action.take", true)) {
                String msg = UtilsString.fix(this.getConfigString("log"), target, true, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%player_name%", target.getName());
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true)) {
                    Util.sendMessage(Bukkit.getConsoleSender(), msg);
                }
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true)) {
                    Util.logToFile(msg);
                }
            }
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3 -> CompleteUtility.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4 -> CompleteUtility.completePlayers(args[3]);
            case 5 -> CompleteUtility.complete(args[4], Aliases.BOOLEAN);
            default -> Collections.emptyList();
        };
    }

}
