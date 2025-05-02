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
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Buy extends SubCmd {

    private static Economy economy = null;

    public Buy(ServerItemCommand cmd) {
        super("buy", cmd, false, false);
        setupEconomy();
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
                .getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
        if (economy == null)
            throw new IllegalStateException();
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        try {
            // <id> <amount> <player> <price> [silent]
            if (args.length < 5 || args.length > 6) {
                throw new IllegalArgumentException("Wrong param number");
            }
            Boolean silent = args.length == 6 ? (Aliases.BOOLEAN.convertAlias(args[5])) : ((Boolean) false);
            if (silent == null)
                silent = Boolean.valueOf(args[5]);
            int amount = Integer.parseInt(args[2]);
            if (amount < 1)
                throw new IllegalArgumentException("Wrong amount number");
            ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
            Player target = Bukkit.getPlayer(args[3]);
            double price = Double.parseDouble(args[4]);
            if (price <= 0)
                throw new IllegalArgumentException();
            if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
                ItemMeta meta = ItemUtils.getMeta(item);
                meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%", target.getName(), "%player_uuid%", target.getUniqueId().toString()));
                item.setItemMeta(meta);
            }
            int removed = InventoryUtils.removeAmount(target, item, amount, InventoryUtils.LackMode.CANCEL);

            // have enough items?
            if (removed == 0) {
                // not enough items, aborting
                if (!silent)
                    this.sendLanguageString("not-enough-items", null, target, "%id%",
                            args[1].toLowerCase(), "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]),
                            "%amount%", String.valueOf(amount), "%price%", economy.format(price));
                return;
            }
            if (!economy.depositPlayer(target, price).transactionSuccess()) {
                // error
                InventoryUtils.giveAmount(target, item, amount, InventoryUtils.ExcessMode.DROP_EXCESS);
                if (!silent)
                    Util.sendMessage(target,
                            "&cAn error occurred, try again, if this message shows again try to contact the server administrators");
                Util.logToFile("[transaction failed] no errors, is your Economy provider stable?");
                return;
            }

            // success, giving some feedback
            if (!silent)
                this.sendLanguageString("feedback", null, target, "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%price%", economy.format(price));

            if (ItemEdit.get().getConfig().loadBoolean("log.action.buy", true)) {
                String msg = this.getConfigString("log", "%id%", args[1].toLowerCase(),
                        "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
                        String.valueOf(amount), "%player_name%", target.getName(), "%price%", economy.format(price));
                if (ItemEdit.get().getConfig().loadBoolean("log.console", true))
                    Util.sendMessage(Bukkit.getConsoleSender(), msg);
                if (ItemEdit.get().getConfig().loadBoolean("log.file", true))
                    Util.logToFile(msg);
            }
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        switch (args.length) {
            case 2:
                return CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds());
            case 3:
                return CompleteUtility.complete(args[2], Arrays.asList("1", "10", "64", "576", "2304"));
            case 4:
                return CompleteUtility.completePlayers(args[3]);
            case 5:
                return CompleteUtility.complete(args[2], Arrays.asList("10", "100", "1000", "10000"));
            case 6:
                return CompleteUtility.complete(args[4], Aliases.BOOLEAN);
        }
        return Collections.emptyList();
    }
}
