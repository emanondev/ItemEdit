package emanondev.itemedit.utility;

import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to handle an {@link ItemMeta} color.
 */
@Getter
public class ColoredMeta {

    private final Type type;
    private final ItemMeta meta;

    private ColoredMeta(Type type, ItemMeta meta) {
        this.type = type;
        this.meta = meta;
    }

    /**
     * Map an {@link ItemMeta} to a wrapped instance of {@link ColoredMeta}.
     * @param item the non-null item to extract the meta of.
     * @return {@code null} if the meta does not match any valid type.
     */
    public static @Nullable ColoredMeta of(@NotNull ItemStack item) {
        return of(ItemUtils.getMeta(item));
    }

    /**
     * Map an {@link ItemMeta} to a wrapped instance of {@link ColoredMeta}.
     * @param meta the non-null item-meta to convert.
     * @return {@code null} if the meta does not match any valid type.
     */
    public static @Nullable ColoredMeta of(@NotNull ItemMeta meta) {
        // Potion
        if(meta instanceof PotionMeta)
            return new ColoredMeta(Type.POTION, meta);

        // Leather armor
        try {
            if(meta instanceof LeatherArmorMeta)
                return new ColoredMeta(Type.LEATHER_ARMOR, meta);
        } catch(Throwable t) {/* Missing class, old game version. */}

        // Leather armor
        try {
            if(meta instanceof FireworkEffectMeta)
                return new ColoredMeta(Type.FIREWORK, meta);
        } catch(Throwable t) {/* Missing class, old game version. */}

        // No match
        return null;
    }

    /**
     * Change the meta color.
     * @param color the new color to set.
     */
    public void setColor(@NotNull Color color) {
        ItemUtils.setColor(meta, color);
    }

    /**
     * Get the item-meta value.
     * @return a non-null color.
     */
    public @NotNull Color getColor() {
        return ItemUtils.getColor(meta);
    }

    /**
     * Set this meta to an item.
     * @param item the item to set the meta to.
     */
    public void setToItem(@NotNull ItemStack item) {
        item.setItemMeta(meta);
    }

    /**
     * Meta type.
     */
    public enum Type {
        POTION,
        LEATHER_ARMOR,
        FIREWORK
    }

}
