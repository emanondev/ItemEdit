package emanondev.itemedit.storage;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;

public class YmlPlayerStorage implements PlayerStorage {

	private final YMLConfig database = ItemEdit.get()
			.getConfig("database" + File.separatorChar + "player-database.yml");

	private String getBasePath(OfflinePlayer p) {
		return storeByUUID() ? p.getUniqueId().toString() : p.getName();
	}

	@Override
	public ItemStack getItem(OfflinePlayer p, String id) {
		validateID(id);
		id = id.toLowerCase();
		if (p == null)
			throw new NullPointerException();
		ItemStack item = database.getItemStack(getBasePath(p) + "." + id + ".item", null);
		return item == null ? null : item.clone();
	}

	@Override
	public void setItem(OfflinePlayer p, String id, ItemStack item) {
		validateID(id);
		id = id.toLowerCase();
		if (p == null)
			throw new NullPointerException();
		if (item == null || item.getType() == Material.AIR)
			throw new IllegalArgumentException();
		item.setAmount(1);
		database.set(getBasePath(p) + "." + id + ".item", item);
		database.save();
	}

	@Override
	public void remove(OfflinePlayer p, String id) {
		validateID(id);
		id = id.toLowerCase();
		if (p == null)
			throw new NullPointerException();
		database.set(getBasePath(p) + "." + id, null);
		database.save();
	}

	@Override
	public void clear(OfflinePlayer p) {
		if (p == null)
			throw new NullPointerException();
		database.set(getBasePath(p), null);
		database.save();
	}

	@Override
	public Set<String> getIds(OfflinePlayer p) {
		if (p == null)
			throw new NullPointerException();
		return database.getKeys(getBasePath(p));
	}

	@SuppressWarnings("deprecation")
	@Override
	public Set<OfflinePlayer> getPlayers() {
		Set<String> playersData = database.getKeys(false);
		Set<OfflinePlayer> players = new HashSet<>();
		boolean uuid = storeByUUID();
		for (String val : playersData) {
			if (uuid)
				players.add(Bukkit.getOfflinePlayer(UUID.fromString(val)));
			else
				players.add(Bukkit.getOfflinePlayer(val));
		}
		return players;
	}

}
