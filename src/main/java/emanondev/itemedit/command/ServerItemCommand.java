package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.serveritem.*;

public class ServerItemCommand extends AbstractCommand {
	public static ServerItemCommand instance;

	public static ServerItemCommand get() {
		return instance;
	}

	public ServerItemCommand() {
		super("serveritem",ItemEdit.get());
		instance = this;
		this.registerSubCommand(new Save(this));
		this.registerSubCommand(new Update(this));
		this.registerSubCommand(new Delete(this));
		this.registerSubCommand(new Show(this));
		this.registerSubCommand(new SetNick(this));
		this.registerSubCommand(new Give(this));
		this.registerSubCommand(new GiveAll(this));
		this.registerSubCommand(new Drop(this));
		this.registerSubCommand(new Take(this));
		try {
			this.registerSubCommand(new Sell(this));
			this.registerSubCommand(new Buy(this));
			this.registerSubCommand(new SellMax(this));
			this.registerSubCommand(new BuyMax(this));
			ItemEdit.get().log("Hooking into Vault");
		} catch (NoClassDefFoundError|IllegalStateException e) {
			ItemEdit.get().log("Unable to hook into Vault");
		}
	}

}
