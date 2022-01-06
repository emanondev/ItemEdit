package emanondev.itemedit.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;

public class YmlServerStorage implements ServerStorage {

	private final YMLConfig database = ItemEdit.get()
			.getConfig("database" + File.separatorChar + "server-database.yml");
	private final HashMap<ItemStack, String> reversedMap = new HashMap<>();
	
	public YmlServerStorage() {
		reload();
	}

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
		reversedMap.remove(database.get(id));
		database.set(id, null);
		database.save();
	}

	@Override
	public void clear() {
		for (String key : database.getKeys(false))
			database.set(key, null);
		reversedMap.clear();
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
		reversedMap.put(item, id);
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

	@Override
	public String getId(ItemStack item) {
		if (item==null)
			return null;
		int amount = item.getAmount();
		if (item.getAmount()!=1)
			item.setAmount(1);
		String id = reversedMap.get(item);
		if (amount!=1)
			item.setAmount(amount);
		return id;
	}

	@Override
	public void reload() {
		reversedMap.clear();
		for(String id:database.getKeys(false)) {
			try {
				validateID(id);
			} catch (Exception e) {
				continue;
			}
			reversedMap.put(database.getItemStack(id + ".item", null),id);
		}
	}
}
