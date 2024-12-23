package emanondev.itemedit;

import emanondev.itemedit.compability.Hooks;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UtilsString {

    UtilsString() {
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
        if (item == null)
            return;

        // prepare title and lore
        String title;
        ArrayList<String> lore;
        if (desc == null || desc.isEmpty()) {
            title = " ";
            lore = null;
        } else if (desc.size() == 1) {
            if (desc.get(0) != null)
                if (!desc.get(0).startsWith(ChatColor.RESET + ""))
                    title = ChatColor.RESET + desc.get(0);
                else
                    title = desc.get(0);
            else
                title = null;
            lore = null;
        } else {
            if (!desc.get(0).startsWith(ChatColor.RESET + ""))
                title = ChatColor.RESET + desc.get(0);
            else
                title = desc.get(0);
            lore = new ArrayList<>();
            for (int i = 1; i < desc.size(); i++)
                if (desc.get(i) != null)
                    if (!desc.get(i).startsWith(ChatColor.RESET + ""))
                        lore.add(ChatColor.RESET + desc.get(i));
                    else
                        lore.add(desc.get(i));
                else
                    lore.add("");
        }

        // apply holders and colors for title and lore
        title = fix(title, p, color, holders);
        lore = fix(lore, p, color, holders);

        // apply title and lore to item
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
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
        if (list == null)
            return null;
        ArrayList<String> newList = new ArrayList<>();
        for (String line : list)
            newList.add(fix(line, player, color, holders));
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
        if (item == null || item.getType() == Material.AIR)
            return null;

        ItemStack itemCopy = new ItemStack(item);
        updateDescription(itemCopy, description, player, color, holders);
        return itemCopy;
    }

    @Contract("!null, _, _, _ -> !null")
    public static String fix(@Nullable String text, @Nullable Player player, boolean color, String... holders) {
        if (text == null)
            return null;

        // holders

        if (holders != null && holders.length % 2 != 0)
            throw new IllegalArgumentException("holder without replacer");
        if (holders != null && holders.length > 0)
            for (int i = 0; i < holders.length; i += 2)
                text = text.replace(holders[i], holders[i + 1]);

        // papi
        if (player != null && Hooks.isPAPIEnabled())
            text = PlaceholderAPI.setPlaceholders(player, text);

        //minimessage
        if (Hooks.hasMiniMessage())
            text = Hooks.getMiniMessageUtil().fromMiniToText(text);


        // color
        if (color)
            text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    /**
     * @param text text to revert
     * @return a string with original colors and formats but with &amp; instead of §
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
