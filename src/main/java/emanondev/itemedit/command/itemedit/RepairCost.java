package emanondev.itemedit.command.itemedit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class RepairCost extends SubCmd {

	public RepairCost(ItemEditCommand cmd) {
		super("repaircost", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			Repairable meta = (Repairable) item.getItemMeta();
			meta.setRepairCost(Integer.parseInt(args[1]));
			item.setItemMeta((ItemMeta) meta);
			p.updateInventory();
		} catch (Exception e) {
			onFail(p);
		}

	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Arrays.asList("0","1","3","7","30","40"));
		return Collections.emptyList();
	}

}
