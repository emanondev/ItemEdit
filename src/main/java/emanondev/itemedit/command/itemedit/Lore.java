package emanondev.itemedit.command.itemedit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.BaseComponent;

public class Lore extends SubCmd {

	// private BaseComponent[] helpAdd;
	private final Map<UUID,List<String>> copies = new HashMap<>();
	private BaseComponent[] helpSet;
	private BaseComponent[] helpRemove;
	private BaseComponent[] helpInsert;
	private String copyFeedback;
	private String pasteFeedback;
	private String pasteNoCopyFeedback;
	
	private static final String[] loreSub = new String[] { "add", "set", "remove", "reset", "insert","copy","paste" };

	public Lore(ItemEditCommand cmd) {
		super("lore", cmd, true, true);
		load();
	}

	private void load() {
		this.helpSet = this.craftFailFeedback(getConfString("set.params"),
				String.join("\n", getConfStringList("set.description")));
		this.helpRemove = this.craftFailFeedback(getConfString("remove.params"),
				String.join("\n", getConfStringList("remove.description")));
		this.helpInsert = this.craftFailFeedback(getConfString("insert.params"),
				String.join("\n", getConfStringList("insert.description")));
		copyFeedback = this.getConfString("copy.feedback");
		pasteFeedback = this.getConfString("paste.feedback");
		pasteNoCopyFeedback = this.getConfString("paste.no-copy");
	}

	public void reload() {
		super.reload();
		load();
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		if (args.length == 1) {
			onFail(p);
			return;
		}

		switch (args[1].toLowerCase()) {
		case "set":
			loreSet(p, item, args);
			return;
		case "add":
			loreAdd(p, item, args);
			return;
		case "insert":
			loreInsert(p, item, args);
			return;
		case "reset":
			loreReset(p, item, args);
			return;
		case "remove":
			loreRemove(p, item, args);
			return;
		case "copy":
			loreCopy(p, item, args);
			return;
		case "paste":
			lorePaste(p, item, args);
			return;
		default:
			onFail(p);
		}
	}

	private void lorePaste(Player p, ItemStack item, String[] args) {
		if (!copies.containsKey(p.getUniqueId())) {
			p.sendMessage(pasteNoCopyFeedback);
			return;
		}
		ItemMeta meta = item.getItemMeta();
		meta.setLore(copies.get(p.getUniqueId()));
		item.setItemMeta(meta);
		if (pasteFeedback!=null)
			p.sendMessage(pasteFeedback);
		p.updateInventory();
	}

	private void loreCopy(Player p, ItemStack item, String[] args) {
		
		List<String> lore;
		if (item.hasItemMeta()) {
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMeta.hasLore())
				lore = new ArrayList<String>(itemMeta.getLore());
			else
				lore = new ArrayList<String>();
		}
		else
			lore = new ArrayList<String>();

		copies.put(p.getUniqueId(), lore);

		if (copyFeedback!=null)
			p.sendMessage(copyFeedback);
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], loreSub);
		if (args.length == 3) {
			switch (args[1].toLowerCase()) {
			case "remove":
			case "set":
				return Util.complete(args[2],Arrays.asList( "1","2","3","last"));
			}
		}
		if (args.length == 4) {
			switch (args[1].toLowerCase()) {
			case "set":
				if (sender instanceof Player) {
					ItemStack item = this.getItemInHand((Player) sender);
					if (item!=null && item.hasItemMeta()) {
						ItemMeta meta = item.getItemMeta();
						if (meta.hasLore()) {

							List<String> lore = meta.getLore();
							
							int line = Integer.parseInt(args[2]) - 1;
							if (args[2].equalsIgnoreCase("last"))
								line = lore.size()-1;
							else
								line = Integer.parseInt(args[2]) - 1;
							if (line <0 || line >= lore.size())
								return Collections.emptyList();
							return Util.complete(args[3], lore.get(line).replace('§','&'));
						}
					}
				}
			}
		}
		return Collections.emptyList();
	}

	// /itemedit lore add
		private void loreAdd(Player p, ItemStack item, String[] args) {

			String text = "";
			if (args.length > 2) {
				text = args[2];
				for (int i = 3; i < args.length; i++)
					text = text + " " + args[i];
				// text = ChatColor.translateAlternateColorCodes('&', text);
			}

			ItemMeta itemMeta = item.getItemMeta();

			List<String> lore;
			if (itemMeta.hasLore())
				lore = new ArrayList<String>(itemMeta.getLore());
			else
				lore = new ArrayList<String>();

			text = Util.formatText(p, text, getPermission());
			if (Util.hasBannedWords(p, text))
				return;

			lore.add(text);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			p.updateInventory();
		}
		// /itemedit lore insert [line] [text]
		private void loreInsert(Player p, ItemStack item, String[] args) {
			try {
				if (args.length < 3)
					throw new IllegalArgumentException("Wrong param number");

				String text = "";
				if (args.length > 3) {
					text = args[3];
					for (int i = 4; i < args.length; i++)
						text = text + " " + args[i];
					// text = ChatColor.translateAlternateColorCodes('&', text);
				}

				int line = Integer.parseInt(args[2]) - 1;
				if (line < 0)
					throw new IllegalArgumentException("Wrong line number");
				ItemMeta itemMeta = item.getItemMeta();

				List<String> lore;
				if (itemMeta.hasLore())
					lore = new ArrayList<String>(itemMeta.getLore());
				else
					lore = new ArrayList<String>();

				for (int i = lore.size(); i <= line; i++)
					lore.add("");

				text = Util.formatText(p, text, getPermission());
				if (Util.hasBannedWords(p, text))
					return;

				lore.add(line, text);
				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);
				p.updateInventory();
			} catch (Exception e) {
				p.spigot().sendMessage(helpInsert);
			}
		}

	// lore set line text
	private void loreSet(Player p, ItemStack item, String[] args) {
		try {
			if (args.length < 3)
				throw new IllegalArgumentException("Wrong param number");

			String text = "";
			if (args.length > 3) {
				text = args[3];
				for (int i = 4; i < args.length; i++)
					text = text + " " + args[i];
				// text = ChatColor.translateAlternateColorCodes('&', text);
			}

			ItemMeta itemMeta = item.getItemMeta();

			List<String> lore;
			if (itemMeta.hasLore())
				lore = new ArrayList<String>(itemMeta.getLore());
			else
				lore = new ArrayList<String>();
			int line = Integer.parseInt(args[2]) - 1;
			if (args[2].equalsIgnoreCase("last"))
				line = lore.size()-1;
			else
				line = Integer.parseInt(args[2]) - 1;
			if (line < 0)
				throw new IllegalArgumentException("Wrong line number");

			for (int i = lore.size(); i <= line; i++)
				lore.add("");

			text = Util.formatText(p, text, getPermission());
			if (Util.hasBannedWords(p, text))
				return;

			lore.set(line, text);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			p.updateInventory();
		} catch (Exception e) {
			p.spigot().sendMessage(helpSet);
		}
	}

	private void loreRemove(Player p, ItemStack item, String[] args) {
		try {
			if (args.length < 3)
				throw new IllegalArgumentException("Wrong param number");
			if (!item.hasItemMeta())
				return;
			ItemMeta itemMeta = item.getItemMeta();
			if (!itemMeta.hasLore() || itemMeta.getLore().size()==0)
				return;
			List<String> lore = new ArrayList<String>(itemMeta.getLore());
			int line;
			if (args[2].equalsIgnoreCase("last"))
				line = lore.size()-1;
			else
				line = Integer.parseInt(args[2]) - 1;
			if (line < 0)
				throw new IllegalArgumentException("Wrong line number");


			if (lore.size() < line)
				return;

			lore.remove(line);
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);
			p.updateInventory();
		} catch (Exception e) {
			p.spigot().sendMessage(helpRemove);
		}
	}

	private void loreReset(Player p, ItemStack item, String[] args) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(null);
		item.setItemMeta(meta);
		p.updateInventory();
	}
}
