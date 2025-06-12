package emanondev.itemedit.gui;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.SchedulerUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ColorListSelectorGui implements Gui {

    private static final String subPath = "gui.colorselector.";
    private final List<DyeColor> colors;
    private final Gui parent;
    private final Inventory inventory;

    /**
     * @param parent
     * @param colors original list to be changed
     */
    public ColorListSelectorGui(@NotNull Gui parent, @NotNull List<DyeColor> colors) {
        this.colors = colors;
        this.parent = parent;
        String title = getLanguageMessage(subPath + "title");
        this.inventory = Bukkit.createInventory(this, (2) * 9, title);
        inventory.setItem(inventory.getSize() - 1, this.getBackItem());
    }

    public void updateInventory() {
        int i = 0;
        List<String> list = new ArrayList<>();
        for (DyeColor c : colors)
            list.add(Aliases.COLOR.getName(c));
        for (DyeColor color : DyeColor.values()) {
            ItemStack item = Util.getDyeItemFromColor(color);
            ItemMeta meta = ItemUtils.getMeta(item);
            meta.addItemFlags(ItemFlag.values());
            loadLanguageDescription(meta, subPath + "buttons.color", "%colors%",
                    String.join("&b, &e", list), "%color%", Aliases.COLOR.getName(color));
            item.setItemMeta(meta);
            item.setAmount(Math.max(Math.min(101, colors.size()), 1));
            inventory.setItem(i, item);
            i++;
        }
        //inventory.setItem(inventory.getSize() - 1, this.getBackItem());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        SchedulerUtils.runLater(getPlugin(), 1L,
                () -> {
                    if (!InventoryUtils.getTopInventory(getTargetPlayer()).equals(parent.getInventory()))
                        getTargetPlayer().openInventory(parent.getInventory());
                });
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(parent.getTargetPlayer()))
            return;
        if (!inventory.equals(event.getClickedInventory()))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;
        if (event.getClick() == ClickType.DOUBLE_CLICK)
            return;
        if (event.getSlot() < DyeColor.values().length) {
            switch (event.getClick()) {
                case LEFT:
                    colors.add(DyeColor.values()[event.getSlot()]);
                    updateInventory();
                    break;
                case RIGHT:
                    colors.remove(colors.size() - 1);
                    updateInventory();
                    break;
                case SHIFT_RIGHT:
                    colors.clear();
                    updateInventory();
                    break;
            }
            return;
        }
        if (event.getSlot() == inventory.getSize() - 1)
            getTargetPlayer().openInventory(parent.getInventory());
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        updateInventory();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Player getTargetPlayer() {
        return parent.getTargetPlayer();
    }

    @Override
    public @NotNull APlugin getPlugin() {
        return parent.getPlugin();
    }
}
