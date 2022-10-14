package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.compability.Hooks;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
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
            Util.sendMessage(sender, new ComponentBuilder(
                    ChatColor.BLUE + "Server: " + ChatColor.AQUA + Bukkit.getVersion() + "\n" +
                            ChatColor.BLUE + "ItemEdit: " + ChatColor.AQUA + plugin.getDescription().getVersion() + "\n" +
                            ChatColor.BLUE + "ItemTag: " + ChatColor.AQUA + (Hooks.isEnabled("ItemTag") ?
                            Bukkit.getPluginManager().getPlugin("ItemTag").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "PAPI: " + ChatColor.AQUA + (Hooks.isPAPIEnabled() ?
                            Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "Vault: " + ChatColor.AQUA + (Hooks.isVault() ?
                            Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "MythicMobs: " + ChatColor.AQUA + (Hooks.isMythicMobsEnabled() ?
                            Bukkit.getPluginManager().getPlugin("MythicMobs").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "ShopGuiPlus: " + ChatColor.AQUA + (Hooks.isShopGuiPlusEnabled() ?
                            Bukkit.getPluginManager().getPlugin("ShopGuiPlus").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "Vanish: " + ChatColor.AQUA + (Hooks.isVanishEnabled() ?
                            (Bukkit.getPluginManager().getPlugin("SuperVanish") == null ?
                                    Bukkit.getPluginManager().getPlugin("PremiumVanish") :
                                    Bukkit.getPluginManager().getPlugin("SuperVanish")).getDescription().getFullName() : "Nope")
            ).create());
        } else
            sendPermissionLackMessage(permission, sender);
        return true;
    }

    public void register() {
        try {
            plugin.registerCommand(plugin.getName().toLowerCase(Locale.ENGLISH) + "info", this, null);
        } catch (Exception e) {
            plugin.log("Unable to register command " + ChatColor.YELLOW + plugin.getName().toLowerCase(Locale.ENGLISH) + "info");
            e.printStackTrace();
        }
    }
}
