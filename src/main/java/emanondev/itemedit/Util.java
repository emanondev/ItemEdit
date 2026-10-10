package emanondev.itemedit;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.utility.InventoryUtils;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class Util {

    private static final Pattern LEGACY_HEX_COLOR = Pattern.compile("(?i)&#([0-9a-f]{6})");
    private static final Pattern LEGACY_REPEATED_HEX_COLOR = Pattern.compile("(?i)[&§]x(?:[&§][0-9a-f]){6}");

    private Util() {
        throw new UnsupportedOperationException();
    }

    /**
     * takes an already formatted message
     *
     * @param sender
     * @param message
     */
    @Deprecated(forRemoval = true)
    public static void sendMessage(@NotNull CommandSender sender, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sendMessage2(sender, MiniMessage.miniMessage().deserialize(message));
    }

    @Deprecated(forRemoval = true)
    public static void sendMessage(@NotNull CommandSender sender, List<String> message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        sendMessage(sender, String.join("\n", message));
    }

    public static void sendMessage2(@NotNull CommandSender sender, List<Component> message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        List<Component> lines = message.stream()
                .filter(Objects::nonNull)
                .toList();
        if (lines.isEmpty()) {
            return;
        }

        Component combined = Component.join(JoinConfiguration.separator(Component.newline()), lines);
        sendMessage2(sender, combined);
    }

    /**
     * takes an already formatted message
     *
     * @param sender
     * @param message
     */
    public static void sendMessage2(@NotNull CommandSender sender, Component message) {
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
        sendMessage(Bukkit.getConsoleSender(), "<red>ERROR when executing /" + command.getName()
                + " " + String.join(" ", args) + " by " + sender.getName()
                + " (with " + (item == null ? "nothing" : item) + " in hand)");
    }

    public static void logToFile(Component msg) {
        logToFile(PlainTextComponentSerializer.plainText().serialize(msg));
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
                    sendMessage(Bukkit.getConsoleSender(), "user: <yellow>" + user.getName() + "<white> attempt to write '" + text
                            + "'<reset> (stripped by colors and lowcased) was blocked by regex: <yellow>" + regex);
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
                            "user: <yellow>" + user.getName() + "<reset> attempt to write '" + text
                                    + "'<reset> (stripped by colors and lowcased) was blocked by word: <yellow>"
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

    public static @Nullable String formatText(CommandSender sender, @Nullable String text, String basePermission) {
        if (text == null) {
            return null;
        }
        Component formatted = formatComponent(sender, text, basePermission);
        return MiniMessage.miniMessage().serialize(formatted);
    }

    public static Component formatComponent(CommandSender sender, @Nullable String text, String basePermission) {
        return formatText(sender, UtilsString.fix2(legacyHexToMiniMessage(text), null, true), basePermission);
    }

    /**
     * Formats text immediately unless it must remain a MiniMessage template until
     * a server item is generated for its recipient.
     */
    public static Component formatItemTemplate(CommandSender sender, @Nullable String text, String basePermission) {
        if (text == null) {
            return Component.empty();
        }

        // These tags generate arbitrary RGB colors, so deferring them requires
        // the same hexa-color permission that the regular formatter enforces.
        if (isDeferredItemTemplate(text)
                && (basePermission == null || sender.hasPermission(basePermission + ".color.hexa"))) {
            return Component.text(text);
        }
        return formatComponent(sender, text, basePermission);
    }

    public static String getPlainTextItemTemplate(CommandSender sender, Component component, String basePermission) {
        if (component instanceof TextComponent text
                && isDeferredItemTemplate(text.content())
                && (basePermission == null || sender.hasPermission(basePermission + ".color.hexa"))) {
            return getPlainText(formatComponent(sender, text.content(), basePermission));
        }
        return getPlainText(formatText(sender, component, basePermission));
    }

    public static String serializeItemTemplate(Component component) {
        if (component instanceof TextComponent text && isDeferredItemTemplate(text.content())) {
            return text.content();
        }
        return MiniMessage.miniMessage().serialize(component);
    }

    public static boolean isDeferredItemTemplate(String text) {
        String lowerCase = text.toLowerCase(Locale.ROOT);
        return lowerCase.contains("<transition")
                || (countCharacters(text, '%') >= 2
                && (lowerCase.contains("<rainbow")
                || lowerCase.contains("<gradient")
                || lowerCase.contains("<pride")
                || lowerCase.contains("<head")));
    }

    public static boolean isDeferredItemTemplate(Component component) {
        return component instanceof TextComponent text && isDeferredItemTemplate(text.content());
    }

    public static Component formatText(CommandSender sender, @Nullable Component text, String basePermission) {
        if (text == null || basePermission == null) {
            return text == null ? Component.empty() : text;
        }

        TextColor color = text.color();
        if (color != null) {
            String colorName = color instanceof NamedTextColor named ? NamedTextColor.NAMES.key(named) : null;
            String colorPermission = colorName == null ? "hexa" : colorName;
            if (!sender.hasPermission(basePermission + ".color." + colorPermission)) {
                text = text.color(null);
            }
        }

        for (TextDecoration decoration : TextDecoration.values()) {
            if (text.decoration(decoration) != TextDecoration.State.TRUE) {
                continue;
            }
            String permissionName = switch (decoration) {
                case OBFUSCATED -> "magic";
                case BOLD -> "bold";
                case STRIKETHROUGH -> "strikethrough";
                case UNDERLINED -> "underline";
                case ITALIC -> "italic";
            };
            if (!sender.hasPermission(basePermission + ".format." + permissionName)) {
                text = text.decoration(decoration, TextDecoration.State.NOT_SET);
            }
        }

        List<Component> children = text.children().stream()
                .map(child -> formatText(sender, child, basePermission))
                .toList();
        return text.children(children);
    }

    public static String getPlainText(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
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

    public static Component asSuggestCommand(Component text, @Nullable String command) {
        if (command == null || command.isEmpty()) {
            return text;
        }
        return text.clickEvent(ClickEvent.suggestCommand(command));
    }

    public static String asExecuteCommand(String text, @Nullable String command) {
        if (command == null || command.isEmpty()) {
            return text;
        }
        return "<click:run_command:'" + command.replace("'", "''") + "'>" + text + "</click>";
    }

    public static Component asExecuteCommand(Component text, @Nullable String command) {
        return command == null || command.isEmpty() ? text : text.clickEvent(ClickEvent.runCommand(command));
    }

    public static String asCopyToClipboard(String text, @Nullable String copied) {
        if (copied == null || copied.isEmpty()) {
            return text;
        }
        return "<click:copy_to_clipboard:'" + copied.replace("'", "''") + "'>" + text + "</click>";
    }

    public static Component asCopyToClipboard(Component text, @Nullable String copied) {
        return copied == null || copied.isEmpty() ? text : text.clickEvent(ClickEvent.copyToClipboard(copied));
    }

    public static String asOpenUrl(String text, @Nullable String url) {
        if (url == null || url.isEmpty()) {
            return text;
        }
        return "<click:open_url:'" + url.replace("'", "''") + "'>" + text + "</click>";
    }

    public static Component asOpenUrl(Component text, @Nullable String url) {
        return url == null || url.isEmpty() ? text : text.clickEvent(ClickEvent.openUrl(url));
    }

    public static String asHover(String text, @Nullable String hover) {
        if (hover == null || hover.isEmpty()) {
            return text;
        }
        return "<hover:show_text:'" + hover.replace("'", "''") + "'>" + text + "</hover>";
    }

    public static String asHover(String text, @Nullable Component hover) {
        if (hover == null) {
            return text;
        }
        return asHover(text, MiniMessage.miniMessage().serialize(hover));
    }

    public static Component asHover(Component text, @Nullable Component hover) {
        return hover == null ? text : text.hoverEvent(HoverEvent.showText(hover));
    }

    public static Component asHover(Component text, @Nullable String hover) {
        return hover == null || hover.isEmpty()
                ? text
                : asHover(text, MiniMessage.miniMessage().deserialize(hover));
    }

    public static Component asHover(Component text, @Nullable List<Component> hover) {
        return hover == null || hover.isEmpty()
                ? text
                : asHover(text, Component.join(JoinConfiguration.newlines(), hover));
    }

    public static String asHover(String text, @Nullable List<String> hover) {
        if (hover == null || hover.isEmpty()) {
            return text;
        }
        return asHover(text, String.join("\n", hover));
    }

    private static int countCharacters(String text, char character) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == character) {
                count++;
            }
        }
        return count;
    }

    private static @Nullable String legacyHexToMiniMessage(@Nullable String text) {
        if (text == null) {
            return null;
        }
        String normalized = LEGACY_HEX_COLOR.matcher(text).replaceAll("<#$1>");
        Matcher matcher = LEGACY_REPEATED_HEX_COLOR.matcher(normalized);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group().substring(2).replaceAll("[&§]", "");
            matcher.appendReplacement(result, Matcher.quoteReplacement("<#" + hex + ">"));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
