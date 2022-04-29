package emanondev.itemedit.gui;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.YMLConfig;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public interface Gui extends InventoryHolder {

    void onClose(InventoryCloseEvent event);

    void onClick(InventoryClickEvent event);

    /**
     * This event is called when the player drags an item in their cursor across the
     * inventory. The ItemStack is distributed across the slots the HumanEntity
     * dragged over. The method of distribution is described by the DragType
     * returned by <a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryDragEvent.html#getType--"><code>getType()</code></a>.
     * <p>
     * Canceling this event will result in none of the changes described in <a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/InventoryDragEvent.html#getNewItems--"><code>getNewItems()</code></a>
     * being applied to the Inventory.
     * <p>
     * Because InventoryDragEvent occurs within a modification of the Inventory, not
     * all Inventory related methods are safe to use.
     * <p>
     * The following should never be invoked by an EventHandler for
     * InventoryDragEvent using the HumanEntity or InventoryView associated with
     * this event.
     * <ul>
     * <li><a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#closeInventory--"><code>HumanEntity.closeInventory()</code></a>
     * <li><a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openInventory-org.bukkit.inventory.Inventory-"><code>HumanEntity.openInventory(Inventory)</code></a>
     * <li><a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openWorkbench-org.bukkit.Location-boolean-"><code>HumanEntity.openWorkbench(Location, boolean)</code></a>
     * <li><a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/HumanEntity.html#openEnchanting-org.bukkit.Location-boolean-"><code>HumanEntity.openEnchanting(Location, boolean)</code></a>
     * <li><a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/InventoryView.html#close--"><code>InventoryView.close()</code></a>
     * </ul>
     * To invoke one of these methods, schedule a task using <a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
     * which will run the task on the next tick. Also be aware that this is not an
     * exhaustive list, and other methods could potentially create issues as well.
     * <p>
     * Assuming the EntityHuman associated with this event is an instance of a
     * Player, manipulating the MaxStackSize or contents of an Inventory will
     * require an Invocation of <a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/Player.html#updateInventory--"><code>Player.updateInventory()</code></a>.
     * <p>
     * Any modifications to slots that are modified by the results of this
     * InventoryDragEvent will be overwritten. To change these slots, this event
     * should be cancelled and the changes applied. Alternatively, scheduling a task
     * using <a href=
     * "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/scheduler/BukkitScheduler.html#runTask-org.bukkit.plugin.Plugin-java.lang.Runnable-"><code>BukkitScheduler.runTask(Plugin, Runnable)</code></a>,
     * which would execute the task on the next tick, would work as well.
     *
     * @param event - the event
     */
    void onDrag(InventoryDragEvent event);

    /**
     * @param event - the event
     */
    void onOpen(InventoryOpenEvent event);

    /**
     * @return button at slot position
     */
    @NotNull Inventory getInventory();

    Player getTargetPlayer();

    APlugin getPlugin();

    default String getLanguageMessage(String fullPath, String... holders) {
        return getPlugin().getLanguageConfig(getTargetPlayer()).loadMessage(fullPath, "", null, true, holders);
    }

    default List<String> getLanguageMultiMessage(String fullPath, String... holders) {
        return getPlugin().getLanguageConfig(getTargetPlayer())
                .loadMultiMessage(fullPath, Collections.emptyList(), null, true, holders);
    }

    default void loadLanguageDescription(ItemMeta meta, String fullPath, String... holders) {
        List<String> list = getPlugin().getLanguageConfig(getTargetPlayer()).loadMultiMessage(fullPath,
                null, getTargetPlayer(), true, holders);
        meta.setDisplayName(list == null || list.isEmpty() ? " " : list.get(0));
        if (list != null && !list.isEmpty())
            meta.setLore(list.subList(1, list.size()));
    }

    default ItemStack getGuiItem(String path, Material defMaterial) {
        return getGuiItem(path, defMaterial, 0);
    }

    default ItemStack getGuiItem(String path, Material defMaterial, int defDurability) {
        YMLConfig config = getPlugin().getConfig("gui.yml");
        ItemStack item = new ItemStack(config.loadMaterial(path + ".material", defMaterial));
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        if (config.getBoolean(path + ".glow", false))
            meta.addEnchant(Enchantment.DURABILITY, 1, true);

        item.setItemMeta(meta);
        int dur = config.loadInteger(path + ".durability", defDurability);
        if (dur > 0)
            item.setDurability((short) dur);
        return item;
    }

    default ItemStack getBackItem() {
        ItemStack item = getGuiItem("buttons.back", Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        this.loadLanguageDescription(meta, "gui.back.description");
        item.setItemMeta(meta);
        return item;
    }
}
