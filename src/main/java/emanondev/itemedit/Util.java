package emanondev.itemedit;

import emanondev.itemedit.aliases.AliasSet;
import net.md_5.bungee.api.chat.BaseComponent;
import org.apache.commons.lang.time.DateFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Util {
    private static final int MAX_COMPLETES = 100;

    public static <T extends Enum<T>> @NotNull List<String> complete(String prefix, @NotNull Class<T> enumClass) {
        prefix = prefix.toUpperCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (T el : enumClass.getEnumConstants())
            if (el.toString().startsWith(prefix)) {
                results.add(el.toString().toLowerCase());
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
                results.add(el.toString().toLowerCase());
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static List<String> complete(String prefix, String... list) {
        prefix = prefix.toLowerCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase().startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static List<String> complete(String prefix, Collection<String> list) {
        prefix = prefix.toLowerCase();
        ArrayList<String> results = new ArrayList<>();
        int c = 0;
        for (String value : list)
            if (value.toLowerCase().startsWith(prefix)) {
                results.add(value);
                c++;
                if (c > MAX_COMPLETES)
                    return results;
            }
        return results;
    }

    public static List<String> completePlayers(String prefix) {
        ArrayList<String> names = new ArrayList<>();
        final String text = prefix.toLowerCase();
        Bukkit.getOnlinePlayers().forEach((p) -> {
            if (p.getName().toLowerCase().startsWith(text))
                names.add(p.getName());
        });
        return names;
    }

    public static List<String> complete(String prefix, AliasSet<?> aliases) {
        ArrayList<String> results = new ArrayList<>();
        prefix = prefix.toLowerCase();
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

    public static void sendMessage(CommandSender sender, String message) {
        if (message == null || message.isEmpty())
            return;
        sender.sendMessage(message);
    }

    public static void sendMessage(CommandSender sender, BaseComponent... message) {
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
                            + DateFormatUtils.format(date,
                            ItemEdit.get().getConfig().loadMessage("log.file-format", "yyyy.MM.dd", false))
                            + ".log");
            if (!saveTo.getParentFile().exists()) { // Create parent folders if they don't exist
                saveTo.getParentFile().mkdirs();
            }
            if (!saveTo.exists())
                saveTo.createNewFile();

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(DateFormatUtils.format(date,
                    ItemEdit.get().getConfig().loadMessage("log.log-date-format", "[dd.MM.yyyy HH:mm:ss]", false))
                    + message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasBannedWords(Player user, String text) {
        if (user.hasPermission("itemedit.bypass.censure"))
            return false;
        String message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', text.toLowerCase()));
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
            if (message.contains(bannedWord.toLowerCase())) {
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.console", true))
                    sendMessage(Bukkit.getConsoleSender(),
                            "user: §e" + user.getName() + "§r attempt to write '" + text
                                    + "'§r (stripped by colors and lowcased) was blocked by word: §e"
                                    + bannedWord.toLowerCase());
                if (ItemEdit.get().getConfig().getBoolean("blocked.log.file", true))
                    logToFile("user: '" + user.getName() + "' attempt to write '" + text
                            + "' (stripped by colors and lowcased to '" + message + "') was blocked by word: '"
                            + bannedWord.toLowerCase() + "'");
                sendMessage(user,
                        ItemEdit.get().getLanguageConfig(user).loadMessage("blocked-by-censure", "", null, true));
                return true;
            }
        return false;

    }

    public static String formatText(CommandSender sender, String text, String basePermission) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        if (basePermission != null) {
            for (ChatColor style : ChatColor.values())
                if (style.isFormat()) {
                    if (!sender.hasPermission(basePermission + ".format." + style.name().toLowerCase()))
                        text = text.replaceAll(style.toString(), "");
                } else if (!sender.hasPermission(basePermission + ".color." + style.name().toLowerCase()))
                    text = text.replaceAll(style.toString(), "");
            if (sender.hasPermission(basePermission + ".color.hexa")) {
                try {
                    int from = 0;
                    while (text.indexOf("&#", from) >= 0) {
                        from = text.indexOf("&#", from) + 1;
                        text = text.replace(text.substring(from - 1, from + 7),
                                net.md_5.bungee.api.ChatColor.of(text.substring(from, from + 7)).toString());
                    }
                } catch (Throwable t) {
                }
            }
        }
        return text;

    }

    public static boolean isAllowedRenameItem(CommandSender sender, Material type) {
        if (sender.hasPermission("itemedit.bypass.rename_type_restriction"))
            return true;

        List<String> values = ItemEdit.get().getConfig().getStringList("blocked.type-blocked-rename");
        if (values == null || values.isEmpty())
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
            return new ItemStack(Material.valueOf("INK_SACK"), 1, (short) 0, getData(color));
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
            return new ItemStack(Material.valueOf("WOOL"), 1, (short) 0, getData(color));
        }
    }

    private static Byte getData(DyeColor color) {
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
}
