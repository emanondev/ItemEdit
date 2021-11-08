package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class UnbreakableOld extends SubCmd {

	public UnbreakableOld(ItemEditCommand cmd) {
		super("unbreakable", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			Map<String, Object> map = new LinkedHashMap<String, Object>(item.getItemMeta().serialize());
			boolean value = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !map.containsKey("Unbreakable");

			if (value)
				map.put("Unbreakable", value);
			else
				map.remove("Unbreakable");
			map.put("==", "ItemMeta");
			ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(map);
			item.setItemMeta(meta);
			p.updateInventory();
		} catch (Exception e) {
			onFail(p);
		}

	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Aliases.BOOLEAN);
		return Collections.emptyList();
	}
}
