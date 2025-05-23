package emanondev.itemedit;

import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UtilLegacy {

    private static final Map<Class<?>, Method> getTopInventory =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private static final Map<Class<?>, Method> getBottomInventory =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();

    /**
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.<br><br>
     * In API versions 1.20.6 and earlier, InventoryView is a class.<br>
     * In API versions 1.21 and later, it is an interface.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     * @see emanondev.itemedit.utility.InventoryUtils#getTopInventory(InventoryEvent) InventoryUtils.getTopInventory(InventoryEvent)
     */
    @Deprecated
    public static Inventory getTopInventory(@NotNull InventoryEvent event) {
        if (VersionUtils.isVersionAfter(1, 21))
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
     * @see emanondev.itemedit.utility.InventoryUtils#getTopInventory(Player) InventoryUtils.getTopInventory(Player)
     */
    @Deprecated
    public static Inventory getTopInventory(@NotNull Player player) {
        if (VersionUtils.isVersionAfter(1, 21))
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
     * @see emanondev.itemedit.utility.InventoryUtils#getBottomInventory(InventoryEvent) InventoryUtils.getBottomInventory(InventoryEvent)
     */
    @Deprecated
    public static Inventory getBottomInventory(@NotNull InventoryEvent event) {
        if (VersionUtils.isVersionAfter(1, 21))
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
     * @see emanondev.itemedit.utility.InventoryUtils#updateView(Player) InventoryUtils.updateView(Player)
     */
    @Deprecated
    public static void updateView(@NotNull Player player) {
        if (VersionUtils.isVersionUpTo(1, 19, 4) || Util.hasPurpurAPI()) {
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
     * @see emanondev.itemedit.utility.InventoryUtils#updateViewDelayed(Player) InventoryUtils.updateViewDelayed(Player)
     */
    @Deprecated
    public static void updateViewDelayed(@NotNull Player player) {
        if (VersionUtils.isVersionUpTo(1, 19, 4) || Util.hasPurpurAPI()) {
            SchedulerUtils.runLater(ItemEdit.get(), 1L, player::updateInventory);
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
        else if (!VersionUtils.isVersionAfter(1, 19, 4))
            duration = Integer.MAX_VALUE;
        else
            duration = -1;
        return duration;
    }
}
