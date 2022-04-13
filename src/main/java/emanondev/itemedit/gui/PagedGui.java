package emanondev.itemedit.gui;

import emanondev.itemedit.YMLConfig;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface PagedGui extends Gui {

    int getPage();

    @SuppressWarnings("deprecation")
    default ItemStack getPreviousPageItem() {
        YMLConfig config = getPlugin().getConfig("gui.yml");
        ItemStack item = new ItemStack(config.loadMaterial("buttons.previous-page.material", Material.ARROW));
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        if (config.loadBoolean("buttons.playeritems.previous-page.glow", false))
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.loadLanguageDescription(meta, "buttons.previous-page.description", "%page%", String.valueOf(getPage()), "%target_page%",
                String.valueOf(getPage() + 1));
        item.setItemMeta(meta);
        int dur = config.loadInteger("buttons.playeritems.previous-page.durability", 0);
        if (dur > 0)
            item.setDurability((short) dur);
        return item;
    }

    @SuppressWarnings("deprecation")
    default ItemStack getNextPageItem() {
        YMLConfig config = getPlugin().getConfig("gui.yml");
        ItemStack item = new ItemStack(config.loadMaterial("gui.playeritems.next-page.material", Material.ARROW));
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.values());
        if (config.loadBoolean("gui.playeritems.next-page.glow", false))
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        this.loadLanguageDescription(meta, "gui.next-page.description",
                "%page%", String.valueOf(getPage()), "%target_page%",
                String.valueOf(getPage() + 1));
        item.setItemMeta(meta);
        int dur = config.loadInteger("gui.playeritems.next-page.durability", 0);
        if (dur > 0)
            item.setDurability((short) dur);
        return item;
    }
}
