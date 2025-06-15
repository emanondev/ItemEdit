package emanondev.itemedit.gui;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ColorGui implements Gui {


    private static final String subPath = "gui.color.";
    private final Player target;
    private final Inventory inventory;
    private final ItemStack colorable;
    private final ItemMeta colorableMeta;
    private final ItemMeta cleanColorableMeta;


    public ColorGui(@NotNull Player target) {
        String title = getLanguageMessage(subPath + "title");
        this.inventory = Bukkit.createInventory(this, (6) * 9, title);
        this.target = target;
        this.colorable = ItemUtils.getHandItem(getTargetPlayer());
        this.colorableMeta = ItemUtils.getMeta(this.colorable);
        cleanColorableMeta = colorableMeta.clone();
        cleanColorableMeta.addItemFlags(ItemFlag.values());
        cleanColorableMeta.setDisplayName(null);
        cleanColorableMeta.setLore(null);
        try {
            cleanColorableMeta.setMaxStackSize(100);
        } catch (Throwable ignored) {
            //for old game versions
        }
        updateInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        try {
            target.getInventory().setItemInMainHand(colorable);
        } catch (Throwable t) {
            target.getInventory().setItemInHand(colorable);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Color original = ItemUtils.getColor(colorableMeta);
        int[] colors = fromColor(original);
        switch (event.getSlot()) {
            case 0:
                colors[0] += 1;
                break;
            case 1:
                colors[0] += 10;
                break;
            case 2:
                colors[0] += 50;
                break;
            case 9:
                colors[0] -= 1;
                break;
            case 10:
                colors[0] -= 10;
                break;
            case 11:
                colors[0] -= 50;
                break;
            case 6:
                colors[2] += 1;
                break;
            case 7:
                colors[2] += 10;
                break;
            case 8:
                colors[2] += 50;
                break;
            case 15:
                colors[2] -= 1;
                break;
            case 16:
                colors[2] -= 10;
                break;
            case 17:
                colors[2] -= 50;
                break;
            case 3:
                colors[1] += 1;
                break;
            case 4:
                colors[1] += 10;
                break;
            case 5:
                colors[1] += 50;
                break;
            case 12:
                colors[1] -= 1;
                break;
            case 13:
                colors[1] -= 10;
                break;
            case 14:
                colors[1] -= 50;
                break;
            case 34:
                colors[0] += 5;
                colors[1] += 5;
                colors[2] += 5;
                break;
            case 35:
                colors[0] += 25;
                colors[1] += 25;
                colors[2] += 25;
                break;
            case 43:
                colors[0] -= 5;
                colors[1] -= 5;
                colors[2] -= 5;
                break;
            case 44:
                colors[0] -= 25;
                colors[1] -= 25;
                colors[2] -= 25;
                break;
            case 27:
                colors = new int[]{255, 255, 255};  // White (#FFFFFF)
                break;
            case 36:
                colors = new int[]{170, 170, 170};  // Light Gray (#AAAAAA)
                break;
            case 37:
                colors = new int[]{85, 85, 85};     // Dark Gray (#555555)
                break;
            case 28:
                colors = new int[]{0, 0, 0};        // Black (#000000)
                break;
            case 45:
                colors = new int[]{65, 105, 225};  // Royal Blue (#4169E1)
                break;
            case 46:
                colors = new int[]{125, 249, 255}; // Electric Blue (#7DF9FF)
                break;
            case 47:
                colors = new int[]{15, 255, 192};  // Lime Cyan (#0FFFC0)
                break;
            case 48:
                colors = new int[]{57, 255, 20};   // Neon Green (#39FF14)
                break;
            case 49:
                colors = new int[]{255, 255, 51};  // Bright Yellow (#FFFF33)
                break;
            case 50:
                colors = new int[]{255, 179, 0};   // Neon Orange (#FFB300)
                break;
            case 51:
                colors = new int[]{255, 95, 31};   // Vivid Orange (#FF5F1F)
                break;
            case 52:
                colors = new int[]{255, 105, 180}; // Hot Pink (#FF69B4)
                break;
            case 53:
                colors = new int[]{191, 0, 255};   // Bright Purple (#BF00FF)
                break;
            default:
                return;
        }
        Color newColor = toColor(colors);
        if (original.equals(newColor)) {
            getTargetPlayer().playSound(getTargetPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 0.5F, 1F);
            return;
        }
        ItemUtils.setColor(colorableMeta, newColor);
        ItemUtils.setColor(cleanColorableMeta, newColor);
        updateInventory();
    }

    private void updateInventory() {
        colorable.setItemMeta(colorableMeta);
        inventory.setItem(31, colorable);

        Color current = ItemUtils.getColor(colorableMeta);

        // RGB channel adjustments
        setupChannel(current, 0, 9, 255, 0, 0, "&c█ %amount%", 'R');
        setupChannel(current, 6, 15, 0, 0, 255, "&9█ %amount%", 'B');
        setupChannel(current, 3, 12, 0, 255, 0, "&a█ %amount%", 'G');

        // All channels
        setupAllChannels(current);

        // Preset colors
        setupPresetColors(current);
    }

    private void setupChannel(Color current, int incStart, int decStart, int r, int g, int b, String title, char channel) {
        int[] amounts = {1, 10, 50};
        // Increments
        for (int i = 0; i < amounts.length; i++) {
            int amount = amounts[i];
            inventory.setItem(incStart + i, createItem(amount, current, toColor(r, g, b),
                    channel == 'R' ? amount : 0, channel == 'G' ? amount : 0, channel == 'B' ? amount : 0, title));
        }
        // Decrements
        for (int i = 0; i < amounts.length; i++) {
            int amount = -amounts[i];
            inventory.setItem(decStart + i, createItem(amount, current, toColor(r, g, b),
                    channel == 'R' ? amount : 0, channel == 'G' ? amount : 0, channel == 'B' ? amount : 0, title));
        }
    }

    private void setupAllChannels(Color current) {
        inventory.setItem(34, createItem(5, current, toColor(255, 255, 255), 5, 5, 5, "&f█ %amount%"));
        inventory.setItem(35, createItem(25, current, toColor(255, 255, 255), 25, 25, 25, "&f█ %amount%"));
        inventory.setItem(43, createItem(-5, current, toColor(0, 0, 0), -5, -5, -5, "&f█ %amount%"));
        inventory.setItem(44, createItem(-25, current, toColor(0, 0, 0), -25, -25, -25, "&f█ %amount%"));
    }

    private void setupPresetColors(Color current) {
        inventory.setItem(27, createItem(current, toColor(255, 255, 255))); // White
        inventory.setItem(36, createItem(current, toColor(170, 170, 170))); // Light Gray
        inventory.setItem(37, createItem(current, toColor(85, 85, 85)));    // Dark Gray
        inventory.setItem(28, createItem(current, toColor(0, 0, 0)));       // Black

        inventory.setItem(45, createItem(current, toColor(65, 105, 225)));  // Royal Blue
        inventory.setItem(46, createItem(current, toColor(125, 249, 255))); // Electric Blue
        inventory.setItem(47, createItem(current, toColor(15, 255, 192)));  // Lime Cyan
        inventory.setItem(48, createItem(current, toColor(57, 255, 20)));   // Neon Green
        inventory.setItem(49, createItem(current, toColor(255, 255, 51)));  // Bright Yellow
        inventory.setItem(50, createItem(current, toColor(255, 179, 0)));   // Neon Orange
        inventory.setItem(51, createItem(current, toColor(255, 95, 31)));   // Vivid Orange
        inventory.setItem(52, createItem(current, toColor(255, 105, 180))); // Hot Pink
        inventory.setItem(53, createItem(current, toColor(191, 0, 255)));   // Bright Purple
    }


    private ItemStack createItem(Color currentColor, Color color) {
        ItemStack stack = colorable.clone();
        stack.setItemMeta(ItemUtils.setColor(cleanColorableMeta.clone(), color));
        stack.setAmount(1);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        String hex = String.format("%02X%02X%02X", red, green, blue);
        String currentHex = String.format("%02X%02X%02X", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
        List<String> description = Arrays.asList(
                "&x&" + String.join("&", currentHex.split("")) + "█ &f-> &x&" + String.join("&", hex.split("")) + "█",
                "",
                "",
                "&f -> (&c█ %red%&f, &a█ %green%&f, &9█ %blue%&f)",
                "&f -> HEX: #%hex%"
        );

        return UtilsString.setDescription(stack, description, target, true,
                "%red%", String.valueOf(red),
                "%green%", String.valueOf(green),
                "%blue%", String.valueOf(blue),
                "%hex%", hex
        );
    }

    private ItemStack createItem(int amount, Color currentColor, Color baseColor, int redDiff, int greenDiff, int blueDiff, String title) {
        ItemStack stack = colorable.clone();

        int nextRed = limit(currentColor.getRed() + redDiff);
        int nextGreen = limit(currentColor.getGreen() + greenDiff);
        int nextBlue = limit(currentColor.getBlue() + blueDiff);

        ItemMeta meta = ItemUtils.setColor(cleanColorableMeta.clone(), baseColor);
        try {
            meta.setMaxStackSize(100);
        } catch (Throwable ignored) {
            //for old game versions
        }
        stack.setItemMeta(meta);
        stack.setAmount(Math.abs(amount));

        String nextHex = String.format("%02X%02X%02X", nextRed, nextGreen, nextBlue);
        String currentHex = String.format("%02X%02X%02X", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
        List<String> description = Arrays.asList(
                "&x&" + String.join("&", currentHex.split("")) + "█ &f-> &x&" + String.join("&", nextHex.split("")) + "█",
                title,
                "",
                "&f(&c█ %red%&f, &a█ %green%&f, &9█ %blue%&f)",
                "&fHEX: #%hex%"
        );

        return UtilsString.setDescription(stack, description, target, true,
                "%red%", String.valueOf(currentColor.getRed()),
                "%green%", String.valueOf(currentColor.getGreen()),
                "%blue%", String.valueOf(currentColor.getBlue()),
                "%hex%", currentHex,
                "%amount%", (amount > 0 ? "+" : "") + amount
        );
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        updateInventory();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Player getTargetPlayer() {
        return target;
    }

    @Override
    public @NotNull ItemEdit getPlugin() {
        return ItemEdit.get();
    }

    private Color toColor(int red, int green, int blue) {
        return Color.fromRGB(limit(red), limit(green), limit(blue));
    }

    private Color toColor(int[] colors) {
        return Color.fromRGB(limit(colors[0]), limit(colors[1]), limit(colors[2]));
    }

    private int[] fromColor(Color color) {
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

    private int limit(int color) {
        return Math.max(0, Math.min(255, color));
    }
}
