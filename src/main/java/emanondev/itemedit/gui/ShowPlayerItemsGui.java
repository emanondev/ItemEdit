package emanondev.itemedit.gui;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.command.ItemStorageCommand;
import emanondev.itemedit.storage.PlayerStorage;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class ShowPlayerItemsGui implements PagedGui {


    private static final YMLConfig GUI_CONFIG = ItemEdit.get().getConfig();
    private final Inventory inventory;
    private final Player target;
    private final int page;
    private int rows;
    private ArrayList<String> ids;
    private boolean showItems = true;

    public ShowPlayerItemsGui(Player player, int page) {
        if (player == null)
            throw new NullPointerException();
        if (page < 1)
            throw new NullPointerException();

        this.target = player;
        rows = GUI_CONFIG.loadInteger("gui.playeritems.rows", 6);
        if (rows < 1 || rows > 5) {
            rows = Math.min(5, Math.max(1, rows));
        }
        PlayerStorage storage = ItemEdit.get().getPlayerStorage();
        ArrayList<String> list = new ArrayList<>(storage.getIds(target));
        Collections.sort(list);
        int maxPages = (list.size() - 1) / (rows * 9) + 1;
        if (page > maxPages)
            page = maxPages;
        this.page = page;

        String title = this.getLanguageMessage("gui.playeritems.title",
                "%player_name%", target.getName(), "%page%", String.valueOf(page));
        this.inventory = Bukkit.createInventory(this, (rows + 1) * 9, title);
        updateInventory();
        this.inventory.setItem(rows * 9 + 4, getPageInfoItem());
        if (page > 1)
            this.inventory.setItem(rows * 9 + 1, getPreviousPageItem());
        if (page < maxPages)
            this.inventory.setItem(rows * 9 + 7, getNextPageItem());
    }

    public void updateInventory() {
        PlayerStorage storage = ItemEdit.get().getPlayerStorage();
        ArrayList<String> list = new ArrayList<>(storage.getIds(target));
        ids = list;
        Collections.sort(list);
        for (int i = 0; i < rows * 9; i++) {
            int slot = rows * 9 * (page - 1) + i;
            if (slot >= list.size()) {
                this.inventory.setItem(i, null);
                continue;
            }
            ItemStack item = storage.getItem(target, list.get(slot));
            if (showItems) {
                this.inventory.setItem(i, item);
            } else {
                ItemStack display = item.clone();
                ItemMeta meta = ItemUtils.getMeta(display);
                meta.addItemFlags(ItemFlag.values());
                meta.setDisplayName(UtilsString.fix("&9ID: &e" + list.get(slot), null, true));
                display.setItemMeta(meta);
                this.inventory.setItem(i, display);
            }
        }
    }

    private ItemStack getPageInfoItem() {
        return this.loadLanguageDescription(this.getGuiItem("gui.playeritems.page-info", Material.NAME_TAG),
                "gui.playeritems.page-info.description", "%page%", String.valueOf(page));
    }

    /**
     * @return 1+
     */
    public int getPage() {
        return this.page;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(target))
            return;
        if (!inventory.equals(event.getClickedInventory()))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;
        if (event.getSlot() > inventory.getSize() - 9) {
            if (event.getSlot() == inventory.getSize() - 2)
                target.openInventory(new ShowPlayerItemsGui(target, page + 1).getInventory());
            else if (event.getSlot() == inventory.getSize() - 5) {
                showItems = !showItems;
                updateInventory();
            } else
                target.openInventory(new ShowPlayerItemsGui(target, page - 1).getInventory());
            return;
        }
        int slot = rows * 9 * (page - 1) + event.getSlot();
        String id = ids.get(slot);
        PlayerStorage storage = ItemEdit.get().getPlayerStorage();
        ItemStack item = storage.getItem(target, id);
        if (item == null || item.getType() == Material.AIR) {
            updateInventory();
            return;
        }
        switch (event.getClick()) {
            case LEFT:
                InventoryUtils.giveAmount(target, item, 1, InventoryUtils.ExcessMode.DELETE_EXCESS);
                return;
            case SHIFT_LEFT:
                InventoryUtils.giveAmount(target, item, 64, InventoryUtils.ExcessMode.DELETE_EXCESS);
                return;
            case SHIFT_RIGHT:
                if (!event.getWhoClicked().hasPermission("itemedit.itemstorage.delete")) {
                    ItemStorageCommand.get().sendPermissionLackMessage("itemedit.itemstorage.delete", event.getWhoClicked());
                    return;
                }
                storage.remove(target, id);
                updateInventory();
                return;
            default:
                break;
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public Player getTargetPlayer() {
        return this.target;
    }

    @Override
    public @NotNull ItemEdit getPlugin() {
        return ItemEdit.get();
    }

}
