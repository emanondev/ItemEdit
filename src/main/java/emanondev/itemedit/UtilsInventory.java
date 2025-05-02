package emanondev.itemedit;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * @see emanondev.itemedit.utility.InventoryUtils InventoryUtils
 */
@Deprecated
public class UtilsInventory {

    private UtilsInventory() {
        throw new UnsupportedOperationException();
    }

    /**
     * @param player - the player
     * @param item   - item to be give, note: item.getAmount() is ignored
     * @param amount - amount of item to be given
     * @param mode   - how to handle special cases
     * @return the amount given (or given + dropped)
     * @throws NullPointerException     if player is null
     * @throws NullPointerException     if item is null
     * @throws NullPointerException     if mode is null
     * @throws IllegalArgumentException if amount is negative
     */
    public static int giveAmount(@NotNull HumanEntity player, @NotNull ItemStack item, final int amount,
                                 @NotNull final ExcessManage mode) {
        if (amount < 0)
            throw new IllegalArgumentException("negative amount");
        item = item.clone();
        if (amount == 0)
            return 0;
        int remains = amount;
        while (remains > 0) {
            item.setAmount(Math.min(item.getMaxStackSize(), remains));
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(item);
            remains = remains - Math.min(item.getMaxStackSize(), remains);
            if (map.isEmpty())
                continue;
            remains = remains + map.get(0).getAmount();
            break;
        }

        InventoryUtils.updateViewDelayed((Player) player);

        if (remains == 0)
            return amount;

        switch (mode) {
            case DELETE_EXCESS:
                return amount - remains;
            case DROP_EXCESS:
                while (remains > 0) {
                    int drop = Math.min(remains, 64);
                    item.setAmount(drop);
                    @NotNull ItemStack finalItem = item;
                    SchedulerUtils.run(ItemEdit.get(), player.getEyeLocation(), () -> player.getWorld().dropItem(player.getEyeLocation(), finalItem));

                    remains -= drop;
                }
                return amount;
            case CANCEL:
                removeAmount(player, item, amount - remains, LackManage.REMOVE_MAX_POSSIBLE);
                return 0;
        }
        throw new IllegalArgumentException();
    }

    /**
     * @param player - the player
     * @param item   - item to be give, note: item.getAmount() is ignored
     * @param amount - amount of item to be given
     * @param mode   - how to handle special cases
     * @return the removed amount
     * @throws NullPointerException     if player is null
     * @throws NullPointerException     if item is null
     * @throws NullPointerException     if mode is null
     * @throws IllegalArgumentException if amount is negative
     */
    public static int removeAmount(@NotNull HumanEntity player, @NotNull ItemStack item, final int amount,
                                   @NotNull final LackManage mode) {
        if (amount < 0)
            throw new IllegalArgumentException("negative amount");
        item = item.clone();
        if (amount == 0)
            return 0;
        switch (mode) {
            case REMOVE_MAX_POSSIBLE: {
                item.setAmount(amount);
                HashMap<Integer, ItemStack> map = player.getInventory().removeItem(item);

                InventoryUtils.updateViewDelayed((Player) player);

                if (map.isEmpty())
                    return amount;
                else {
                    int left = map.get(0).getAmount();
                    if (VersionUtils.isVersionAfter(1, 9)) {
                        ItemStack[] extras = player.getInventory().getExtraContents();
                        for (int i = 0; i < extras.length; i++) {
                            ItemStack extra = extras[i];
                            if (extra != null && item.isSimilar(extra)) {
                                int toRemove = Math.min(left, extra.getAmount());
                                left -= toRemove;
                                if (toRemove == extra.getAmount())
                                    extras[i] = null;
                                else {
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
                if (player.getInventory().containsAtLeast(item, amount)) {
                    item.setAmount(amount);
                    HashMap<Integer, ItemStack> map = player.getInventory().removeItem(item);

                    InventoryUtils.updateViewDelayed((Player) player);

                    if (map.isEmpty())
                        return amount;
                    else
                        return amount - map.get(0).getAmount();
                }

                return 0;
            }
        }
        throw new IllegalArgumentException();
    }

    public enum ExcessManage {
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

    public enum LackManage {
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
