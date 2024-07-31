package emanondev.itemedit.gui;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.command.ServerItemCommand;
import emanondev.itemedit.storage.ServerStorage;
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

public class ShowServerItemsGui implements PagedGui {
    private static final YMLConfig GUI_CONFIG = ItemEdit.get().getConfig();
    private final Inventory inventory;
    private final Player target;
    private final int page;
    private int rows;
    private ArrayList<String> ids;
    private boolean showItems = true;

    public ShowServerItemsGui(Player player, int page) {
        if (player == null)
            throw new NullPointerException();
        if (page < 1)
            throw new NullPointerException();

        this.target = player;
        rows = GUI_CONFIG.loadInteger("gui.serveritems.rows", 6);
        if (rows < 1 || rows > 5) {
            rows = Math.min(5, Math.max(1, rows));
        }
        ServerStorage storage = ItemEdit.get().getServerStorage();
        ArrayList<String> list = new ArrayList<>(storage.getIds());
        Collections.sort(list);
        int maxPages = (list.size()) / (rows * 9) + ((list.size()) % (rows * 9) == 0 ? 0 : 1);
        if (page > maxPages)
            page = maxPages;
        this.page = page;

        String title = UtilsString.fix(GUI_CONFIG.loadMessage("gui.serveritems.title", "", false), player, true,
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
        ServerStorage storage = ItemEdit.get().getServerStorage();
        ArrayList<String> list = new ArrayList<>(storage.getIds());
        ids = list;
        Collections.sort(list);
        for (int i = 0; i < rows * 9; i++) {
            int slot = rows * 9 * (page - 1) + i;
            if (slot >= list.size()) {
                this.inventory.setItem(i, null);
                continue;
            }
            ItemStack item = storage.getItem(list.get(slot));
            if (item == null) {
                new NullPointerException("invalid id " + list.get(slot)).printStackTrace();
                continue;
            }
            if (showItems) {
                this.inventory.setItem(i, item);
            } else {
                ItemStack display = item.clone();
                ItemMeta meta = display.getItemMeta();
                meta.addItemFlags(ItemFlag.values());
                meta.setLore(Collections.singletonList(UtilsString.fix("&9Nick: &e" + storage.getNick(list.get(slot)), null, true)));
                meta.setDisplayName(UtilsString.fix("&9ID: &e" + list.get(slot), null, true));
                display.setItemMeta(meta);
                this.inventory.setItem(i, display);
            }
        }
    }

    private ItemStack getPageInfoItem() {
        return this.loadLanguageDescription(this.getGuiItem("gui.serveritems.page-info", Material.NAME_TAG),
                "gui.serveritems.page-info.description", "%player_name%", target.getName(), "%page%", String.valueOf(page));
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
            switch (inventory.getSize() - event.getSlot()) {
                case 2:
                    target.openInventory(new ShowServerItemsGui(target, page + 1).getInventory());
                    return;
                case 5:
                    showItems = !showItems;
                    updateInventory();
                    return;
                default:
                    target.openInventory(new ShowServerItemsGui(target, page - 1).getInventory());
                    return;
            }
        }
        int slot = rows * 9 * (page - 1) + event.getSlot();
        String id = ids.get(slot);
        ServerStorage storage = ItemEdit.get().getServerStorage();
        ItemStack item = storage.getItem(id);
        if (item == null || item.getType() == Material.AIR) {
            updateInventory();
            return;
        }
        switch (event.getClick()) {
            case LEFT:
                UtilsInventory.giveAmount(target, item, 1, ExcessManage.DELETE_EXCESS);
                return;
            case SHIFT_LEFT:
                UtilsInventory.giveAmount(target, item, 64, ExcessManage.DELETE_EXCESS);
                return;
            case SHIFT_RIGHT:
                if (!event.getWhoClicked().hasPermission("itemedit.serveritem.delete")) {
                    ServerItemCommand.get().sendPermissionLackMessage("itemedit.serveritem.delete", event.getWhoClicked());
                    return;
                }
                storage.remove(id);
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
