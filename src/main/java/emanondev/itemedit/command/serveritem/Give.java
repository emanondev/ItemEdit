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
import emanondev.itemedit.utility.SchedulerUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Give extends SubCmd {

    public Give(ServerItemCommand cmd) {
        super("give", cmd, false, false);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
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
            Player target = args.length >= 4 ? Bukkit.getPlayer(args[3]) : (Player) sender;
            if (target == null) {
                throw new IllegalArgumentException("Unknown player");
            }
            String id = args[1].toLowerCase(Locale.ENGLISH);
            ItemStack item = ItemEdit.get().getServerStorage().getItem(id);
            if (item == null) {
                throw new IllegalArgumentException("Unknown server item");
            }
            String nick = ItemEdit.get().getServerStorage().getNick(id);
            boolean silentFeedback = silent;
            SchedulerUtils.run(getPlugin(), target, () -> giveToTarget(sender, alias, target,
                    id, item, nick, amount, silentFeedback), () -> scheduleFail(sender, alias));
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
            case 2 -> CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3 -> CompleteUtility.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4 -> CompleteUtility.completePlayers(args[3]);
            case 5 -> CompleteUtility.complete(args[4], Aliases.BOOLEAN);
            default -> List.of();
        };
    }

    private void giveToTarget(CommandSender sender, String alias, Player target, String id, ItemStack item,
                              String nick, int amount, boolean silent) {
        try {
            ItemUtils.updateTemplate(item, target);
            int given = InventoryUtils.giveAmount(target, item, amount, ItemEdit.get().getConfig()
                    .loadBoolean("serveritem.give-drops-excess", true) ?
                    InventoryUtils.ExcessMode.DROP_EXCESS : InventoryUtils.ExcessMode.DELETE_EXCESS);
            if (given > 0 && !silent) {
                sendLanguageString("feedback", target,
                        "%id%", id,
                        "%nick%", nick,
                        "%amount%", String.valueOf(given));
            }

            if (given > 0 && ItemEdit.get().getConfig().loadBoolean("log.action.give", true)) {
                Component msg = UtilsString.fix(this.getConfigString("log"), null, true,
                        "%id%", id,
                        "%nick%", nick,
                        "%amount%", String.valueOf(given),
                        "%player_name%", target.getName());
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true)) {
                    Util.sendComponent(Bukkit.getConsoleSender(), msg);
                }
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true)) {
                    Util.logToFile(msg);
                }
            }
        } catch (Exception e) {
            scheduleFail(sender, alias);
        }
    }

}
