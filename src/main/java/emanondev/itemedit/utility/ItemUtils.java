package emanondev.itemedit.utility;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    /**
     * Creates an instance of {@link AttributeModifier} based on the provided parameters.
     *
     * @param amount    The amount for the attribute modifier.
     * @param operation The operation for the attribute modifier, cannot be null.
     * @param slot      The equipment slot (group) name for the modifier, can be null. If null, defaults to {@code EquipmentSlotGroup.ANY}.
     * @return A new {@link AttributeModifier} instance.
     * @throws IllegalArgumentException if the provided slot is invalid in the current Minecraft version.
     */
    @SuppressWarnings("UnstableApiUsage")
    public static AttributeModifier createAttributeModifier(double amount,
                                                            @NotNull AttributeModifier.Operation operation,
                                                            @Nullable String slot) {
        if (VersionUtils.isVersionAfter(1, 20, 6)) {
            EquipmentSlotGroup group;
            if (slot == null)
                group = EquipmentSlotGroup.ANY;
            else {
                group = EquipmentSlotGroup.getByName(slot.toUpperCase(Locale.ENGLISH));
                if (group == null) {
                    group = EquipmentSlot.valueOf(slot.toUpperCase(Locale.ENGLISH)).getGroup();
                }
            }
            if (VersionUtils.isVersionAfter(1, 21, 2))
                return new AttributeModifier(Objects.requireNonNull(NamespacedKey.fromString(UUID.randomUUID().toString())),
                        amount, operation, group);
            UUID uuid = UUID.randomUUID();
            return ReflectionUtils.invokeConstructor(AttributeModifier.class,
                    UUID.class, uuid,
                    String.class, uuid.toString(),
                    double.class, amount,
                    AttributeModifier.Operation.class, operation,
                    EquipmentSlotGroup.class, group);
        }

        UUID uuid = UUID.randomUUID();
        if (VersionUtils.isVersionAfter(1, 13, 2)) {
            return ReflectionUtils.invokeConstructor(AttributeModifier.class,
                    UUID.class, uuid,
                    String.class, uuid.toString(),
                    double.class, amount,
                    AttributeModifier.Operation.class, operation,
                    EquipmentSlot.class, slot == null ? null : EquipmentSlot.valueOf(slot.toUpperCase(Locale.ENGLISH)));
        }
        return ReflectionUtils.invokeConstructor(AttributeModifier.class,
                UUID.class, uuid,
                String.class, uuid.toString(),
                double.class, amount,
                AttributeModifier.Operation.class, operation);
    }

    /**
     * Retrieves all available {@link PatternType}s based on the current Minecraft version.
     *
     * @return An array of {@link PatternType} objects. In versions after 1.20.6, patterns are retrieved from
     * the {@code Registry.BANNER_PATTERN}. For earlier versions, they are retrieved via reflection.
     */
    public static PatternType[] getPatternTypes() {
        try {
            if (VersionUtils.isVersionAfter(1, 20, 6)) {
                List<PatternType> result = new ArrayList<>();
                Registry.BANNER_PATTERN.forEach(result::add);
                return result.toArray(new PatternType[0]);
            }
        } catch (Throwable ignored) {
        }
        PatternType[] result = Objects.requireNonNull((PatternType[]) ReflectionUtils
                .invokeStaticMethod(PatternType.class, "values"));
        return Arrays.copyOfRange(result, 1, result.length);
    }

    /**
     * Retrieves filtered {@link PatternType}s excluding non-craftable patterns in certain Minecraft versions.
     *
     * @return An array of {@link PatternType} objects with {@code PatternType.BASE} always removed.
     * If the version is 1.20.6 but not after 1.21, {@code PatternType.FLOW} and {@code PatternType.GUSTER}
     * are also removed as they are not craftable.
     */
    public static PatternType[] getPatternTypesFiltered() {
        PatternType[] val = getPatternTypes();
        ArrayList<PatternType> list = new ArrayList<>(Arrays.asList(val));
        list.remove(PatternType.BASE);
        if (VersionUtils.isVersionAfter(1, 20, 6) &&
                !VersionUtils.isVersionAfter(1, 21)) { //those are not craftable items on 1.20.6
            list.remove(PatternType.FLOW);
            list.remove(PatternType.GUSTER);
        }
        return list.toArray(new PatternType[0]);
    }
}
