package emanondev.itemedit.utility;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ItemUtils {

    private ItemUtils() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    public static ItemMeta getMeta(@NotNull ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta());
    }

    @NotNull
    @SuppressWarnings("deprecation")
    public static ItemStack getHandItem(@NotNull Player player) {
        return player.getInventory().getItemInHand();
    }

    public static boolean isAirOrNull(@Nullable ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }
}
