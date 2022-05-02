package emanondev.itemedit.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PagedGui extends Gui {

    int getPage();

    default @NotNull ItemStack getPreviousPageItem() {
        return this.loadLanguageDescription(getGuiItem("buttons.previous-page", Material.BARRIER),
                "gui.previous-page.description", "%page%", String.valueOf(getPage()), "%target_page%", String.valueOf(getPage() - 1));
    }


    default @NotNull ItemStack getNextPageItem() {
        return this.loadLanguageDescription(getGuiItem("buttons.next-page", Material.BARRIER),
                "gui.next-page.description", "%page%", String.valueOf(getPage()), "%target_page%", String.valueOf(getPage() + 1));
    }

}
