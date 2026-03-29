package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Utility command, prints information about the plugin and supported plugins
 */
public class ItemEditInfoCommand implements TabExecutor {

    private static final ItemEdit plugin = ItemEdit.get();
    private final String permission;

    public ItemEditInfoCommand() {
        this.permission = plugin.getName().toLowerCase(Locale.ENGLISH) + "." + plugin.getName().toLowerCase(Locale.ENGLISH) + "info";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    public void sendPermissionLackMessage(@NotNull String permission, CommandSender sender) {
        plugin.getTranslator().send(sender, "lack-permission",
                "%permission%", permission);
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission(permission)) {
            sendPermissionLackMessage(permission, sender);
            return true;
        }
        StringBuilder copyText = new StringBuilder();
        if (sender instanceof Player)
            copyText.append("<blue>Server: <aqua>").append(Bukkit.getVersion()).append("\n")
                    .append("<blue>View: <aqua>").append(VersionUtils.getVersion()).append("\n")
                    .append("<blue>Java: <aqua>").append(System.getProperty("java.version")).append("\n")
                    .append("<blue>ItemEdit: <aqua>").append(plugin.getDescription().getVersion())
                    .append("<blue> Storage: <aqua>").append(plugin.getStorageType().name()).append("\n")
                    .append("<blue>ItemTag: <aqua>").append(Hooks.getPluginVersion("ItemTag", "No")).append("\n")
                    .append("<blue>PAPI: <aqua>").append(Hooks.getPluginVersion("PlaceholderAPI", "No")).append("\n")
                    .append("<blue>NBTAPI: <aqua>").append(Hooks.getPluginVersion("NBTAPI", "No")).append("\n")
                    .append("<blue>Vault: <aqua>").append(Hooks.getPluginVersion("Vault", "No")).append("\n")
                    .append("<blue>MythicMobs: <aqua>").append(Hooks.getPluginVersion("MythicMobs", "No")).append("\n")
                    .append("<blue>ShopGuiPlus: <aqua>").append(Hooks.getPluginVersion("ShopGuiPlus", "No")).append("\n")
                    .append("<blue>Vanish: <aqua>").append(Hooks.getPluginVersion("PremiumVanish",
                            Hooks.getPluginVersion("SuperVanish", "No")));
        StringBuilder msg = new StringBuilder(copyText.toString());
        if (sender instanceof Player) {
            msg.append("\n").append(Util.asCopyToClipboard("<gold><u>Click To Copy</u></gold>",
                    copyText.toString().replace("<aqua>", "").replace("<blue>", "")));
            copyText.append("\nPlugins: ");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                copyText.append(plugin.getName())
                        .append(" (").append(plugin.getDescription().getVersion())
                        .append(plugin.isEnabled() ? "), " : " loaded only), ");
            }
            msg.append("\n").append(Util.asCopyToClipboard("<gold><u>Click To Copy with Plugin list</u></gold>",
                    copyText.substring(0, copyText.length() - 2)));
            Util.sendMessage(sender, msg.substring(0, msg.length() - 2));
        } else {
            msg.append("\n<blue>Plugins: <aqua>");
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                msg.append(plugin.getName()).append(" (")
                        .append(plugin.getDescription().getVersion())
                        .append(plugin.isEnabled() ? "), " : " loaded only), ");
            }
            Util.sendMessage(sender, msg.substring(0, msg.length() - 2));
        }
        return true;
    }
}
