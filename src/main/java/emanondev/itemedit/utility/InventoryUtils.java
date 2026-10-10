package emanondev.itemedit.utility;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

public final class InventoryUtils {

    private static final Set<EquipmentSlot> playerEquipmentSlots = loadPlayerEquipmentSlot();

    private InventoryUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.<br>
     * Also, ViaBackwards and ViaVersion may cause desyncronization of inventory
     *
     * @param player The player which inventory view should be updated
     */
    public static void updateView(@NotNull Player player) {
        SchedulerUtils.run(ItemEdit.get(), player, player::updateInventory);
    }

    /**
     * Update InventoryView for player.<br><br>
     * In API versions 1.19.3 and earlier, there is no implicit consistency for inventory and
     * changes, so it has to be done manually (also Purpur has similar issue in later versions too).<br>
     * In API versions 1.19.4 and later, there is implicit consistency for inventory and changes.<br>
     * Also, ViaBackwards and ViaVersion may cause desyncronization of inventory
     *
     * @param player The player which inventory view should be updated
     */
    public static void updateViewDelayed(@NotNull Player player) {
        SchedulerUtils.runLater(ItemEdit.get(), player, 1L, player::updateInventory);
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

        return switch (mode) {
            case DELETE_EXCESS -> amount - remains;
            case DROP_EXCESS -> {
                while (remains > 0) {
                    int drop = Math.min(remains, 64);
                    itemClone.setAmount(drop);
                    ItemStack itemCopy = new ItemStack(itemClone);
                    Location loc = player.getEyeLocation();
                    SchedulerUtils.run(ItemEdit.get(), loc,
                            () -> player.getWorld().dropItem(loc, itemCopy));
                    remains -= drop;
                }
                yield amount;
            }
            case CANCEL -> {
                removeAmount(player, itemClone, amount - remains, LackMode.REMOVE_MAX_POSSIBLE);
                yield 0;
            }
        };
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

        return switch (mode) {
            case REMOVE_MAX_POSSIBLE -> {
                itemClone.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                if (map.isEmpty()) {
                    yield amount;
                }

                int left = map.get(0).getAmount();
                if (VersionUtils.isAfter(1, 9)) {
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
                yield amount - left;

            }
            case CANCEL -> {
                if (player.getInventory().containsAtLeast(itemClone, amount)) {
                    itemClone.setAmount(amount);
                    HashMap<Integer, ItemStack> map = player.getInventory().removeItem(itemClone);

                    if (map.isEmpty()) {
                        yield amount;
                    }
                    yield amount - map.get(0).getAmount();
                }
                yield 0;
            }
        };
    }

    public static @NotNull Set<EquipmentSlot> getPlayerEquipmentSlots() {
        return playerEquipmentSlots;
    }

    private static Set<EquipmentSlot> loadPlayerEquipmentSlot() {
        EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        slots.add(EquipmentSlot.HEAD);
        slots.add(EquipmentSlot.CHEST);
        slots.add(EquipmentSlot.LEGS);
        slots.add(EquipmentSlot.FEET);
        slots.add(EquipmentSlot.HAND);
        slots.add(EquipmentSlot.OFF_HAND);
        return Collections.unmodifiableSet(slots);
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