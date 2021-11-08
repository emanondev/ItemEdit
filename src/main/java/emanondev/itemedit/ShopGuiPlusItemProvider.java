package emanondev.itemedit;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import net.brcdev.shopgui.ShopGuiPlusApi;
import net.brcdev.shopgui.provider.item.ItemProvider;

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
		String id = section.getString("serverItem");
		try {
			ItemStack result = ItemEdit.get().getServerStorage().getItem(id);
			if (result == null) {
				ItemEdit.get().log("Invalid ServerItem id on ShopGuiPlus config for &e"+id);
			}
			return result;
		} catch (Exception e) {
			ItemEdit.get().log("Invalid ServerItem id on ShopGuiPlus config for &e"+id);
		}
		return null;
	}

	@Override
	public boolean compare(ItemStack item1, ItemStack item2) {
		String id1 = getCustomId(item1);
		if (id1==null)
			return false;
		return id1.equals(getCustomId(item2));
	}

	private String getCustomId(ItemStack item) {
		if (item==null)
			return null;
		if (item.getType()==Material.AIR)
			return null;
		for (String id:ItemEdit.get().getServerStorage().getIds()) {
			if (item.isSimilar(ItemEdit.get().getServerStorage().getItem(id)) )
				return id;
		}
		return null;
	  }

	public void register() {
		ShopGuiPlusApi.registerItemProvider(this);
	}
}