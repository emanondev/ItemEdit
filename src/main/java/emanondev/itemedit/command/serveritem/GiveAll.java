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
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Locale;

@Slf4j
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
            String id = args[1].toLowerCase(Locale.ENGLISH);
            ItemStack template = ItemEdit.get().getServerStorage().getItem(id);
            if (template == null) {
                throw new IllegalArgumentException("Unknown server item");
            }
            boolean silentFeedback = silent;
            boolean dropExcess = ItemEdit.get().getConfig().loadBoolean("serveritem.give-drops-excess", true);
            boolean logAction = ItemEdit.get().getConfig().loadBoolean("log.action.giveall", true);
            boolean logConsole = ItemEdit.get().getConfig().loadBoolean("log.console", true);
            boolean logFile = ItemEdit.get().getConfig().loadBoolean("log.file", true);
            String logTemplate = this.getConfigString("log");
            String nick = ItemEdit.get().getServerStorage().getNick(id);

            SchedulerUtils.run(getPlugin(), () -> giveToOnlinePlayers(id, template, amount, silentFeedback,
                    dropExcess, logAction, logConsole, logFile, logTemplate, nick));
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    private void giveToOnlinePlayers(String id,
                                     ItemStack template,
                                     int amount,
                                     boolean silent,
                                     boolean dropExcess,
                                     boolean logAction,
                                     boolean logConsole,
                                     boolean logFile,
                                     String logTemplate,
                                     String nick) {
        List<Player> targets = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (targets.isEmpty()) {
            return;
        }

        AtomicInteger remaining = new AtomicInteger(targets.size());
        AtomicInteger totalGiven = new AtomicInteger();
        ConcurrentLinkedQueue<String> targetNames = new ConcurrentLinkedQueue<>();
        Runnable completed = () -> {
            if (remaining.decrementAndGet() == 0) {
                logGiveAll(id, amount, nick, totalGiven.get(), targetNames, logAction, logConsole, logFile, logTemplate);
            }
        };

        for (Player target : targets) {
            SchedulerUtils.run(getPlugin(), target, () -> {
                try {
                    ItemStack item = template.clone();
                    ItemUtils.updateTemplate(item, target);
                    int given = InventoryUtils.giveAmount(target, item, amount,
                            dropExcess ? InventoryUtils.ExcessMode.DROP_EXCESS : InventoryUtils.ExcessMode.DELETE_EXCESS);
                    totalGiven.addAndGet(given);
                    targetNames.add(target.getName());
                    if (given > 0) {
                        if (!silent) {
                            sendLanguageString("feedback", target,
                                    "%id%", id,
                                    "%nick%", nick,
                                    "%amount%", String.valueOf(given));
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to give server item '{}' to player {}", id, target.getName(), e);
                } finally {
                    completed.run();
                }
            }, completed);
        }
    }

    private void logGiveAll(String id,
                            int requestedAmount,
                            String nick,
                            int total,
                            ConcurrentLinkedQueue<String> targetNames,
                            boolean logAction,
                            boolean logConsole,
                            boolean logFile,
                            String logTemplate) {
        if (total <= 0 || !logAction) {
            return;
        }
        String targets = "[" + String.join(", ", targetNames) + "]";
        Component message = UtilsString.fix(logTemplate, null, true,
                "%id%", id,
                "%nick%", nick,
                "%amount%", requestedAmount + " (for a total of " + total + " given)",
                "%targets%", targets);
        if (logConsole) {
            SchedulerUtils.run(getPlugin(), () -> Util.sendComponent(Bukkit.getConsoleSender(), message));
        }
        if (logFile) {
            SchedulerUtils.runAsync(getPlugin(), () -> Util.logToFile(message));
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
