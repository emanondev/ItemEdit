package emanondev.itemedit.compability;

import emanondev.itemedit.ItemEdit;
import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.provider.item.ItemProvider;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class ShopGuiPlusItemProvider extends ItemProvider {

    public ShopGuiPlusItemProvider() {
        super("ServerItem");
    }

    @Override
    public boolean isValidItem(ItemStack item) {
        return getCustomId(item) != null;
    }

    @Override
    public ItemStack loadItem(ConfigurationSection section) {
        String id = section.getString("serveritem");
        if (id == null) {
            for (String key : section.getKeys(false))
                if (key.equalsIgnoreCase("serveritem")) {
                    id = section.getString(key);
                    break;
                }
            if (id == null)
                return null;
        }
        try {
            ItemStack result = ItemEdit.get().getServerStorage().getItem(id);
            if (result != null) {
                result.setAmount(section.getInt("quantity", 1));
                return result;
            }
            ItemEdit.get().log("Invalid ServerItem id on ShopGuiPlus config for &e" + id + " &fon path &e"
                    + section.getCurrentPath() + ".serveritem");
            return null;
        } catch (Exception e) {
            ItemEdit.get().log("Invalid ServerItem id on ShopGuiPlus config for &e" + id + " &fon path &e"
                    + section.getCurrentPath() + ".serveritem");
        }
        return null;
    }

    @Override
    public boolean compare(ItemStack item1, ItemStack item2) {
        String id1 = getCustomId(item1);
        if (id1 == null)
            return false;
        return id1.equals(getCustomId(item2));
    }

    private String getCustomId(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;
        return ItemEdit.get().getServerStorage().getId(item);
    }

    public void register() {
        ShopGuiPlusApi.registerItemProvider(this);
    }
}