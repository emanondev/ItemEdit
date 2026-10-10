package emanondev.itemedit.command.serveritem;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.InventoryUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GiveAll extends SubCmd {

    public GiveAll(ServerItemCommand cmd) {
        super("giveall", cmd, false, false);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        try {
            if (Bukkit.getOnlinePlayers().isEmpty()) {
                return;
            }
            // <id> [amount] [silent]
            if (args.length < 2 || args.length > 4) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 4 ? (Aliases.BOOLEAN.convertAlias(args[3])) : ((Boolean) false);
            if (silent == null) {
                silent = Boolean.valueOf(args[3]);
            }
            int amount = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
            if (amount < 1) {
                throw new IllegalArgumentException("Wrong amount number");
            }
            if (ItemEdit.get().getServerStorage().getItem(args[1]) == null) {
                throw new IllegalArgumentException("Unknown server item");
            }
            int total = 0;
            for (Player target : Bukkit.getOnlinePlayers()) {
                ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1], target);
                int given = InventoryUtils.giveAmount(target, item, amount, ItemEdit.get().getConfig()
                        .loadBoolean("serveritem.give-drops-excess", true) ?
                        InventoryUtils.ExcessMode.DROP_EXCESS : InventoryUtils.ExcessMode.DELETE_EXCESS);
                total += given;
                if (given > 0 && !silent) {
                    sendLanguageString("feedback", target, "%id%", args[1].toLowerCase(),
                            "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                            String.valueOf(given));
                }
            }

            if (total > 0 && ItemEdit.get().getConfig().loadBoolean("log.action.giveall", true)) {
                StringBuilder sb = new StringBuilder("[");
                for (Player target : Bukkit.getOnlinePlayers()) {
                    sb.append(target.getName()).append(", ");
                }

                Component msg = UtilsString.fix2(this.getConfigString("log"), null, true, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        amount + " (for a total of " + total + " given)", "%targets%", sb.delete(sb.length() - 2, sb.length()).append("]").toString());
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true)) {
                    Util.sendMessage2(Bukkit.getConsoleSender(), msg);
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
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return List.of();
        }
        return switch (args.length) {
            // <id> [amount] [silent]
            case 2 -> CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3 -> CompleteUtility.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4 -> CompleteUtility.complete(args[3], Aliases.BOOLEAN);
            default -> List.of();
        };
    }

}
