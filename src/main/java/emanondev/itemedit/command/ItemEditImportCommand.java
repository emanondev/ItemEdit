package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ItemEditImportCommand implements TabExecutor {

    private final ItemEdit plugin;
    private final String permission;

    public ItemEditImportCommand() {
        this.plugin = ItemEdit.get();
        this.permission = "itemedit.itemeditimport";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 1)
            return Util.complete(args[0], Collections.singletonList("itemeditor"));
        return Collections.emptyList();
    }


    public void sendPermissionLackMessage(@NotNull String permission, CommandSender sender) {
        Util.sendMessage(sender, plugin.getLanguageConfig(sender).loadMessage("lack-permission", "&cYou lack of permission %permission%",
                sender instanceof Player ? (Player) sender : null, true
                , "%permission%",
                permission));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sendPermissionLackMessage(permission, sender);
            return true;
        }


        if (args.length == 0) {
            Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                    "itemeditimport.help", new ArrayList<>())));
            return true;
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "itemeditor": {
                File[] files = new File("plugins" + File.separator + "ItemEditor" + File.separator + "items").listFiles();
                if (files != null
                        && files.length != 0) {
                    List<String> importedIds = new ArrayList<>();
                    int max = files.length;
                    for (File file : files) {
                        String name = file.getName().replace(".yml", "");
                        try {
                            ItemEdit.get().getServerStorage().validateID(name);
                        } catch (Exception e) {
                            Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                                    "itemeditimport.itemeditor.invalid-id", new ArrayList<>(), null, true, "%id%", name)));
                            continue;
                        }
                        if (ItemEdit.get().getServerStorage().getItem(name) != null) {
                            Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                                    "itemeditimport.itemeditor.already-used-id", new ArrayList<>(), null, true,
                                    "%id%", name)));
                            continue;
                        }

                        try {
                            ItemStack item = fromBase64(YamlConfiguration.loadConfiguration(file).getString("Item"));
                            ItemEdit.get().getServerStorage().setItem(name, item);
                            importedIds.add(name);
                        } catch (Exception e) {
                            Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                                    "itemeditimport.itemeditor.unable-to-get-item", new ArrayList<>(), null, true,
                                    "%id%", name)));
                            e.printStackTrace();
                            continue;
                        }

                    }
                    if (importedIds.isEmpty()) {
                        Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                                "itemeditimport.itemeditor.import-unsuccess", new ArrayList<>(), null, true,
                                "%ids%", String.join(", ", importedIds),
                                "%max%", String.valueOf(max), "%done%", String.valueOf(importedIds.size()))));
                    } else {
                        Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                                "itemeditimport.itemeditor.import-success", new ArrayList<>(), null, true,
                                "%ids%", String.join(", ", importedIds)
                                , "%max%", String.valueOf(max),
                                "%done%", String.valueOf(importedIds.size()))));
                    }

                } else {
                    Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage(
                            "itemeditimport.itemeditor.import-empty", new ArrayList<>())));
                }
                return true;
            }
            default:
                break;
        }
        Util.sendMessage(sender, String.join("\n", plugin.getLanguageConfig(sender).loadMultiMessage("itemeditimport.help",
                new ArrayList<>())));
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
