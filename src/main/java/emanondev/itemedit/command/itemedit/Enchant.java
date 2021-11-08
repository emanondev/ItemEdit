package emanondev.itemedit.command.itemedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class Enchant extends SubCmd {
	public Enchant(ItemEditCommand cmd) {
		super("enchant", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length != 2 && args.length != 3)
				throw new IllegalArgumentException("Wrong argument Number");
			int lv = 1;
			Enchantment ench = Aliases.ENCHANT.convertAlias(args[1]);
			if (ench == null)
				throw new IllegalArgumentException("Wrong Echantment");
			if (args.length == 3)
				lv = Integer.parseInt(args[2]);
			if (lv == 0)
				item.removeEnchantment(ench);
			else {
				if (!p.hasPermission(this.getPermission()+".bypass_max_level"))
					lv = Math.min(ench.getMaxLevel(), lv);
				item.addUnsafeEnchantment(ench, lv);
			}
			p.updateInventory();
		} catch (Exception e) {
			onFail(p);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Aliases.ENCHANT);
		Enchantment ench = Aliases.ENCHANT.convertAlias(args[2]);
		if (ench == null)
			return Collections.emptyList();
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i <= ench.getMaxLevel(); i++)
			list.add(String.valueOf(i));
		return list;
	}

}
