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
     * @return true if the item is null or its type is {@link Material#AIR}, false otherwise.
     */
    public static boolean isAirOrNull(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
