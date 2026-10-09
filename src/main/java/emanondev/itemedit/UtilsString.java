package emanondev.itemedit;

import emanondev.itemedit.compability.Hooks;
import emanondev.itemedit.utility.ItemUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UtilsString {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final Pattern LEGACY_CODE = Pattern.compile("(?i)([§&])([0-9a-fk-or])");

    private UtilsString() {
        throw new UnsupportedOperationException();
    }

    /**
     * Update the item with the description, covering both title and lore
     *
     * @param item    item to update
     * @param desc    raw text
     * @param p       player or null for placeHolderApi use
     * @param color   translate colors
     * @param holders additional Holders, must be even number with the format "to
     *                replace","replacer","to replace 2","replacer 2"....
     */
    public static void updateDescription(@Nullable ItemStack item, @Nullable List<String> desc, @Nullable Player p, boolean color,
                                         String... holders) {
        if (item == null) {
            return;
        }

        // prepare title and lore
        String title;
        ArrayList<String> lore;
        if (desc == null || desc.isEmpty()) {
            title = " ";
            lore = null;
        } else if (desc.size() == 1) {
            if (desc.get(0) != null) {
                if (!desc.get(0).startsWith(ChatColor.RESET + "")) {
                    title = ChatColor.RESET + desc.get(0);
                } else {
                    title = desc.get(0);
                }
            } else {
                title = null;
            }
            lore = null;
        } else {
            if (!desc.get(0).startsWith(ChatColor.RESET + "")) {
                title = ChatColor.RESET + desc.get(0);
            } else {
                title = desc.get(0);
            }
            lore = new ArrayList<>();
            for (int i = 1; i < desc.size(); i++) {
                if (desc.get(i) != null) {
                    if (!desc.get(i).startsWith(ChatColor.RESET + "")) {
                        lore.add(ChatColor.RESET + desc.get(i));
                    } else {
                        lore.add(desc.get(i));
                    }
                } else {
                    lore.add("");
                }
            }
        }

        // apply title and lore to item
        ItemMeta meta = ItemUtils.getMeta(item);
        meta.displayName(fix2(title, p, color, holders));
        if (lore == null) {
            meta.lore(null);
        } else {
            List<Component> componentLore = new ArrayList<>(lore.size());
            for (String line : lore) {
                componentLore.add(fix2(line, p, color, holders));
            }
            meta.lore(componentLore);
        }
        item.setItemMeta(meta);

    }

    /**
     * @param list    raw text
     * @param player  player or null for placeHolderApi use
     * @param color   translate colors
     * @param holders additional placeholders, must be even number with the format
     *                "to replace#1","replacer#1","to replace#2","replacer#2"....
     * @return a new list with fixed text, or null if list was null
     */
    @Contract("!null, _, _, _ -> !null")
    public static @Nullable ArrayList<String> fix(@Nullable List<String> list, @Nullable Player player, boolean color, String... holders) {
        if (list == null) {
            return null;
        }
        ArrayList<String> newList = new ArrayList<>();
        for (String line : list) {
            newList.add(fix(line, player, color, holders));
        }
        return newList;
    }

    /**
     * @param list    raw text
     * @param player  player or null for placeHolderApi use
     * @param color   translate colors
     * @param holders additional placeholders, must be even number with the format
     *                "to replace#1","replacer#1","to replace#2","replacer#2"....
     * @return a new list with fixed text, or null if list was null
     */
    @Contract("!null, _, _, _ -> !null")
    public static @Nullable ArrayList<Component> fix2(@Nullable List<String> list, @Nullable Player player, boolean color, String... holders) {
        if (list == null) {
            return null;
        }
        ArrayList<Component> newList = new ArrayList<>();
        for (String line : list) {
            newList.add(fix2(line, player, color, holders));
        }
        return newList;
    }

    /**
     * Set the description on an item clone, covering both title and lore original
     * item is unmodified
     *
     * @param item        item to clone and update
     * @param description raw text
     * @param player      player or null for placeHolderApi use
     * @param color       translate colors
     * @param holders     additional placeholders, must be even number with the format
     *                    "to replace","replacer","to replace 2","replacer 2"....
     * @return new item with display name and lore used for desc
     */
    public static ItemStack setDescription(@Nullable ItemStack item, @Nullable List<String> description, @Nullable Player player, boolean color,
                                           String... holders) {
        if (ItemUtils.isAirOrNull(item)) {
            return null;
        }

        ItemStack itemCopy = new ItemStack(item);
        updateDescription(itemCopy, description, player, color, holders);
        return itemCopy;
    }

    @Contract("!null, _, _, _ -> !null")
    @Deprecated
    public static String fix(@Nullable String text, @Nullable Player player, boolean color, String... holders) {
        return replacePlaceholders(text, player, holders);
    }

    @Contract("!null, _, _ -> !null")
    public static @Nullable String replacePlaceholders(@Nullable String text, @Nullable Player player, String... holders) {
        if (text == null)
            return null;

        // holders

        if (holders != null && holders.length % 2 != 0)
            throw new IllegalArgumentException("holder without replacer");
        if (holders != null && holders.length > 0)
            for (int i = 0; i < holders.length; i += 2)
                text = text.replace(holders[i], holders[i + 1]);

        // papi
        if (player != null && Hooks.isPAPIEnabled()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    @Contract("!null, _, _, _ -> !null")
    public static @Nullable Component fix2(@Nullable String text, @Nullable Player player, boolean color, String... holders) {
        String fixed = replacePlaceholders(text, player, holders);
        if (fixed == null) {
            return null;
        }
        return MINI_MESSAGE.deserialize(legacyToMiniMessage(fixed, color));
    }

    /**
     * Replaces holders and PlaceholderAPI values in text nodes while preserving
     * the formatting and events of an already parsed component.
     */
    public static Component replacePlaceholders(Component component, Player player, String... holders) {
        Component replaced = component;
        if (replaced instanceof net.kyori.adventure.text.TextComponent text) {
            replaced = text.content(replacePlaceholders(text.content(), player, holders));
        }

        List<Component> children = replaced.children().stream()
                .map(child -> replacePlaceholders(child, player, holders))
                .toList();
        return replaced.children(children);
    }

    public static Component toSingleComponent(@Nullable List<Component> components) {
        if (components == null || components.isEmpty()) {
            return null;
        }

        List<Component> lines = components.stream()
                .filter(Objects::nonNull)
                .toList();
        if (lines.isEmpty()) {
            return null;
        }

        return Component.join(JoinConfiguration.separator(Component.newline()), lines);
    }

    private static String legacyToMiniMessage(String text, boolean color) {
        Matcher matcher = LEGACY_CODE.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            char prefix = matcher.group(1).charAt(0);
            if (prefix == '&' && !color) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group()));
                continue;
            }

            String tag = switch (matcher.group(2).toLowerCase()) {
                case "0" -> "black";
                case "1" -> "dark_blue";
                case "2" -> "dark_green";
                case "3" -> "dark_aqua";
                case "4" -> "dark_red";
                case "5" -> "dark_purple";
                case "6" -> "gold";
                case "7" -> "gray";
                case "8" -> "dark_gray";
                case "9" -> "blue";
                case "a" -> "green";
                case "b" -> "aqua";
                case "c" -> "red";
                case "d" -> "light_purple";
                case "e" -> "yellow";
                case "f" -> "white";
                case "k" -> "obfuscated";
                case "l" -> "bold";
                case "m" -> "strikethrough";
                case "n" -> "underlined";
                case "o" -> "italic";
                case "r" -> "reset";
                default -> throw new IllegalStateException("Unexpected legacy formatting code");
            };
            matcher.appendReplacement(result, Matcher.quoteReplacement("<" + tag + ">"));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * @param text text to revert
     * @return a string with original colors and formats but with <green>mp; instead of §
     */
    @Contract("!null -> !null")
    public static @Nullable String revertColors(@Nullable String text) {
        if (text == null)
            return null;
        return text.replace("§", "&");
    }

    /**
     * @param text text clear
     * @return a string with no colors and no formats
     */
    @Contract("!null -> !null")
    public static @Nullable String clearColors(@Nullable String text) {
        if (text == null)
            return null;
        return ChatColor.stripColor(text);
    }

    public static @NotNull String formatNumber(double value, int decimals, boolean optional) {
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(decimals);
        df.setMinimumFractionDigits(optional ? 0 : decimals);
        return df.format(value);
    }
}
