package emanondev.itemedit.command;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.compability.Hooks;
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
                        .append("View: ").append(Util.hasFoliaAPI() ? "Folia" : (Util.hasPurpurAPI() ? "Purpur" : (Util.hasPaperAPI() ? "Paper" : "Spigot")))
                        .append(" ").append(Util.GAME_MAIN_VERSION).append(".").append(Util.GAME_VERSION).append(".").append(Util.GAME_SUB_VERSION).append("\n")
                        .append("Java: ").append(System.getProperty("java.version")).append("\n")
                        .append("ItemEdit: ").append(plugin.getDescription().getVersion())
                        .append(" Storage: ").append(plugin.getStorageType().name()).append("\n")
                        .append("ItemTag: ").append(Hooks.isEnabled("ItemTag") ? Bukkit.getPluginManager().getPlugin("ItemTag").getDescription().getVersion() : "Nope").append("\n")
                        .append("PAPI: ").append(Hooks.isPAPIEnabled() ? Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion() : "Nope").append("\n")
                        .append("NBTAPI: ").append(Hooks.isNBTAPIEnabled() ? Bukkit.getPluginManager().getPlugin("NBTAPI").getDescription().getVersion() : "Nope").append("\n")
                        .append("Vault: ").append(Hooks.isVault() ? Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion() : "Nope").append("\n")
                        .append("MythicMobs: ").append(Hooks.isMythicMobsEnabled() ? Bukkit.getPluginManager().getPlugin("MythicMobs").getDescription().getVersion() : "Nope").append("\n")
                        .append("ShopGuiPlus: ").append(Hooks.isShopGuiPlusEnabled() ? Bukkit.getPluginManager().getPlugin("ShopGuiPlus").getDescription().getVersion() : "Nope").append("\n")
                        .append("Vanish: ").append(Hooks.isVanishEnabled() ? (Bukkit.getPluginManager().getPlugin("SuperVanish") == null ?
                                Bukkit.getPluginManager().getPlugin("PremiumVanish") : Bukkit.getPluginManager().getPlugin("SuperVanish")).getDescription().getFullName() : "Nope");
            ComponentBuilder msg = new ComponentBuilder(
                    ChatColor.BLUE + "Server: " + ChatColor.AQUA + Bukkit.getVersion() + "\n" +
                            ChatColor.BLUE + "Java: " + ChatColor.AQUA + System.getProperty("java.version") + "\n" +
                            ChatColor.BLUE + "View: " + ChatColor.AQUA + (Util.hasFoliaAPI() ? "Folia" :
                            (Util.hasPurpurAPI() ? "Purpur" : (Util.hasPaperAPI() ? "Paper" : "Spigot")))
                            + " " + Util.GAME_MAIN_VERSION + "." + Util.GAME_VERSION + "." + Util.GAME_SUB_VERSION + "\n" +
                            ChatColor.BLUE + "ItemEdit: " + ChatColor.AQUA + plugin.getDescription().getVersion() + " Storage: " + plugin.getStorageType().name() + "\n" +
                            ChatColor.BLUE + "ItemTag: " + ChatColor.AQUA + (Hooks.isEnabled("ItemTag") ?
                            Bukkit.getPluginManager().getPlugin("ItemTag").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "PAPI: " + ChatColor.AQUA + (Hooks.isPAPIEnabled() ?
                            Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "NBTAPI: " + ChatColor.AQUA + (Hooks.isNBTAPIEnabled() ?
                            Bukkit.getPluginManager().getPlugin("NBTAPI").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "Vault: " + ChatColor.AQUA + (Hooks.isVault() ?
                            Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "MythicMobs: " + ChatColor.AQUA + (Hooks.isMythicMobsEnabled() ?
                            Bukkit.getPluginManager().getPlugin("MythicMobs").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "ShopGuiPlus: " + ChatColor.AQUA + (Hooks.isShopGuiPlusEnabled() ?
                            Bukkit.getPluginManager().getPlugin("ShopGuiPlus").getDescription().getVersion() : "Nope") + "\n" +
                            ChatColor.BLUE + "Vanish: " + ChatColor.AQUA + (Hooks.isVanishEnabled() ?
                            (Bukkit.getPluginManager().getPlugin("SuperVanish") == null ?
                                    Bukkit.getPluginManager().getPlugin("PremiumVanish") :
                                    Bukkit.getPluginManager().getPlugin("SuperVanish")).getDescription().getFullName() : "Nope"));
            if (sender instanceof Player) {
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

    public void register() {
        try {
            plugin.registerCommand(plugin.getName().toLowerCase(Locale.ENGLISH) + "info", this, null);
        } catch (Exception e) {
            plugin.log("Unable to register command " + ChatColor.YELLOW + plugin.getName().toLowerCase(Locale.ENGLISH) + "info");
            e.printStackTrace();
        }
    }
}
