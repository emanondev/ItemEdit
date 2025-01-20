package emanondev.itemedit.utility;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utility class for working with {@link ItemStack} and {@link ItemMeta}.
 * This class cannot be instantiated.
 */
public final class ItemUtils {

    private ItemUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the {@link ItemMeta} of an {@link ItemStack}.
     *
     * @param item the {@link ItemStack} to get the {@link ItemMeta} from.
     * @return the {@link ItemMeta} of the provided item.
     */
    @NotNull
    public static ItemMeta getMeta(@NotNull ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta(), "ItemMeta cannot be null");
    }

    /**
     * Retrieves the {@link ItemStack} currently held in the main hand of a {@link Player}.
     *
     * @param player the {@link Player} whose main hand item is to be retrieved.
     * @return the {@link ItemStack} in the player's main hand.
     */
    @NotNull
    @SuppressWarnings("deprecation")
    public static ItemStack getHandItem(@NotNull Player player) {
        return player.getInventory().getItemInHand();
    }

    /**
     * Checks if an {@link ItemStack} is null or represents air.
     *
     * @param item the {@link ItemStack} to check.
     * @return {@code true} if the item is null or its type is {@link Material#AIR}, {@code false} otherwise.
     */
    public static boolean isAirOrNull(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    /**
     * Returns true if the item has the unbreakable tag.
     *
     * @param item The {@link ItemStack} to check.
     * @return {@code true} if the item has the unbreakable tag, {@code false} otherwise.
     * @see #isUnbreakable(ItemMeta)
     */
    public static boolean isUnbreakable(@NotNull ItemStack item) {
        return isUnbreakable(ItemUtils.getMeta(item));
    }

    /**
     * Returns true if the {@link ItemMeta} has the unbreakable tag.
     *
     * @param meta The {@link ItemMeta} to check.
     * @return {@code true} if the meta has the unbreakable tag, {@code false} otherwise.
     */
    public static boolean isUnbreakable(@Nullable ItemMeta meta) {
        if (meta == null)
            return false;
        if (VersionUtils.isVersionAfter(1, 11))
            return meta.isUnbreakable();

        // For older versions, use reflection to access the unbreakable property.
        Object spigotMeta = Objects.requireNonNull(ReflectionUtils.invokeMethod(meta, "spigot"));
        Boolean result = (Boolean) ReflectionUtils.invokeMethod(spigotMeta, "isUnbreakable");
        return result != null && result;
    }

    /**
     * Sets the unbreakable tag on the item.
     *
     * @param item  The {@link ItemStack} to set the unbreakable tag on.
     * @param value The value to set for the unbreakable tag.
     * @see #setUnbreakable(ItemMeta, boolean)
     */
    public static void setUnbreakable(@NotNull ItemStack item,
                                      boolean value) {
        ItemMeta meta = ItemUtils.getMeta(item);
        setUnbreakable(meta, value);
        item.setItemMeta(meta);
    }

    /**
     * Sets the unbreakable tag on the {@link ItemMeta}.
     *
     * @param meta  The {@link ItemMeta} to set the unbreakable tag on.
     * @param value The value to set for the unbreakable tag.
     * @see #setUnbreakable(ItemStack, boolean)
     */
    public static void setUnbreakable(@Nullable ItemMeta meta,
                                      boolean value) {
        if (meta == null)
            return;
        if (VersionUtils.isVersionAfter(1, 11)) {
            meta.setUnbreakable(value);
            return;
        }
        // For older versions, use reflection to set the unbreakable property.
        Object spigotMeta = Objects.requireNonNull(ReflectionUtils.invokeMethod(meta, "spigot"));
        ReflectionUtils.invokeMethod(spigotMeta, "setUnbreakable", boolean.class, value);
    }
}
