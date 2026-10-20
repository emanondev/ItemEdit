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
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BuyMax extends SubCmd {

    private static Economy economy = null;

    public BuyMax(ServerItemCommand cmd) {
        super("buymax", cmd, false, false);
        setupEconomy();
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        try {
            // <id> <amount> <player> <price> [silent]
            if (args.length < 5 || args.length > 6) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 6 ? (Aliases.BOOLEAN.convertAlias(args[5])) : ((Boolean) false);
            if (silent == null) {
                silent = Boolean.valueOf(args[5]);
            }
            int amount = Integer.parseInt(args[2]);
            if (amount < 1) {
                throw new IllegalArgumentException("Wrong amount number");
            }
            Player target = Bukkit.getPlayer(args[3]);
            if (target == null) {
                throw new IllegalArgumentException("Unknown player");
            }
            String id = args[1].toLowerCase(Locale.ENGLISH);
            ItemStack item = ItemEdit.get().getServerStorage().getItem(id);
            if (item == null) {
                throw new IllegalArgumentException("Unknown server item");
            }
            double price = Double.parseDouble(args[4]);
            if (!Double.isFinite(price) || price <= 0) {
                throw new IllegalArgumentException("Price must be positive");
            }
            String nick = ItemEdit.get().getServerStorage().getNick(id);
            boolean silentFeedback = silent;
            SchedulerUtils.run(getPlugin(), target, () -> buyMaxFromTarget(sender, alias, target, id, item, nick, amount,
                    silentFeedback, price), () -> scheduleFail(sender, alias));
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
            case 5 -> CompleteUtility.complete(args[2], Arrays.asList("10", "100", "1000", "10000"));
            case 6 -> CompleteUtility.complete(args[4], Aliases.BOOLEAN);
            default -> List.of();
        };
    }

    private void buyMaxFromTarget(CommandSender sender, String alias, Player target, String id, ItemStack item,
                                  String nick, int amount,
                                  boolean silent, double price) {
        try {
            ItemUtils.updateTemplate(item, target);
            int removed = InventoryUtils.removeAmount(target, item, amount, InventoryUtils.LackMode.REMOVE_MAX_POSSIBLE);

            if (removed == 0) {
                if (!silent) {
                    sendLanguageString("not-enough-items", target, "%id%", id,
                            "%nick%", nick,
                            "%amount%", String.valueOf(amount), "%price%", economy.format(price));
                }
                return;
            }
            if (removed < amount) {
                double pricePerItem = price / amount;
                price = pricePerItem * removed;
                amount = removed;
            }
            if (!economy.depositPlayer(target, price).transactionSuccess()) {
                InventoryUtils.giveAmount(target, item, amount, InventoryUtils.ExcessMode.DROP_EXCESS);
                if (!silent) {
                    Util.sendComponent(target, Component.text(
                            "An error occurred, try again, if this message shows again try to contact the server administrators",
                            NamedTextColor.RED));
                }
                Util.logToFile("[transaction failed] no errors, is your Economy provider stable?");
                return;
            }

            if (!silent) {
                sendLanguageString("feedback", target, "%id%", id,
                        "%nick%", nick, "%amount%",
                        String.valueOf(amount), "%price%", economy.format(price));
            }

            if (ItemEdit.get().getConfig().loadBoolean("log.action.buy", true)) {
                Component msg = UtilsString.fix(this.getConfigString("log"), null, true, "%id%", id,
                        "%nick%", nick, "%amount%",
                        String.valueOf(amount), "%player_name%", target.getName(), "%price%", economy.format(price));
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

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        if (economy == null) {
            throw new IllegalStateException();
        }
    }
}
