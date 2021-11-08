package emanondev.itemedit.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.storage.ServerStorage;

public class ShowServerItemsGui implements Gui {
	private final Inventory inventory;
	private final Player target;
	private final int page;
	private static final YMLConfig config = ItemEdit.get().getConfig();
	private int rows;
	private ArrayList<String> ids;
	private boolean showItems = true;

	public void updateInventory() {
		ServerStorage stor = ItemEdit.get().getServerStorage();
		ArrayList<String> list = new ArrayList<>(stor.getIds());
		ids = list;
		Collections.sort(list);
		for (int i = 0; i < rows * 9; i++) {
			int slot = rows * 9 * (page - 1) + i;
			if (slot >= list.size()) {
				this.inventory.setItem(i, null);
				continue;
			}
			ItemStack item = stor.getItem(list.get(slot));
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
				meta.setLore(Arrays.asList(UtilsString.fix("&9Nick: &e" + stor.getNick(list.get(slot)), null, true)));
				meta.setDisplayName(UtilsString.fix("&9ID: &e" + list.get(slot), null, true));
				display.setItemMeta(meta);
				this.inventory.setItem(i, display);
			}
		}
	}

	public ShowServerItemsGui(Player player, int page) {
		if (player == null)
			throw new NullPointerException();
		if (page < 1)
			throw new NullPointerException();

		this.target = player;
		rows = config.loadInteger("gui.serveritems.rows", 6);
		if (rows < 1 || rows > 5) {
			rows = Math.min(5, Math.max(1, rows));
		}
		ServerStorage stor = ItemEdit.get().getServerStorage();
		ArrayList<String> list = new ArrayList<>(stor.getIds());
		Collections.sort(list);
		int maxPages = (list.size()) / (rows * 9) + ((list.size()) % (rows * 9) == 0 ? 0 : 1);
		if (page > maxPages)
			page = maxPages;
		this.page = page;

		String title = UtilsString.fix(config.loadString("gui.serveritems.title", "", false), player, true,
				"%player_name%", target.getName(), "%page%", String.valueOf(page));
		this.inventory = Bukkit.createInventory(this, (rows + 1) * 9, title);
		updateInventory();
		this.inventory.setItem(rows * 9 + 4, getPageInfoItem());
		if (page > 1)
			this.inventory.setItem(rows * 9+1, getPreviusPageItem());
		if (page < maxPages)
			this.inventory.setItem(rows * 9 + 7, getNextPageItem());
	}

	@SuppressWarnings("deprecation")
	private ItemStack getPageInfoItem() {
		ItemStack item = new ItemStack(config.loadMaterial("gui.serveritems.page-info.material", Material.NAME_TAG));
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		if (config.loadBoolean("gui.serveritems.page-info.glow", false))
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		ArrayList<String> desc = new ArrayList<>(UtilsString.fix(
				config.loadStringList("gui.serveritems.page-info.description", new ArrayList<>(), false), target, true,
				"%player_name%", target.getName(), "%page%", String.valueOf(page)));
		if (desc.size() > 1)
			meta.setDisplayName(desc.remove(0));
		else
			meta.setDisplayName("");
		if (desc.size() > 0)
			meta.setLore(desc);
		item.setItemMeta(meta);
		int dur = config.loadInteger("gui.serveritems.page-info.durability", 0);
		if (dur > 0)
			item.setDurability((short) dur);
		return item;

	}

	@SuppressWarnings("deprecation")
	private ItemStack getPreviusPageItem() {
		ItemStack item = new ItemStack(config.loadMaterial("gui.serveritems.previus-page.material", Material.ARROW));
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		if (config.loadBoolean("gui.serveritems.previus-page.glow", false))
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		ArrayList<String> desc = new ArrayList<>(UtilsString.fix(
				config.loadStringList("gui.serveritems.previus-page.description", new ArrayList<>(), false), target,
				true, "%player_name%", target.getName(), "%page%", String.valueOf(page), "%target_page%",
				String.valueOf(page - 1)));
		if (desc.size() > 1)
			meta.setDisplayName(desc.remove(0));
		if (desc.size() > 0)
			meta.setLore(desc);
		item.setItemMeta(meta);
		int dur = config.loadInteger("gui.serveritems.previus-page.durability", 0);
		if (dur > 0)
			item.setDurability((short) dur);
		return item;
	}

	@SuppressWarnings("deprecation")
	private ItemStack getNextPageItem() {
		ItemStack item = new ItemStack(config.loadMaterial("gui.serveritems.next-page.material", Material.ARROW));
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		if (config.loadBoolean("gui.serveritems.next-page.glow", false))
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
		ArrayList<String> desc = new ArrayList<>(UtilsString.fix(
				config.loadStringList("gui.serveritems.next-page.description", new ArrayList<>(), false), target, true,
				"%player_name%", target.getName(), "%page%", String.valueOf(page), "%target_page%",
				String.valueOf(page + 1)));
		if (desc.size() > 1)
			meta.setDisplayName(desc.remove(0));
		if (desc.size() > 0)
			meta.setLore(desc);
		item.setItemMeta(meta);
		int dur = config.loadInteger("gui.serveritems.next-page.durability", 0);
		if (dur > 0)
			item.setDurability((short) dur);
		return item;
	}

	/**
	 * 
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
		ServerStorage stor = ItemEdit.get().getServerStorage();
		ItemStack item = stor.getItem(id);
		if (item == null || item.getType() == Material.AIR) {
			updateInventory();
			return;
		}
		switch (event.getClick()) {
		case CREATIVE:
			break;
		case LEFT:
			UtilsInventory.giveAmount(target, item, 1, ExcessManage.DELETE_EXCESS);
			return;
		case MIDDLE:
			break;
		case RIGHT:
			break;
		case SHIFT_LEFT:
			UtilsInventory.giveAmount(target, item, 64, ExcessManage.DELETE_EXCESS);
			return;
		case SHIFT_RIGHT:
			if (!event.getWhoClicked().hasPermission("itemedit.serveritem.delete")) {
				Util.sendMessage(event.getWhoClicked(),
						ItemEdit.get().getConfig("serveritem")
								.loadString("lack-permission", "&cYou lack of permission %permission%", true)
								.replace("%permission%", "itemedit.serveritem.delete"));
				return;
			}
			stor.remove(id);
			updateInventory();
			return;
		case UNKNOWN:
			break;
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
	public Inventory getInventory() {
		return this.inventory;
	}

	@Override
	public Player getTargetPlayer() {
		return this.target;
	}

}
