package emanondev.itemedit.command.serveritem;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;
import net.milkbowl.vault.economy.Economy;

public class Sell extends SubCmd {

	private static Economy economy = null;

	private void setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}
		if (economy == null)
			throw new IllegalStateException();
	}

	public Sell(ServerItemCommand cmd) {
		super("sell", cmd, false, false);
		setupEconomy();
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		try {
			// <id> <amount> <player> <price> [silent]
			if (args.length < 5 || args.length > 6) {
				throw new IllegalArgumentException("Wrong param number");
			}
			Boolean silent = args.length == 6 ? (Aliases.BOOLEAN.convertAlias(args[5])) : ((Boolean) false);
			if (silent == null)
				Boolean.valueOf(args[5]);
			int amount = Integer.parseInt(args[2]);
			if (amount < 1)
				throw new IllegalArgumentException("Wrong amount number");
			ItemStack item = ItemEdit.get().getServerStorage().getItem(args[1]);
			Player target = args.length >= 4 ? Bukkit.getPlayer(args[3]) : (Player) sender;
			double price = Double.parseDouble(args[4]);
			if (price <= 0)
				throw new IllegalArgumentException();
			if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders",true)) {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%",target.getName(),"%player_uuid%",target.getUniqueId().toString()));
				meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%",target.getName(),"%player_uuid%",target.getUniqueId().toString()));
				item.setItemMeta(meta);
			}
			// have enough money?
			if (!economy.has(target, price)) {
				// not enough money, aborting
				if (!silent)
					Util.sendMessage(target,
							UtilsString.fix(this.getConfString("not-enough-money"), target, true, "%id%",
									args[1].toLowerCase(), "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]),
									"%amount%", String.valueOf(amount), "%price%", economy.format(price)));
				return;
			}
			// subtracting money
			if (!economy.withdrawPlayer(target, price).transactionSuccess()) {
				// error
				if (!silent)
					Util.sendMessage(target,
							"&cAn error occurred, try again, if this message shows again try to contanct the server administrators");
				Util.logToFile("[transaction failed] no errors, is your Economy provider stable?");
				return;
			}
			int given = UtilsInventory.giveAmount(target, item, amount, ExcessManage.CANCEL);
			// reiceved items?
			if (given == 0) {
				// not enough space, aborting
				if (!silent)
					Util.sendMessage(target,
							UtilsString.fix(this.getConfString("not-enough-space"), target, true, "%id%",
									args[1].toLowerCase(), "%nick%", ItemEdit.get().getServerStorage().getNick(args[1]),
									"%amount%", String.valueOf(amount)));
				if (!economy.depositPlayer(target, price).transactionSuccess()) {
					// error
					if (!silent)
						Util.sendMessage(target,
								"&cAn error occurred, try again, if this message shows again try to contanct the server administrators");
					Util.logToFile("[transaction failed] player '" + target.getName() + "' (" + target.getUniqueId()
							+ ") lost " + economy.format(price) + " money, is your Economy provider stable?");
					return;
				}
				return;
			}

			// success, giving some feedback
			if (!silent)
				Util.sendMessage(target,
						UtilsString.fix(this.getConfString("feedback"), target, true, "%id%", args[1].toLowerCase(),
								"%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
								String.valueOf(amount), "%price%", economy.format(price)));

			if (ItemEdit.get().getConfig().loadBoolean("log.action.sell", true)) {
				String msg = UtilsString.fix(this.getConfString("log"), target, true, "%id%", args[1].toLowerCase(),
						"%nick%", ItemEdit.get().getServerStorage().getNick(args[1]), "%amount%",
						String.valueOf(amount), "%player_name%", target.getName(), "%price%", economy.format(price));
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
			return Util.complete(args[2], Arrays.asList("10", "100", "1000", "10000"));
		case 6:
			return Util.complete(args[4], Aliases.BOOLEAN);
		}
		return Collections.emptyList();
	}

}
