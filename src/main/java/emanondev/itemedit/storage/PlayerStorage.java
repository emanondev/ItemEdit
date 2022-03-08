package emanondev.itemedit.storage;

import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import emanondev.itemedit.ItemEdit;

/**
 * this class allows to interact with players storages
 */
public interface PlayerStorage {
	/**
	 * Get ItemStack associated with given id for player.
	 * 
	 * @param p  player
	 * @param id unique identifier of the item, case insensitive
	 * @return item associated with id
	 */
	@Nullable ItemStack getItem(@NotNull OfflinePlayer p, @NotNull String id);

	/**
	 * Set association for id and item for player.
	 * 
	 * @param p    player
	 * @param id   unique identifier of the item, case insensitive
	 * @param item item to associate to id
	 */
	void setItem(@NotNull OfflinePlayer p, @NotNull String id, @NotNull ItemStack item);

	/**
	 * Remove associations with id for player.
	 * 
	 * @param p  player
	 * @param id unique identifier of the item, case insensitive
	 */
	void remove(@NotNull OfflinePlayer p, @NotNull String id);

	/**
	 * remove all ids of player.
	 * 
	 * @param p player
	 */
	void clear(@NotNull OfflinePlayer p);

	/**
	 * Get a set of all ids of Player.
	 * 
	 * @param p player
	 * @return a set of all ids saved for player
	 */
	@NotNull Set<String> getIds(@NotNull OfflinePlayer p);

	/**
	 * Get all players with at least one saved id.
	 * 
	 * @return a set af all players with at least an id
	 */
	@NotNull Set<OfflinePlayer> getPlayers();

	default boolean storeByUUID() {
		return ItemEdit.get().getConfig().loadBoolean("storage.store-by-uuid", true);
	}

	default void validateID(String id) {
		if (id == null || id.contains(" ") || id.contains(".") || id.isEmpty())
			throw new IllegalArgumentException();
	}

	/**
	 * Handle plugin reloads
	 */
	default void reload() {
	}

}
