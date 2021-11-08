package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.FireworkEditor;

public class Firework extends SubCmd {

	public Firework(ItemEditCommand cmd) {
		super("firework", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		if (!(item.getItemMeta() instanceof FireworkMeta)) {
			Util.sendMessage(p, this.getConfString("wrong-type"));
			return;
		}

		try {
			((Player) sender).openInventory(new FireworkEditor((Player) sender,item).getInventory());
		} catch (Exception e) {
			e.printStackTrace();
			onFail(p);
		}
	}

	// itemedit firework
	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
