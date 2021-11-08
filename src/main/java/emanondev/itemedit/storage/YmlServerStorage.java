package emanondev.itemedit.storage;

import java.io.File;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;

public class YmlServerStorage implements ServerStorage {

	private final YMLConfig database = ItemEdit.get()
			.getConfig("database" + File.separatorChar + "server-database.yml");

	@Override
	public ItemStack getItem(String id) {
		validateID(id);
		id = id.toLowerCase();
		ItemStack item = database.getItemStack(id + ".item", null);
		return item == null ? null : item.clone();
	}
	
	
	@Override
	public String getNick(String id) {
		validateID(id);
		id = id.toLowerCase();
		String nick = database.getString(id + ".nick", null, true);
		if (nick != null)
			return nick;
		if (!database.contains(id))
			return null;
		ItemStack item = getItem(id);
		if (!item.hasItemMeta())
			return item.getType().name().toLowerCase();
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName())
			return item.getType().name().toLowerCase();
		return meta.getDisplayName();
	}

	@Override
	public void remove(String id) {
		validateID(id);
		id = id.toLowerCase();
		database.set(id, null);
		database.save();
	}

	@Override
	public void clear() {
		for (String key : database.getKeys(false))
			database.set(key, null);
		database.save();
	}

	@Override
	public Set<String> getIds() {
		return database.getKeys(false);
	}

	@Override
	public void setItem(String id, ItemStack item) {
		validateID(id);
		id = id.toLowerCase();
		if (item == null || item.getType() == Material.AIR)
			throw new IllegalArgumentException();
		item.setAmount(1);
		database.set(id + ".item", item);
		database.save();
	}

	@Override
	public void setNick(String id, String nick) {
		validateID(id);
		if (!database.contains(id))
			return;
		id = id.toLowerCase();
		database.set(id + ".nick", nick);
		database.save();

	}

}
