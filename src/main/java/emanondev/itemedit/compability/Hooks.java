package emanondev.itemedit.compability;

import org.bukkit.Bukkit;

public class Hooks {
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

    public static boolean isMythicMobsEnabled() {
        return isEnabled("MythicMobs");
    }

    public static boolean isItemBridgeEnabled() {
        return isEnabled("ItemBridge");
    }
}
