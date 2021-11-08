package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.itemstorage.Delete;
import emanondev.itemedit.command.itemstorage.Get;
import emanondev.itemedit.command.itemstorage.Save;
import emanondev.itemedit.command.itemstorage.Show;
import emanondev.itemedit.command.itemstorage.Update;

public class ItemStorageCommand extends AbstractCommand{
	public static ItemStorageCommand instance;
	public static ItemStorageCommand get() {
		return instance;
	}

	public ItemStorageCommand() {
		super("itemstorage",ItemEdit.get());
		instance = this;
		this.registerSubCommand(new Save(this));
		this.registerSubCommand(new Update(this));
		this.registerSubCommand(new Delete(this));
		this.registerSubCommand(new Show(this));
		this.registerSubCommand(new Get(this));
	}

}
