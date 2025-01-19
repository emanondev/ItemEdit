package emanondev.itemedit.gui;

import emanondev.itemedit.utility.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class GuiHandler implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private static void onOpen(InventoryOpenEvent event) {
        Inventory topInventory = InventoryUtils.getTopInventory(event);
        if (!(topInventory.getHolder() instanceof Gui))
            return;
        if (!(event.getPlayer() instanceof Player))
            return;
        ((Gui) topInventory.getHolder()).onOpen(event);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private static void onClose(InventoryCloseEvent event) {
        Inventory topInventory = InventoryUtils.getTopInventory(event);
        if (!(topInventory.getHolder() instanceof Gui))
            return;
        if (!(event.getPlayer() instanceof Player))
            return;
        ((Gui) topInventory.getHolder()).onClose(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private static void onClick(InventoryClickEvent event) {
        Inventory topInventory = InventoryUtils.getTopInventory(event);
        if (!(topInventory.getHolder() instanceof Gui))
            return;
        event.setCancelled(true);
        if (event.getClickedInventory() != null && event.getClickedInventory().equals(topInventory))
            if (event.getWhoClicked() instanceof Player)
                ((Gui) topInventory.getHolder()).onClick(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private static void onDrag(InventoryDragEvent event) {
        Inventory topInventory = InventoryUtils.getTopInventory(event);
        if (topInventory.getHolder() instanceof Gui) {
            event.setCancelled(true);
            ((Gui) topInventory.getHolder()).onDrag(event);
        }
    }
}