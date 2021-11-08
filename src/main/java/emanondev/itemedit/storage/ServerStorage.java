package emanondev.itemedit.storage;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsString;

/**
 * this class allows to interact with server stored items
 */
public interface ServerStorage {
	/**
	 * Get ItemStack associated with given id.
	 * 
	 * @param id
	 *            unique identifier of the item, case insensitive
	 * @return item associated with id
	 */
	public @Nullable ItemStack getItem(@NotNull String id);

	/**
	 * Get the nick associated with id.
	 * 
	 * @param id
	 *            unique identifier of the item, case insensitive
	 * @return nick associated with id, if none is set returns item title, if item
	 *         has no title@Override
	 item material name is returned
	 */
	public @Nullable String getNick(@NotNull String id);

	/**
	 * Set association for id and item for player.
	 * 
	 * @param id
	 *            unique identifier of the item, case insensitive
	 * @param item
	 *            item to associate to id
	 */
	public void setItem(@NotNull String id, @NotNull ItemStack item);

	/**
	 * Sets nick value for id.
	 * 
	 * @param id
	 *            unique identifier of the item, case insensitive
	 * @param nick
	 *            nick of the item,
	 */
	public void setNick(@NotNull String id, @Nullable String nick);

	/**
	 * Remove associations with id.
	 * 
	 * @param id
	 *            unique identifier of the item, case insensitive
	 */
	public void remove(@NotNull String id);

	/**
	 * Remove all ids.
	 */
	public void clear();

	/**
	 * Get a set of all used ids.
	 * 
	 * @return a set of all ids saved
	 */
	public @NotNull Set<String> getIds();

	public default void validateID(String id) {
		if (id == null || id.contains(" ") || id.contains(".") || id.isEmpty())
			throw new IllegalArgumentException();
	}

	public default ItemStack getItem(String id,Player target) {
		ItemStack item = getItem(id);
		if (item==null || target==null)
			return item;
		if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%",
					target.getName(), "%player_uuid%", target.getUniqueId().toString()));
			meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%", target.getName(),
					"%player_uuid%", target.getUniqueId().toString()));
			item.setItemMeta(meta);
		}
		return item;
	}
	
}
