package emanondev.itemedit.command;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;

public class ItemEditImportCommand implements TabExecutor {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 1)
			return Util.complete(args[0], Arrays.asList("itemeditor"));
		return Collections.emptyList();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("itemedit.itemeditimport")) {
			if (args.length == 0) {
				Util.sendMessage(sender, String.join("\n", ItemEdit.get().getConfig("itemeditimport.yml")
						.loadStringList("help", new ArrayList<>(), true)));
				return true;
			}
			switch (args[0].toLowerCase()) {
			case "itemeditor": {
				if ((new File("plugins/ItemEditor/items")).listFiles() != null
						&& ((new File("plugins/ItemEditor/items")).listFiles()).length != 0) {
					List<String> importedIds = new ArrayList<>();
					int max = (new File("plugins/ItemEditor/items")).listFiles().length;
					for (File file : (new File("plugins/ItemEditor/items")).listFiles()) {
						String name = file.getName().replace(".yml", "");
						try {
							ItemEdit.get().getServerStorage().validateID(name);
						} catch (Exception e) {
							Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditimport.yml")
									.loadString("itemeditor.invalid-id", "", true).replace("%id%", name));
							continue;
						}
						if (ItemEdit.get().getServerStorage().getItem(name) != null) {
							Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditimport.yml")
									.loadString("itemeditor.already-used-id", "", true).replace("%id%", name));
							continue;
						}

						try {
							ItemStack item = fromBase64(YamlConfiguration.loadConfiguration(file).getString("Item"));
							ItemEdit.get().getServerStorage().setItem(name, item);
							importedIds.add(name);
						} catch (Exception e) {
							Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditimport.yml")
									.loadString("itemeditor.unable-to-get-item", "", true).replace("%id%", name));
							e.printStackTrace();
							continue;
						}

					}
					if (importedIds.isEmpty()) {
						Util.sendMessage(
								sender, String
										.join("\n",
												ItemEdit.get().getConfig("itemeditimport.yml").loadStringList(
														"itemeditor.import-unsuccess", new ArrayList<>(), true))
										.replace("%ids%", String.join(", ", importedIds))
										.replace("%max%", String.valueOf(max))
										.replace("%done%", String.valueOf(importedIds.size())));
					} else {
						Util.sendMessage(
								sender, String
										.join("\n",
												ItemEdit.get().getConfig("itemeditimport.yml").loadStringList(
														"itemeditor.import-success", new ArrayList<>(), true))
										.replace("%ids%", String.join(", ", importedIds))
										.replace("%max%", String.valueOf(max))
										.replace("%done%", String.valueOf(importedIds.size())));
					}

				} else {
					Util.sendMessage(sender, String.join("\n", ItemEdit.get().getConfig("itemeditimport.yml")
							.loadStringList("itemeditor.import-empty", new ArrayList<>(), true)));
				}
				return true;
			}
			default:
				break;
			}
			Util.sendMessage(sender, String.join("\n", ItemEdit.get().getConfig("itemeditimport.yml")
					.loadStringList("help", new ArrayList<>(), true)));
			return true;
		} else
			Util.sendMessage(sender, ItemEdit.get().getConfig("itemeditimport.yml")
					.loadString("lack-permission", "", true).replace("%permission%", "itemedit.itemeditimport"));
		return true;
	}

	private static ItemStack fromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack item = (ItemStack) dataInput.readObject();
			dataInput.close();
			return item;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

}
