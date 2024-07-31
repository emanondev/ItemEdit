package emanondev.itemedit;

import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.compability.Hooks;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Util {
    private static final int MAX_COMPLETES = 200;

    public static <T extends Enum<T>> @NotNull List<String> complete(String prefix, @NotNull Class<T> enumClass) {
        prefix = prefix.toUpperCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T el : enumClass.getEnumConstants())
            if (el.toString().startsWith(prefix)) {
                results.add(el.toString().toLowerCase(Locale.ENGLISH));
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }


    public static @NotNull <T extends Enum<T>> List<String> complete(String prefix, @NotNull Class<T> type,
                                                                     @NotNull Predicate<T> predicate) {
        prefix = prefix.toUpperCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T el : type.getEnumConstants())
            if (predicate.test(el) && el.toString().startsWith(prefix)) {
                results.add(el.toString().toLowerCase(Locale.ENGLISH));
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static @NotNull List<String> complete(String prefix, String... list) {
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static @NotNull List<String> complete(String prefix, Collection<String> list) {
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase(Locale.ENGLISH).startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static @NotNull List<String> completePlayers(String prefix) {
        ArrayList<String> names = new ArrayList<>();
        final String text = prefix.toLowerCase(Locale.ENGLISH);
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (p.getName().toLowerCase(Locale.ENGLISH).startsWith(text))
                names.add(p.getName());
        });
        return names;
    }

    public static @NotNull List<String> complete(String prefix, IAliasSet<?> aliases) {
        ArrayList<String> results = new ArrayList<>();
        prefix = prefix.toLowerCase(Locale.ENGLISH);
        int c = 0;
        for (String alias : aliases.getAliases()) {
            if (alias.startsWith(prefix)) {
                results.add(alias);
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        }
        return results;
    }

    public static void sendMessage(@NotNull CommandSender sender, String message) {
        if (message == null || message.isEmpty())
            return;
        sender.sendMessage(message);
    }

    public static void sendMessage(@NotNull CommandSender sender, BaseComponent... message) {
        if (sender instanceof Player)
            ((Player) sender).spigot().sendMessage(message);
        else
            sender.sendMessage(BaseComponent.toPlainText(message));
    }

    public static void logToFile(String message) {
        try {
            File dataFolder = ItemEdit.get().getDataFolder();
            if (!dataFolder.exists())
                dataFolder.mkdir();
            Date date = new Date();
            File saveTo = new File(ItemEdit.get().getDataFolder(),
                    "logs" + File.separatorChar
                            + new SimpleDateFormat(ItemEdit.get().getConfig().loadMessage("log.file-format", "yyyy.MM.dd", false)
                            , Locale.ENGLISH).format(date)
                            //+ DateFormatUtils.format(date,
                            //ItemEdit.get().getConfig().loadMessage("log.file-format", "yyyy.MM.dd", false))
                            + ".log");
            if (!saveTo.getParentFile().exists()) { // Create parent folders if they don't exist
                saveTo.getParentFile().mkdirs();
            }
            if (!saveTo.exists())
                saveTo.createNewFile();

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(new SimpleDateFormat(ItemEdit.get().getConfig().loadMessage("log.log-date-format", "[dd.MM.yyyy HH:mm:ss]", false)
                    , Locale.ENGLISH).format(date)
                    + message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasBannedWords(@NotNull Player user, String text) {
        if (user.hasPermission("itemedit.bypass.censure"))
            return false;
        String message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text.toLowerCase(Locale.ENGLISH)));
        for (String regex : ItemEdit.get().getConfig().getStringList("blocked.regex"))
            if (Pattern.compile(regex).matcher(message).find()) {
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.console", true))
                    sendMessage(Bukkit.getConsoleSender(), "user: §e" + user.getName() + "§r attempt to write '" + text
                            + "'§r (stripped by colors and lowcased) was blocked by regex: §e" + regex);
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.file", true))
                    logToFile("user: '" + user.getName() + "' attempt to write '" + text
                            + "' (stripped by colors and lowcased to '" + message + "') was blocked by regex: '" + regex
                            + "'");
                sendMessage(user,
                        ItemEdit.get().getLanguageConfig(user).loadMessage("blocked-by-censure", "", null, true));
                return true;
            }
        for (String bannedWord : ItemEdit.get().getConfig().getStringList("blocked.words"))
            if (message.contains(bannedWord.toLowerCase(Locale.ENGLISH))) {
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.console", true))
                    sendMessage(Bukkit.getConsoleSender(),
                            "user: §e" + user.getName() + "§r attempt to write '" + text
                                    + "'§r (stripped by colors and lowcased) was blocked by word: §e"
                                    + bannedWord.toLowerCase(Locale.ENGLISH));
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.file", true))
                    logToFile("user: '" + user.getName() + "' attempt to write '" + text
                            + "' (stripped by colors and lowcased to '" + message + "') was blocked by word: '"
                            + bannedWord.toLowerCase(Locale.ENGLISH) + "'");
                sendMessage(user,
                        ItemEdit.get().getLanguageConfig(user).loadMessage("blocked-by-censure", "", null, true));
                return true;
            }
        return false;

    }


    public static String formatText(CommandSender sender, String text, String basePermission) {
        if (Util.hasMiniMessageAPI() && sender.hasPermission(basePermission + ".minimessage")) {
            text = Hooks.getMiniMessageUtil().fromMiniToText(text);
        }
        text = ChatColor.translateAlternateColorCodes('&', text);
        if (basePermission != null) {
            for (ChatColor style : ChatColor.values())
                if (style.isFormat()) {
                    if (!sender.hasPermission(basePermission + ".format." + style.name().toLowerCase(Locale.ENGLISH)))
                        text = text.replaceAll(style.toString(), "");
                } else if (!sender.hasPermission(basePermission + ".color." + style.name().toLowerCase(Locale.ENGLISH)))
                    text = text.replaceAll(style.toString(), "");
            if (sender.hasPermission(basePermission + ".color.hexa")) {
                try {
                    int from = 0;
                    while (text.indexOf("&#", from) >= 0) {
                        from = text.indexOf("&#", from) + 1;
                        text = text.replace(text.substring(from - 1, from + 7),
                                net.md_5.bungee.api.ChatColor.of(text.substring(from, from + 7)).toString());
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return text;

    }

    public static boolean isAllowedRenameItem(CommandSender sender, Material type) {
        if (sender.hasPermission("itemedit.bypass.rename_type_restriction"))
            return true;

        List<String> values = ItemEdit.get().getConfig().getStringList("blocked.type-blocked-rename");
        if (values.isEmpty())
            return true;
        String id = type.name();
        for (String name : values)
            if (id.equalsIgnoreCase(name)) {
                sendMessage(sender,
                        ItemEdit.get().getLanguageConfig(sender).loadMessage("blocked-by-type-restriction", "", null, true));
                return false;
            }
        return true;
    }

    /**
     * for pre 1.13 compatibility
     *
     * @param color color
     * @return An ItemStack of selected Dye
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getDyeItemFromColor(DyeColor color) {
        try {
            return new ItemStack(Material.valueOf(color.name() + "_DYE"));
        } catch (Exception e) {
            return new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 0, getDataByColor(color));
        }
    }


    /**
     * for pre 1.13 compatibility
     *
     * @param color color
     * @return An ItemStack of selected Dyed wool
     */
    @SuppressWarnings("deprecation")
    public static ItemStack getWoolItemFromColor(DyeColor color) {
        try {
            return new ItemStack(Material.valueOf(color.name() + "_WOOL"));
        } catch (Exception e) {
            return new ItemStack(Material.valueOf("WOOL"), 1, (short) 0, getDataByColor(color));
        }
    }

    /**
     * for pre 1.13 compatibility
     *
     * @param color color
     * @return An ItemStack of selected Dyed wool
     */
    public static Material getBannerItemFromColor(DyeColor color) {
        try {
            return Material.valueOf(color.name() + "_BANNER");
        } catch (Exception e) {
            return Material.valueOf("BANNER");
        }
    }

    @SuppressWarnings("deprecation")
    public static DyeColor getColorFromBanner(ItemStack banner) {
        try {
            String name = banner.getType().name();
            return DyeColor.valueOf(name.substring(0, name.length() - 7));
        } catch (Exception e) {
            return getColorByData((byte) banner.getDurability());
        }
    }

    public static DyeColor getColorByData(byte color) {
        switch (color) { //Silver
            case 0:
                return DyeColor.BLACK;
            case 4:
                return DyeColor.BLUE;
            case 3:
                return DyeColor.BROWN;
            case 6:
                return DyeColor.CYAN;
            case 8:
                return DyeColor.GRAY;
            case 2:
                return DyeColor.GREEN;
            case 12:
                return DyeColor.LIGHT_BLUE;
            case 10:
                return DyeColor.LIME;
            case 13:
                return DyeColor.MAGENTA;
            case 14:
                return DyeColor.ORANGE;
            case 9:
                return DyeColor.PINK;
            case 5:
                return DyeColor.PURPLE;
            case 1:
                return DyeColor.RED;
            case 7:
                return DyeColor.LIGHT_GRAY;
            case 15:
                return DyeColor.WHITE;
            case 11:
                return DyeColor.YELLOW;
            default:
                throw new IllegalStateException();
        }
    }

    public static Byte getDataByColor(DyeColor color) {
        switch (color.name()) { //Silver
            case "BLACK":
                return 0;
            case "BLUE":
                return 4;
            case "BROWN":
                return 3;
            case "CYAN":
                return 6;
            case "GRAY":
                return 8;
            case "GREEN":
                return 2;
            case "LIGHT_BLUE":
                return 12;
            case "LIME":
                return 10;
            case "MAGENTA":
                return 13;
            case "ORANGE":
                return 14;
            case "PINK":
                return 9;
            case "PURPLE":
                return 5;
            case "RED":
                return 1;
            case "SILVER":
            case "LIGHT_GRAY":
                return 7;
            case "WHITE":
                return 15;
            case "YELLOW":
                return 11;
            default:
                throw new IllegalStateException();
        }
    }

    public static boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    public static final int GAME_MAIN_VERSION = Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[0]);
    public static final int GAME_VERSION = Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    public static final int GAME_SUB_VERSION = Bukkit.getBukkitVersion().split("-")[0].split("\\.").length < 3 ? 0 : Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[2]);

    /**
     * Inclusive
     * isVersionUpTo(1,9) on 1.9.0 is true
     */
    public static boolean isVersionUpTo(int mainVersion, int version) {
        return isVersionUpTo(mainVersion, version, 99);
    }

    /**
     * Inclusive
     * isVersionUpTo(1,9,4) on 1.9.4 is true
     */
    public static boolean isVersionUpTo(int mainVersion, int version, int subVersion) {
        if (GAME_MAIN_VERSION > mainVersion)
            return false;
        if (GAME_MAIN_VERSION < mainVersion)
            return true;
        if (GAME_VERSION > version)
            return false;
        if (GAME_VERSION < version)
            return true;
        return GAME_SUB_VERSION <= subVersion;
    }

    /**
     * Inclusive
     * isVersionAfter(1,9) on 1.9.0 is true
     */
    public static boolean isVersionAfter(int mainVersion, int version) {
        return isVersionAfter(mainVersion, version, 0);
    }

    /**
     * Inclusive
     * isVersionAfter(1,9,4) on 1.9.4 is true
     */
    public static boolean isVersionAfter(int mainVersion, int version, int subVersion) {
        if (GAME_MAIN_VERSION < mainVersion)
            return false;
        if (GAME_MAIN_VERSION > mainVersion)
            return true;
        if (GAME_VERSION < version)
            return false;
        if (GAME_VERSION > version)
            return true;
        return GAME_SUB_VERSION >= subVersion;
    }

    /**
     * Inclusive
     */
    public static boolean isVersionInRange(int mainVersionMin, int versionMin,
                                           int mainVersionMax, int versionMax) {
        return isVersionInRange(mainVersionMin, versionMin, 0,
                mainVersionMax, versionMax, 99);
    }

    /**
     * Inclusive
     */
    public static boolean isVersionInRange(int mainVersionMin, int versionMin, int subVersionMin,
                                           int mainVersionMax, int versionMax, int subVersionMax) {
        return isVersionAfter(mainVersionMin, versionMin, subVersionMin)
                && isVersionUpTo(mainVersionMax, versionMax, subVersionMax);
    }

    public static boolean isAllowedChangeLore(CommandSender sender, Material type) {
        if (sender.hasPermission("itemedit.bypass.lore_type_restriction"))
            return true;

        List<String> values = ItemEdit.get().getConfig().getStringList("blocked.type-blocked-lore");
        if (values == null || values.isEmpty())
            return true;
        String id = type.name();
        for (String name : values)
            if (id.equalsIgnoreCase(name)) {
                sendMessage(sender,
                        ItemEdit.get().getLanguageConfig(sender).loadMessage("blocked-by-type-restriction-lore", "", null, true));
                return false;
            }
        return true;
    }


    private static final boolean hasPaper = initPaper();
    private static final boolean hasFolia = initFolia();

    private static boolean initPaper() {
        try {
            Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData");
            return true;
        } catch (NoClassDefFoundError | ClassNotFoundException ex) {
            return false;
        }
    }
    public static boolean hasPaperAPI() {
        return hasPaper;
    }

    private static final boolean hasPurpur = initPurpur();

    private static boolean initPurpur() {
        try {
            Class.forName("org.purpurmc.purpur.event.PlayerAFKEvent");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean initFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasPurpurAPI() {
        return hasPurpur;
    }
    public static boolean hasFoliaAPI() {
        return hasFolia;
    }

    public static boolean hasMiniMessageAPI() {
        return Hooks.hasMiniMessage();
    }
}
