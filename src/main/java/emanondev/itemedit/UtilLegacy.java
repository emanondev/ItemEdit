package emanondev.itemedit;

import emanondev.itemedit.compability.V1_20_6;
import org.bukkit.Bukkit;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class UtilLegacy {


    private static final HashMap<Class<?>, Method> getTopInventory = new HashMap<>();
    private static final HashMap<Class<?>, Method> getBottomInventory = new HashMap<>();


    /**
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull InventoryEvent event) {
        if (Util.isVersionAfter(1, 21))
            return event.getView().getTopInventory();
        return getTopInventoryP(event.getView());
    }

    /**
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In versions 1.21 and later, it is an interface.
     *
     * @param player The player with an InventoryView to inspect.
     * @return The top Inventory object from the player's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull Player player) {
        if (Util.isVersionAfter(1, 21))
            return player.getOpenInventory().getTopInventory();
        return getTopInventoryP(player.getOpenInventory());
    }

    private static Inventory getTopInventoryP(@NotNull Object view) {
        try {
            Method method = getTopInventory.get(view.getClass());
            if (method == null) {
                method = view.getClass().getMethod("getTopInventory");
                method.setAccessible(true);
                getTopInventory.put(view.getClass(), method);
            }
            return (Inventory) method.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The bottom Inventory object from the event's InventoryView.
     */
    public static Inventory getBottomInventory(@NotNull InventoryEvent event) {
        if (Util.isVersionAfter(1, 21))
            return event.getView().getBottomInventory();
        return getBottomInventoryP(event.getView());
    }

    private static Inventory getBottomInventoryP(@NotNull Object view) {
        try {
            Method method = getBottomInventory.get(view.getClass());
            if (method == null) {
                method = view.getClass().getMethod("getBottomInventory");
                method.setAccessible(true);
                getBottomInventory.put(view.getClass(), method);
            }
            return (Inventory) method.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.
     *
     * @param player The player which inventory view should be updated
     */
    public static void updateView(@NotNull Player player) {
        if (Util.isVersionUpTo(1, 19, 4) || Util.hasPurpurAPI()) {
            player.updateInventory();
        }
    }

    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.
     *
     * @param player The player which inventory view should be updated
     */
    public static void updateViewDelayed(@NotNull Player player) {
        if (Util.isVersionUpTo(1, 19, 4) || Util.hasPurpurAPI()) {
            Bukkit.getScheduler().runTaskLater(ItemEdit.get(), player::updateInventory, 1L);
        }
    }


    /**
     * This method accepts <code>infinite</code>,<code>∞</code> or any number as valid input.<br>
     * On versions before 1.19.4, infinite is turned to <code>Integer.MAX_VALUE</code>.<br><br>
     * In API versions 1.19.3 and earlier, there is no infinite value for PotionEffects.<br>
     * In API versions 1.19.4 and later, -1 is the value for infinite duration.
     *
     * @param value The raw value to be interpreted, values below <code>0</code> are treated as infinite
     * @throws NumberFormatException When value cannot be interpreted to a valid value
     */
    public static int readPotionEffectDurationSecondsToTicks(@NotNull String value) {
        int duration = (value.equalsIgnoreCase("infinite")
                || (value.equalsIgnoreCase("∞"))) ?
                -1 : (Integer.parseInt(value));
        if (duration >= 0)
            duration *= 20; //to ticks
        else if (!Util.isVersionAfter(1, 19, 4))
            duration = Integer.MAX_VALUE;
        else
            duration = -1;
        return duration;
    }


    /**
     * Returns true if the item has unbreakable tag.<br><br>
     * In API versions 1.10.2 and earlier, there is no isUnbreakable method on ItemMeta, so we rely on serialization.<br>
     * In API versions 1.11 and later, isUnbreakable method was added on ItemMeta.
     *
     * @param item The ItemStack to check
     * @return true if the item has unbreakable tag
     * @see #isUnbreakable(ItemMeta)
     */
    public static boolean isUnbreakable(@NotNull ItemStack item) {
        return isUnbreakable(item.getItemMeta());
    }

    /**
     * Returns true if the meta has unbreakable tag.<br><br>
     * In API versions 1.10.2 and earlier, there is no isUnbreakable method on ItemMeta, so we rely on serialization.<br>
     * In API versions 1.11 and later, isUnbreakable method was added on ItemMeta.
     *
     * @param meta The ItemMeta to check
     * @return true if the meta has unbreakable tag
     */
    public static boolean isUnbreakable(@Nullable ItemMeta meta) {
        if (meta == null)
            return false;
        if (Util.isVersionAfter(1, 11))
            return meta.isUnbreakable();
        return meta.serialize().containsKey("Unbreakable");
    }

    /**
     * Sets the item unbreakable tag.<br><br>
     * In API versions 1.10.2 and earlier, there is no setUnbreakable method on ItemMeta, so we rely on serialization.<br>
     * In API versions 1.11 and later, setUnbreakable method was added on ItemMeta.
     *
     * @param item  The ItemStack to set tag on
     * @param value The value to set
     * @see #setUnbreakable(ItemMeta, boolean)
     */
    public static void setUnbreakable(@NotNull ItemStack item, boolean value) {
        ItemMeta meta = setUnbreakable(item.getItemMeta(), value);
        item.setItemMeta(meta);
    }

    /**
     * Returns meta with unbreakable tag.<br>
     * N.B. returned value may or may not be different from meta parameter<br><br>
     * In API versions 1.10.2 and earlier, there is no setUnbreakable method on ItemMeta, so we rely on serialization.<br>
     * In API versions 1.11 and later, setUnbreakable method was added on ItemMeta.
     *
     * @param meta  The ItemMeta to set tag on
     * @param value The value to set
     * @see #setUnbreakable(ItemMeta, boolean)
     */
    public static ItemMeta setUnbreakable(@Nullable ItemMeta meta, boolean value) {
        if (meta == null)
            return null;
        if (Util.isVersionAfter(1, 11)) {
            meta.setUnbreakable(value);
            return meta;
        }
        Map<String, Object> map = new LinkedHashMap<>(meta.serialize());
        if (map.containsKey("Unbreakable")) {
            if (value)
                map.put("Unbreakable", true);
            else
                return meta;
        } else {
            if (!value)
                return meta;
            else
                map.remove("Unbreakable");
        }
        map.put("==", "ItemMeta");
        return (ItemMeta) ConfigurationSerialization.deserializeObject(map);
    }


    public static AttributeModifier createAttributeModifier(org.bukkit.attribute.Attribute attr, double amount, AttributeModifier.Operation op, @Nullable String slot) {
        try {
            if (Util.isVersionAfter(1, 20, 6))
                return V1_20_6.createAttribute(attr, amount, op, slot);
        } catch (Throwable e) {
        }
        return new AttributeModifier(UUID.randomUUID(), attr.toString(), amount, op, EquipmentSlot.valueOf(slot.toUpperCase(Locale.ENGLISH)));
    }


    public static PatternType[] getPatternTypes() {
        try {
            if (Util.isVersionAfter(1, 20, 6))
                return V1_20_6.getPatternTypes();
        } catch (Throwable e) {
        }
        try {
            Class<PatternType> clazz = PatternType.class;
            Method method = clazz.getMethod("values");
            PatternType[] result = (PatternType[]) method.invoke(null);
            return Arrays.copyOfRange(result, 1, result.length);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    public static PatternType[] getPatternTypesFilthered() {
        PatternType[] val = getPatternTypes();
        ArrayList<PatternType> list = new ArrayList<>(Arrays.asList(val));
        list.remove(PatternType.BASE);
        if (Util.isVersionAfter(1, 20, 6) && !Util.isVersionAfter(1, 21)) { //those are not craftable items on 1.20.6
            list.remove(PatternType.FLOW);
            list.remove(PatternType.GUSTER);
        }
        return list.toArray(new PatternType[0]);
    }


}
