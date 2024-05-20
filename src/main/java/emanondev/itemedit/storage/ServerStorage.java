package emanondev.itemedit.storage;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsString;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * this class allows interacting with server stored items
 */
public interface ServerStorage {
    /**
     * Get ItemStack associated with given id.
     *
     * @param id unique identifier of the item, case insensitive
     * @return item associated with id
     */
    @Nullable
    ItemStack getItem(@NotNull String id);

    /**
     * Get the nick associated with id.
     *
     * @param id unique identifier of the item, case insensitive
     * @return nick associated with id, if none is set returns item title, if item
     * has no title@Override item material name is returned
     */
    @Nullable
    String getNick(@NotNull String id);

    /**
     * Set association for id and item for player.
     *
     * @param id   unique identifier of the item, case insensitive
     * @param item item to associate to id
     */
    void setItem(@NotNull String id, @NotNull ItemStack item);

    /**
     * Sets nick value for id.
     *
     * @param id   unique identifier of the item, case insensitive
     * @param nick nick of the item,
     */
    void setNick(@NotNull String id, @Nullable String nick);

    /**
     * Remove associations with id.
     *
     * @param id unique identifier of the item, case insensitive
     */
    void remove(@NotNull String id);

    /**
     * Remove all ids.
     */
    void clear();

    /**
     * Get a set of all used ids.
     *
     * @return a set of all ids saved
     */
    @NotNull
    Set<String> getIds();

    default void validateID(String id) {
        if (id == null || id.contains(" ") || id.contains(".") || id.isEmpty())
            throw new IllegalArgumentException();
    }

    default ItemStack getItem(String id, Player target) {
        ItemStack item = getItem(id);
        if (item == null || target == null)
            return item;
        if (ItemEdit.get().getConfig().loadBoolean("serveritem.replace-holders", true)) {
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(UtilsString.fix(meta.getDisplayName(), target, true, "%player_name%", target.getName(),
                    "%player_uuid%", target.getUniqueId().toString()));
            meta.setLore(UtilsString.fix(meta.getLore(), target, true, "%player_name%", target.getName(),
                    "%player_uuid%", target.getUniqueId().toString()));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Returns true if the storage contains a similar item
     *
     * @param item item to check
     * @return true if the storage contains a similar item
     */
    default boolean contains(ItemStack item) {
        return getId(item) != null;
    }

    /**
     * Returns the id of the item or null if not contained
     *
     * @param item item to check
     * @return the id of the item or null if not contained
     */
    String getId(ItemStack item);

    /**
     * Handle plugin reloads
     */
    void reload();
}
