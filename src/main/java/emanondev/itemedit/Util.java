package emanondev.itemedit;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.utility.InventoryUtils;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Util {

    private Util() {
        throw new UnsupportedOperationException();
    }

    /**
     * takes an already formatted message
     *
     * @param sender
     * @param message
     */
    public static void sendMessage(@NotNull CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sendMessage(sender, MiniMessage.miniMessage().deserialize(message));
    }

    public static void sendMessage(@NotNull CommandSender sender, List<String> message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sendMessage(sender, String.join("\n", message));
    }

    /**
     * takes an already formatted message
     *
     * @param sender
     * @param message
     */
    public static void sendMessage(@NotNull CommandSender sender, Component message) {
        if (!hasRenderableContent(message)) {
            return;
        }
        sender.sendMessage(message);
    }

    public static boolean hasRenderableContent(Component component) {
        if (component == null) return false;

        if (component instanceof TextComponent text) {
            if (!text.content().isBlank()) {
                return true;
            }
        }

        if (component instanceof TranslatableComponent) return true;
        if (component instanceof KeybindComponent) return true;
        if (component instanceof ScoreComponent) return true;
        if (component instanceof SelectorComponent) return true;

        for (Component child : component.children()) {
            if (hasRenderableContent(child)) {
                return true;
            }
        }
        return false;
    }

    public static void logCommandError(AbstractCommand command, String[] args, CommandSender sender) {
        ItemStack item = sender instanceof Player p ? InventoryUtils.getItem(p, EquipmentSlot.HAND) : null;
        sendMessage(sender, "<red>ERROR when executing /" + command.getName()
                + " " + String.join(" ", args) + " by " + sender.getName()
                + " (with " + (item == null ? "nothing" : item) + " in hand)");
    }

    public static void logToFile(String message) {
        try {
            File dataFolder = ItemEdit.get().getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            Date date = new Date();
            File saveTo = new File(ItemEdit.get().getDataFolder(),
                    "logs" + File.separatorChar
                            + new SimpleDateFormat(ItemEdit.get().getConfig().loadMessage("log.file-format", "yyyy.MM.dd", false)
                            , Locale.ENGLISH).format(date)
                            + ".log");
            if (!saveTo.getParentFile().exists()) { // Create parent folders if they don't exist
                saveTo.getParentFile().mkdirs();
            }
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

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

    public static boolean checkBannedWords(@NotNull Player user, String text) {
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
                ItemEdit.get().getTranslator().send(user, "blocked-by-censure");
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
                ItemEdit.get().getTranslator().send(user, "blocked-by-censure");
                return true;
            }
        return false;

    }

    public static String formatText(CommandSender sender, String text, String basePermission) {
        if (sender.hasPermission(basePermission + ".minimessage")) {
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
        if (sender.hasPermission("itemedit.bypass.rename_type_restriction")) {
            return true;
        }

        List<String> values = ItemEdit.get().getConfig().getStringList("blocked.type-blocked-rename");
        if (values.isEmpty()) {
            return true;
        }
        String id = type.name();
        for (String name : values)
            if (id.equalsIgnoreCase(name)) {
                ItemEdit.get().getTranslator().send(sender, "blocked-by-type-restriction");
                return false;
            }
        return true;
    }

    /**
     * @param color color
     * @return An ItemStack of selected Dye
     */
    public static ItemStack getDyeItemFromColor(DyeColor color) {
        return new ItemStack(Material.valueOf(color.name() + "_DYE"));
    }

    /**
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

    public static DyeColor getColorFromBanner(ItemStack banner) {
        String name = banner.getType().name();
        return DyeColor.valueOf(name.substring(0, name.length() - 7));
    }

    public static boolean isAllowedChangeLore(CommandSender sender, Material type) {
        if (sender.hasPermission("itemedit.bypass.lore_type_restriction")) {
            return true;
        }

        List<String> values = ItemEdit.get().getConfig().getStringList("blocked.type-blocked-lore");
        if (values.isEmpty())
            return true;
        String id = type.name();
        for (String name : values)
            if (id.equalsIgnoreCase(name)) {
                ItemEdit.get().getTranslator().send(sender, "blocked-by-type-restriction-lore");
                return false;
            }
        return true;
    }

    public static String asSuggestCommand(String text, @Nullable String command) {
        if (command == null || command.isEmpty()) {
            return text;
        }
        return "<click:suggest_command:'" + command.replace("'", "''") + "'>" + text + "</click>";
    }

    public static String asExecuteCommand(String text, @Nullable String command) {
        if (command == null || command.isEmpty()) {
            return text;
        }
        return "<click:run_command:'" + command.replace("'", "''") + "'>" + text + "</click>";
    }

    public static String asCopyToClipboard(String text, @Nullable String copied) {
        if (copied == null || copied.isEmpty()) {
            return text;
        }
        return "<click:copy_to_clipboard:'" + copied.replace("'", "''") + "'>" + text + "</click>";
    }

    public static String asOpenUrl(String text, @Nullable String url) {
        if (url == null || url.isEmpty()) {
            return text;
        }
        return "<click:open_url:'" + url.replace("'", "''") + "'>" + text + "</click>";
    }

    public static String asHover(String text, @Nullable String hover) {
        if (hover == null || hover.isEmpty()) {
            return text;
        }
        return "<hover:show_text:'" + hover.replace("'", "''") + "'>" + text + "</hover>";
    }

    public static String asHover(String text, @Nullable List<String> hover) {
        if (hover == null || hover.isEmpty()) {
            return text;
        }
        return asHover(text, String.join("\n", hover));
    }
}
