package emanondev.itemedit.command.itemstorage;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.ShowPlayerItemsGui;

public class Show extends SubCmd {

	public Show(ItemStorageCommand cmd) {
		super("show", cmd, true, false);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		try {
			int page = 1;
			if (args.length >= 2)
				page = Integer.parseInt(args[1]);
			p.openInventory(new ShowPlayerItemsGui(p, page).getInventory());
		} catch (Exception e) {
			onFail(sender);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
