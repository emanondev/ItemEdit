package emanondev.itemedit.utility;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InventoryUtils {

    private static final Map<Class<?>, Method> getTopInventory =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private static final Map<Class<?>, Method> getBottomInventory =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private static final Set<EquipmentSlot> playerEquipmentSlots = loadPlayerEquipmentSlot();

    private InventoryUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the top Inventory object from the event's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull InventoryEvent event) {
        if (VersionUtils.isVersionAfter(1, 21)) {
            return event.getView().getTopInventory();
        }
        return getTopInventoryP(event.getView());
    }

    /**
     * Returns the top Inventory object from the player's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In versions 1.21 and later, it is an interface.
     *
     * @param player The player with an InventoryView to inspect.
     * @return The top Inventory object from the player's InventoryView.
     */
    public static Inventory getTopInventory(@NotNull Player player) {
        if (VersionUtils.isVersionAfter(1, 21)) {
            return player.getOpenInventory().getTopInventory();
        }
        return getTopInventoryP(player.getOpenInventory());
    }

    private static Inventory getTopInventoryP(@NotNull Object view) {
        Method method = getTopInventory.get(view.getClass());
        if (method == null) {
            method = ReflectionUtils.getMethod(view.getClass(), "getTopInventory");
            getTopInventory.put(view.getClass(), method);
        }
        return (Inventory) ReflectionUtils.invokeMethod(view, method);
    }

    /**
     * Returns the bottom Inventory object from the event's InventoryView.<br><br>
     * This method may use reflections to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The bottom Inventory object from the event's InventoryView.
     */
    public static Inventory getBottomInventory(@NotNull InventoryEvent event) {
        if (VersionUtils.isVersionAfter(1, 21)) {
            return event.getView().getBottomInventory();
        }
        return getBottomInventoryP(event.getView());
    }

    private static Inventory getBottomInventoryP(@NotNull Object view) {
        Method method = getBottomInventory.get(view.getClass());
        if (method == null) {
            method = ReflectionUtils.getMethod(view.getClass(), "getBottomInventory");
            getBottomInventory.put(view.getClass(), method);
        }
        return (Inventory) ReflectionUtils.invokeMethod(view, method);
    }

    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.
     *
     * @param player The player which inventory view should be updated
     */
    @SuppressWarnings("UnstableApiUsage")
    public static void updateView(@NotNull Player player) {
        if (VersionUtils.isVersionUpTo(1, 19, 4) || VersionUtils.hasPurpurAPI()) {
            SchedulerUtils.run(ItemEdit.get(), player, player::updateInventory);
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
    @SuppressWarnings("UnstableApiUsage")
    public static void updateViewDelayed(@NotNull Player player) {
        if (VersionUtils.isVersionUpTo(1, 19, 4) || VersionUtils.hasPurpurAPI()) {
            SchedulerUtils.runLater(ItemEdit.get(), player, 1L, player::updateInventory);
        }
    }

    /**
     * @param player the player
     * @param item   item to be give, note: item.getAmount() is ignored
     * @param amount amount of item to be given
     * @param mode   how to handle special cases
     * @return the amount given (or given + dropped)
     */
    public static int giveAmount(@NotNull HumanEntity player,
                                 @NotNull ItemStack item,
                                 @Range(from = 0, to = Integer.MAX_VALUE) int amount,
                                 @NotNull InventoryUtils.ExcessMode mode) {
        final ItemStack itemClone = item.clone();
        if (amount == 0) {
            return 0;
        }
        int remains = amount;
        while (remains > 0) {
            itemClone.setAmount(Math.min(itemClone.getMaxStackSize(), remains));
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(itemClone);
            remains = remains - Math.min(itemClone.getMaxStackSize(), remains);
            if (map.isEmpty()) {
                continue;
            }
            remains = remains + map.get(0).getAmount();
            break;
        }

        if (player instanceof Player) {
            updateViewDelayed((Player) player);
        }

        if (remains == 0) {
            return amount;
        }

        switch (mode) {
            case DELETE_EXCESS: {
                return amount - remains;
            }
            case DROP_EXCESS: {
                while (remains > 0) {
                    int drop = Math.min(remains, 64);
                    itemClone.setAmount(drop);
                    ItemStack itemCopy = new ItemStack(itemClone);
                    Location loc = player.getEyeLocation();
                    SchedulerUtils.run(ItemEdit.get(), loc,
                            () -> player.getWorld().dropItem(loc, itemCopy));
                    remains -= drop;
                }
                return amount;
            }
            case CANCEL: {
                removeAmount(player, itemClone, amount - remains, LackMode.REMOVE_MAX_POSSIBLE);
                return 0;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * @param player the player
     * @param item   item to be give, note: item.getAmount() is ignored
     * @param amount amount of item to be given
     * @param mode   how to handle special cases
     * @return the removed amount
     */
    public static int removeAmount(@NotNull HumanEntity player,
                                   @NotNull ItemStack item,
                                   @Range(from = 0, to = Integer.MAX_VALUE) int amount,
                                   @NotNull InventoryUtils.LackMode mode) {
        final ItemStack itemClone = item.clone();
        if (amount == 0) {
            return 0;
        }
        if (player instanceof Player) {
            updateViewDelayed((Player) player);
        }

        switch (mode) {
            case REMOVE_MAX_POSSIBLE: {
                itemClone.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                if (map.isEmpty()) {
                    return amount;
                } else {
                    int left = map.get(0).getAmount();
                    if (VersionUtils.isVersionAfter(1, 9)) {
                        ItemStack[] extras = player.getInventory().getExtraContents();
                        for (int i = 0; i < extras.length; i++) {
                            ItemStack extra = extras[i];
                            if (extra != null && itemClone.isSimilar(extra)) {
                                int toRemove = Math.min(left, extra.getAmount());
                                left -= toRemove;
                                if (toRemove == extra.getAmount()) {
                                    extras[i] = null;
                                } else {
                                    extra.setAmount(extra.getAmount() - toRemove);
                                    extras[i] = extra;
                                }
                            }
                        }
                        player.getInventory().setExtraContents(extras);
                    }
                    return amount - left;
                }
            }
            case CANCEL: {
                if (player.getInventory().containsAtLeast(itemClone, amount)) {
                    itemClone.setAmount(amount);
                    HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                    if (map.isEmpty()) {
                        return amount;
                    }
                    return amount - map.get(0).getAmount();
                }
                return 0;
            }
            default: {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static Set<EquipmentSlot> loadPlayerEquipmentSlot() {
        EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        slots.add(EquipmentSlot.HEAD);
        slots.add(EquipmentSlot.CHEST);
        slots.add(EquipmentSlot.LEGS);
        slots.add(EquipmentSlot.FEET);
        slots.add(EquipmentSlot.HAND);
        try {
            slots.add(EquipmentSlot.valueOf("OFF_HAND"));
        } catch (Throwable ignored) {
            //1.8
        }
        return Collections.unmodifiableSet(slots);
    }

    public static @NotNull Set<EquipmentSlot> getPlayerEquipmentSlots() {
        return playerEquipmentSlots;
    }

    public static @Nullable ItemStack getItem(@NotNull Player player, @NotNull EquipmentSlot slot) {
        try {
            return player.getInventory().getItem(slot);
        } catch (Throwable ignored) {
        }
        EntityEquipment equip = player.getEquipment();
        if (equip != null) {
            switch (slot.name()) {
                case "HAND":
                    return equip.getItemInHand();
                case "LEGS":
                    return equip.getLeggings();
                case "CHEST":
                    return equip.getChestplate();
                case "HEAD":
                    return equip.getHelmet();
                case "FEET":
                    return equip.getBoots();
                case "OFF_HAND":
                    return equip.getItemInOffHand();
                default:
                    throw new UnsupportedOperationException();
            }
        }
        return null;
    }

    public enum ExcessMode {
        /**
         * drops if front of the player any items that can't be hold by the player
         */
        DROP_EXCESS,
        /**
         * remove any items that can't be hold by the player
         */
        DELETE_EXCESS,
        /**
         * if player has not enough space nothing is given to the player
         */
        CANCEL,
    }

    public enum LackMode {
        /**
         * remove the max number of items up to amount
         */
        REMOVE_MAX_POSSIBLE,
        /**
         * if there aren't enough items to remove, nothing is removed
         */
        CANCEL,
    }

}