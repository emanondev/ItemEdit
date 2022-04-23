package emanondev.itemedit.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface PagedGui extends Gui {

    int getPage();

    default ItemStack getPreviousPageItem() {
        ItemStack item = getGuiItem("buttons.previous-page", Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        this.loadLanguageDescription(meta, "buttons.previous-page.description", "%page%", String.valueOf(getPage()), "%target_page%",
                String.valueOf(getPage() - 1));
        item.setItemMeta(meta);
        return item;
    }


    default ItemStack getNextPageItem() {
        ItemStack item = getGuiItem("buttons.next-page", Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        this.loadLanguageDescription(meta, "buttons.next-page.description", "%page%", String.valueOf(getPage()), "%target_page%",
                String.valueOf(getPage() + 1));
        item.setItemMeta(meta);
        return item;
    }

}
