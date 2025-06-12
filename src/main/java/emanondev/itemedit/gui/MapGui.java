package emanondev.itemedit.gui;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.gui.button.Button;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class MapGui implements Gui {

    private final HashMap<Integer, Button> buttons = new HashMap<>();
    private final Inventory inv;
    private final Player targetPlayer;
    private final APlugin plugin;

    public MapGui(@NotNull APlugin plugin,
                  @NotNull Player target,
                  @Nullable String title,
                  int rows) {
        this.plugin = plugin;
        this.targetPlayer = target;
        this.inv = Bukkit.createInventory(this, rows * 9,
                UtilsString.fix(title == null ? "" : title, target, true));
    }


    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (buttons.containsKey(event.getSlot())) {
            if (buttons.get(event.getSlot()).onClick(event)) {
                updateInventory();
            }
        }
    }

    public void registerButton(int slot, Button button) {
        buttons.put(slot, button);
    }

    public void updateInventory() {
        buttons.forEach((slot, button) -> {
            if (slot < 0 || slot >= getInventory().getSize()) {
                return;
            }
            getInventory().setItem(slot, button.getItem());
        });
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
        return inv;
    }

    @Override
    public @NotNull Player getTargetPlayer() {
        return targetPlayer;
    }

    @Override
    public @NotNull APlugin getPlugin() {
        return plugin;
    }

    public void openGui() {
        if (targetPlayer.isValid()) {
            targetPlayer.openInventory(inv);
        }
    }
}
