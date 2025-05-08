package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.utility.VersionUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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
        Util.sendMessage(sender, plugin.getLanguageConfig(sender).loadMessage("lack-permission", "&cYou lack of permission %permission%",
                sender instanceof Player ? (Player) sender : null, true
                , "%permission%", permission));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission(permission)) {
            StringBuilder copyText = new StringBuilder();
            if (sender instanceof Player)
                copyText.append("Server: ").append(Bukkit.getVersion()).append("\n")
                        .append("View: ").append(VersionUtils.getVersion()).append("\n")
                        .append("Java: ").append(System.getProperty("java.version")).append("\n")
                        .append("ItemEdit: ").append(plugin.getDescription().getVersion())
                        .append(" Storage: ").append(plugin.getStorageType().name()).append("\n")
                        .append("ItemTag: ").append(Hooks.getPluginVersion("ItemTag", "Nope")).append("\n")
                        .append("PAPI: ").append(Hooks.getPluginVersion("PlaceholderAPI", "Nope")).append("\n")
                        .append("NBTAPI: ").append(Hooks.getPluginVersion("NBTAPI", "Nope")).append("\n")
                        .append("Vault: ").append(Hooks.getPluginVersion("Vault", "Nope")).append("\n")
                        .append("MythicMobs: ").append(Hooks.getPluginVersion("MythicMobs", "Nope")).append("\n")
                        .append("ShopGuiPlus: ").append(Hooks.getPluginVersion("ShopGuiPlus", "Nope")).append("\n")
                        .append("Vanish: ").append(Hooks.getPluginVersion("PremiumVanish", Hooks.getPluginVersion("SuperVanish", "Nope")));
            ComponentBuilder msg = new ComponentBuilder(
                    ChatColor.BLUE + "Server: " + ChatColor.AQUA + Bukkit.getVersion() + "\n" +
                            ChatColor.BLUE + "Java: " + ChatColor.AQUA + System.getProperty("java.version") + "\n" +
                            ChatColor.BLUE + "View: " + ChatColor.AQUA + VersionUtils.getVersion() + "\n" +
                            ChatColor.BLUE + "ItemEdit: " + ChatColor.AQUA + plugin.getDescription().getVersion() + " Storage: " + plugin.getStorageType().name() + "\n" +
                            ChatColor.BLUE + "ItemTag: " + ChatColor.AQUA + Hooks.getPluginVersion("ItemTag", "Nope") + "\n" +
                            ChatColor.BLUE + "PAPI: " + ChatColor.AQUA + Hooks.getPluginVersion("PlaceholderAPI", "Nope") + "\n" +
                            ChatColor.BLUE + "NBTAPI: " + ChatColor.AQUA + Hooks.getPluginVersion("NBTAPI", "Nope") + "\n" +
                            ChatColor.BLUE + "Vault: " + ChatColor.AQUA + Hooks.getPluginVersion("Vault", "Nope") + "\n" +
                            ChatColor.BLUE + "MythicMobs: " + ChatColor.AQUA + Hooks.getPluginVersion("MythicMobs", "Nope") + "\n" +
                            ChatColor.BLUE + "ShopGuiPlus: " + ChatColor.AQUA + Hooks.getPluginVersion("ShopGuiPlus", "Nope") + "\n" +
                            ChatColor.BLUE + "Vanish: " + ChatColor.AQUA + Hooks.getPluginVersion("PremiumVanish", Hooks.getPluginVersion("SuperVanish", "Nope")));
            if (sender instanceof Player && VersionUtils.isVersionAfter(1,9)) {
                msg.append("\n").append(new ComponentBuilder(ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Click To Copy\n")
                        .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                copyText.toString())).create());
                copyText.append("\nPlugins: ");
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    copyText.append(plugin.getName()).append(" (").append(plugin.getDescription().getVersion()).append(plugin.isEnabled() ? "), " : " loaded only), ");
                }
                msg.append(new ComponentBuilder(ChatColor.GOLD + ChatColor.UNDERLINE.toString() + "Click To Copy with Plugin list\n")
                        .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                copyText.substring(0, copyText.length() - 2))).create());
            } else {
                msg.append("\n" + ChatColor.BLUE + "Plugins: " + ChatColor.AQUA);
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    copyText.append(plugin.getName()).append(" (").append(plugin.getDescription().getVersion()).append(plugin.isEnabled() ? "), " : " loaded only), ");
                }
                msg.append(copyText.substring(0, copyText.length() - 2));
            }
            Util.sendMessage(sender, msg.create());
        } else
            sendPermissionLackMessage(permission, sender);
        return true;
    }
}
