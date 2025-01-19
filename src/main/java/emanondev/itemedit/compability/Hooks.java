package emanondev.itemedit.compability;

import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Hooks {
    private static final MiniMessageUtil miniMessage = initMiniMessage();

    public static boolean isVault() {
        return isEnabled("Vault");
    }

    public static boolean isVanishEnabled() {
        return isEnabled("SuperVanish") || isEnabled("PremiumVanish");
    }

    public static boolean isPAPIEnabled() {
        return isEnabled("PlaceholderAPI");
    }

    public static boolean isNBTAPIEnabled() {
        return isEnabled("NBTAPI");
    }

    public static boolean isShopGuiPlusEnabled() {
        return isEnabled("ShopGuiPlus");
    }

    public static boolean isEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public static Plugin getPlugin(String pluginName) {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }

    public static String getPluginVersion(String pluginName) {
        return getPluginVersion(pluginName, null);
    }

    public static String getPluginVersion(String pluginName, String ifMissing) {
        Plugin plugin = getPlugin(pluginName);
        if (plugin == null)
            return ifMissing;
        return plugin.getDescription().getVersion();
    }

    public static boolean isMythicMobsEnabled() {
        return isEnabled("MythicMobs");
    }

    public static boolean isItemBridgeEnabled() {
        return isEnabled("ItemBridge");
    }

    public static boolean isDungeonMMOEnabled() {
        return isEnabled("DungeonMMO");
    }

    private static MiniMessageUtil initMiniMessage() {
        try {
            if (VersionUtils.hasPaperAPI() && VersionUtils.isVersionAfter(1, 16, 5)) {
                MiniMessagePaper inst = MiniMessagePaper.getInstance();
                if (!inst.fromMiniToText("<red>this is a test</red>").equals("<red>this is a test</red>"))
                    return inst;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static boolean hasMiniMessage() {
        return miniMessage != null;
    }

    public static MiniMessageUtil getMiniMessageUtil() {
        return miniMessage;
    }
}
