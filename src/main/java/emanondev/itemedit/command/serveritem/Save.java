package emanondev.itemedit.command.serveritem;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.command.SubCmd;

public class Save extends SubCmd {

	public Save(ServerItemCommand cmd) {
		super("save", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		try {
			if (args.length != 2)
				throw new IllegalArgumentException("Wrong param number");
			if (ItemEdit.get().getServerStorage().getItem(args[1]) == null)
				ItemEdit.get().getServerStorage().setItem(args[1], this.getItemInHand(p).clone());
			else
				throw new IllegalArgumentException();
			Util.sendMessage(p, UtilsString.fix(this.getConfString("success"), p, true, "%id%", args[1].toLowerCase()));
		} catch (Exception e) {
			onFail(p);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		return Collections.emptyList();
	}

}
