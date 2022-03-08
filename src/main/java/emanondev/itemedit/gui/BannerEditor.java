package emanondev.itemedit.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.aliases.Aliases;
import org.jetbrains.annotations.NotNull;

public class BannerEditor implements Gui {

    private BannerMeta meta;
    private final Player target;
    private static final YMLConfig config = ItemEdit.get().getConfig("itemedit.yml");
    private static final String subPath = "sub-commands.banner.";
    private final Inventory inventory;
    private final List<BannerData> layers = new ArrayList<>();
    private final ItemStack banner;
    // required to exclude BASE PatternType
    private final static PatternType[] TYPES = Arrays.copyOfRange(PatternType.values(), 1, PatternType.values().length);

    private class BannerData {
        private Pattern pattern;
        private boolean active = false;

        private BannerData() {
            pattern = new Pattern(DyeColor.values()[(int) (Math.random() * DyeColor.values().length)],
                    TYPES[(int) (Math.random() * TYPES.length)]);
        }

        private BannerData(Pattern pattern) {
            this.pattern = pattern == null ? new Pattern(DyeColor.BLUE, TYPES[0]) : pattern;
            active = true;
        }

        public ItemStack getPatternTypeItem() {
            if (!active)
                return null;
            ItemStack item = new ItemStack(Material.WHITE_BANNER);
            BannerMeta bMeta = (BannerMeta) item.getItemMeta();
            bMeta.addPattern(new Pattern(DyeColor.BLACK, pattern.getPattern()));
            bMeta.addItemFlags(ItemFlag.values());
            bMeta.setDisplayName(
                    UtilsString.fix("&e" + Aliases.PATTERN_TYPE.getName(pattern.getPattern()), null, true));
            bMeta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.type",
                    Arrays.asList("&bType Selector", "&bClick to change type"), target, false), null, true));
            item.setItemMeta(bMeta);
            return item;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public ItemStack getColorItem() {
            if (!active)
                return null;
            ItemStack item = Util.getItemFromColor(pattern.getColor());
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            meta.setDisplayName(UtilsString.fix("&e" + Aliases.COLOR.getName(pattern.getColor()), null, true));
            meta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.color",
                    Arrays.asList("&bColor Selector", "&bClick to change color"), target, false), null, true));
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getPositionItem() {
            ItemStack item;
            try {
                item = new ItemStack(active ? Material.ITEM_FRAME : Material.GRAY_DYE);
            } catch (Throwable t) {
                item = Util.getItemFromColor(DyeColor.GRAY);
            }
            ItemMeta meta = item.getItemMeta();

            meta.addItemFlags(ItemFlag.values());
            List<String> list = new ArrayList<>(
                    config.getStringList(
                            subPath + "buttons.position", Arrays.asList("&bPattern Controller", "",
                                    "&bMiddle Click to toggle pattern", "&bLeft/Right Click to move this pattern"),
                            true));
            meta.setDisplayName(list.size() > 0 ? list.remove(0) : "");
            meta.setLore(list);
            item.setItemMeta(meta);
            return item;
        }

        public void onClick(int line, InventoryClickEvent event) {
            switch (line) {
                case 2:
                    target.openInventory(new PatternSelector(this).getInventory());
                    return;
                case 3:
                    target.openInventory(new ColorSelector(this).getInventory());
                    return;
            }
        }

        public void setColor(DyeColor dyeColor) {
            pattern = new Pattern(dyeColor, pattern.getPattern());
        }

        public void setPattern(PatternType type) {
            pattern = new Pattern(pattern.getColor(), type);
        }

    }

    public BannerEditor(Player target, ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof BannerMeta))
            try {
                item = new ItemStack(Material.WHITE_BANNER);
            } catch (Exception e) {
                item = new ItemStack(Material.valueOf("BANNER"));
            }
        this.banner = item;
        this.meta = (BannerMeta) banner.getItemMeta();
        this.target = target;
        String title = UtilsString.fix(config.loadString(subPath + "gui.title", "", false), target, true,
                "%player_name%", target.getName());
        this.inventory = Bukkit.createInventory(this, (6) * 9, title);
        for (int i = 0; i < 8; i++) {
            if (i < meta.getPatterns().size())
                layers.add(new BannerData(meta.getPatterns().get(i)));
            else
                layers.add(new BannerData());
        }
        updateInventory();
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().equals(target))
            return;
        if (!inventory.equals(event.getClickedInventory()))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;
        if (event.getSlot() % 9 == 0) {
            target.openInventory(new ColorSelector(null).getInventory());
            return;
        }
        BannerData layer = layers.get(event.getSlot() % 9 - 1);
        if (event.getSlot() > 9 && event.getSlot() < 18) {
            if (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.CREATIVE) {

                layer.active = !layer.active;
            } else if (event.isLeftClick()) {
                if (event.getSlot() == 10)
                    return;
                layers.add(event.getSlot() - 2 - 9, layers.remove(event.getSlot() - 9 - 1));
            } else if (event.isRightClick()) {
                if (event.getSlot() == 17)
                    return;
                layers.add(event.getSlot() - 9, layers.remove(event.getSlot() - 9 - 1));
            }
            updateInventory();
            return;
        }
        if (event.getSlot() < 45) {
            if (!layer.active)
                return;
            layer.onClick(event.getSlot() / 9, event);
            updateInventory();
            return;
        }
        if (event.getSlot() == 49) {
            if (event.isLeftClick())
                try {
                    target.getInventory().setItemInMainHand(banner);
                } catch (Throwable t) {
                    target.getInventory().setItemInHand(banner);
                }
            else
                target.getInventory().addItem(banner);
            return;
        }
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        updateInventory();
    }

    @SuppressWarnings("deprecation")
    private void updateInventory() {
        meta.setPatterns(new ArrayList<>());
        ItemStack item = banner.clone();
        item.setItemMeta(meta);
        meta = (BannerMeta) item.getItemMeta();
        item.setAmount(1);
        this.getInventory().setItem(0, item);
        DyeColor bcolor;
        try {
            bcolor = DyeColor.valueOf(item.getType().name().split("_")[0]);
        } catch (Exception e) {
            bcolor = meta.getBaseColor();
        }
        item = Util.getItemFromColor(bcolor);
        ItemMeta bmeta = item.getItemMeta();
        bmeta.addItemFlags(ItemFlag.values());
        bmeta.setDisplayName(UtilsString.fix("&e" + Aliases.COLOR.getName(bcolor), null, true));
        bmeta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.color",
                Arrays.asList("&bColor Selector", "&bClick to change color"), target, false), null, true));
        item.setItemMeta(bmeta);
        this.getInventory().setItem(27, item);

        for (int i = 1; i < 9; i++) {
            BannerData layer = layers.get(i - 1);
            item = layer.getPositionItem();
            item.setAmount(i);
            this.getInventory().setItem(i + 9, item);
            this.getInventory().setItem(i + 18, layer.getPatternTypeItem());
            this.getInventory().setItem(i + 27, layer.getColorItem());
            if (layer.active) {
                meta.addPattern(layer.getPattern());
                item = banner.clone();
                item.setItemMeta(meta);
                meta = (BannerMeta) item.getItemMeta();
                this.getInventory().setItem(i, item);
            } else
                this.getInventory().setItem(i, null);
        }
        item = banner.clone();
        item.setItemMeta(meta);
        this.getInventory().setItem(49, item);
        getTargetPlayer().getInventory().getItemInMainHand().setItemMeta(meta);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Player getTargetPlayer() {
        return target;
    }

    private class ColorSelector implements Gui {

        private final BannerData data;
        private final Inventory inventory;

        public ColorSelector(BannerData data) {
            this.data = data;
            String title = UtilsString.fix(config.loadString(subPath + "gui.color_selector_title", "", false), target,
                    true, "%player_name%", target.getName());
            this.inventory = Bukkit.createInventory(this, (6) * 9, title);
            int i = 0;
            for (DyeColor color : DyeColor.values()) {
                ItemStack item = Util.getItemFromColor(color);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(UtilsString.fix(
                        config.loadString(subPath + "buttons.color_selector_info", "&bSelect &6&l%color%", false),
                        target, true, "%color%", Aliases.COLOR.getName(color)));
                item.setItemMeta(meta);
                this.inventory.setItem(i, item);
                i++;
            }
        }

        @Override
        public void onClose(InventoryCloseEvent event) {

        }

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(InventoryClickEvent event) {
            if (!event.getWhoClicked().equals(target))
                return;
            if (!inventory.equals(event.getClickedInventory()))
                return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;
            if (data != null)
                data.setColor(DyeColor.values()[event.getSlot()]);
            else
                try {
                    banner.setType(Material.valueOf(DyeColor.values()[event.getSlot()].name() + "_BANNER"));
                    meta = (BannerMeta) banner.getItemMeta();
                } catch (Throwable t) {
                    // pre 1.13
                    meta.setBaseColor(DyeColor.values()[event.getSlot()]);
                }
            BannerEditor.this.updateInventory();
            BannerEditor.this.getTargetPlayer().openInventory(BannerEditor.this.getInventory());
        }

        @Override
        public void onDrag(InventoryDragEvent event) {
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }

        @Override
        public Player getTargetPlayer() {
            return target;
        }

    }

    private class PatternSelector implements Gui {

        private final BannerData data;
        private final Inventory inventory;

        public PatternSelector(BannerData data) {
            if (data == null)
                throw new NullPointerException();
            this.data = data;
            String title = UtilsString.fix(config.loadString(subPath + "gui.pattern_selector_title", "", false), target,
                    true, "%player_name%", target.getName());
            this.inventory = Bukkit.createInventory(this, (6) * 9, title);
            int i = 0;
            for (PatternType type : TYPES) {
                ItemStack item;
                try {
                    item = new ItemStack(Material.WHITE_BANNER);
                } catch (Throwable t) {
                    item = new ItemStack(Material.valueOf("BANNER"));
                }
                BannerMeta bMeta = (BannerMeta) item.getItemMeta();
                bMeta.addPattern(new Pattern(DyeColor.BLACK, type));
                bMeta.setDisplayName(UtilsString.fix(
                        config.loadString(subPath + "buttons.pattern_selector_info", "&bSelect &6&l%type%", false),
                        target, true, "%type%", Aliases.PATTERN_TYPE.getName(type)));
                item.setItemMeta(bMeta);
                this.inventory.setItem(i, item);
                i++;
            }
        }

        @Override
        public void onClose(InventoryCloseEvent event) {

        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if (!event.getWhoClicked().equals(target))
                return;
            if (!inventory.equals(event.getClickedInventory()))
                return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;
            data.setPattern(TYPES[event.getSlot()]);
            BannerEditor.this.updateInventory();
            BannerEditor.this.getTargetPlayer().openInventory(BannerEditor.this.getInventory());
        }

        @Override
        public void onDrag(InventoryDragEvent event) {
        }

        @Override
        public void onOpen(InventoryOpenEvent event) {
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }

        @Override
        public Player getTargetPlayer() {
            return target;
        }

    }

}
