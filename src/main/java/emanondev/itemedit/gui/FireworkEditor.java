package emanondev.itemedit.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.aliases.Aliases;
import org.jetbrains.annotations.NotNull;

public class FireworkEditor implements Gui {

    private final FireworkMeta meta;
    private final Player target;
    private static final YMLConfig config = ItemEdit.get().getConfig("itemedit.yml");
    private static final String subPath = "sub-commands.firework.";
    private final Inventory inventory;
    private final List<FireworkEffectData> effects = new ArrayList<>();
    private final ItemStack firework;

    private class FireworkEffectData {
        private FireworkEffect.Type type = FireworkEffect.Type.values()[0];
        private final List<DyeColor> colors = new ArrayList<>();
        private final List<DyeColor> fadeColors = new ArrayList<>();
        private DyeColor selectedColor = DyeColor.values()[0];
        private DyeColor selectedFadeColor = DyeColor.values()[0];
        private boolean flicker;
        private boolean trail;
        private boolean active = false;

        private FireworkEffectData() {
            this(null, null, null, false, false);
        }

        private FireworkEffectData(FireworkEffect effect) {
            this(effect.getType(), translateToDyeColor(effect.getColors()), translateToDyeColor(effect.getFadeColors()),
                    effect.hasFlicker(), effect.hasTrail());
            active = true;
        }

        private FireworkEffectData(FireworkEffect.Type type, List<DyeColor> colors, List<DyeColor> fadeColors,
                                   boolean flicker, boolean trail) {
            if (type != null)
                this.type = type;
            if (colors != null)
                this.colors.addAll(colors);
            if (fadeColors != null)
                this.fadeColors.addAll(fadeColors);
            this.flicker = flicker;
            this.trail = trail;
        }

        @SuppressWarnings("deprecation")
        public ItemStack getTypeItem() {
            if (!active)
                return null;
            ItemStack item;
            switch (type) {
                case BALL:
                    try {
                        item = new ItemStack(Material.FIREWORK_STAR);
                    } catch (Throwable t) {
                        item = new ItemStack(Material.valueOf("FIREWORK_CHARGE"));
                    }
                    break;
                case BALL_LARGE:
                    try {
                        item = new ItemStack(Material.FIRE_CHARGE);
                    } catch (Exception e) {
                        item = new ItemStack(Material.valueOf("FIREBALL"));
                    }

                    break;
                case BURST:
                    item = new ItemStack(Material.FEATHER);
                    break;
                case CREEPER:
                    try {
                        item = new ItemStack(Material.CREEPER_HEAD);
                    } catch (Throwable t) {
                        item = new ItemStack(Material.valueOf("SKULL"), 1, (short) 0, (byte) 4);
                    }
                    break;
                case STAR:
                    item = new ItemStack(Material.GOLD_NUGGET);
                    break;
                default:
                    item = new ItemStack(Material.ARROW);
                    break;
            }
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            meta.setDisplayName(UtilsString.fix("&e" + Aliases.FIREWORK_TYPE.getName(type), null, true));
            meta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.type",
                    Arrays.asList("&bType Selector", "&bClick to change type"), target, false), null, true));
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getColorsItem() {
            if (!active)
                return null;
            ItemStack item = Util.getItemFromColor(selectedColor);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            List<String> colorNames = new ArrayList<>();
            for (DyeColor color : colors)
                colorNames.add(Aliases.COLOR.getName(color));

            meta.setDisplayName(UtilsString.fix("&bColors: &e" + String.join("&b, &e", colorNames), null, true));
            meta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.colors",
                    Arrays.asList("&bColor Selector", "&bSelected color: &6%color%", "",
                            "&bMiddle Click to add &e%color%", "&bRight/Left Click to change selected color",
                            "&bShift Click to delete last color"),
                    target, false, "%color%", Aliases.COLOR.getName(selectedColor)), null, true));
            item.setItemMeta(meta);
            item.setAmount(Math.max(colors.size(), 1));
            return item;
        }

        public ItemStack getFadeColorsItem() {
            if (!active)
                return null;
            ItemStack item = Util.getItemFromColor(selectedFadeColor);
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            List<String> colorNames = new ArrayList<>();
            for (DyeColor color : fadeColors)
                colorNames.add(Aliases.COLOR.getName(color));

            meta.setDisplayName(UtilsString.fix("&bFadeColors: &e" + String.join("&b, &e", colorNames), null, true));
            meta.setLore(UtilsString.fix(config.getStringList(subPath + "buttons.fadecolors",
                    Arrays.asList("&bFade Color Selector", "&bSelected color: &6%color%", "",
                            "&bMiddle Click to add &e%color%", "&bRight/Left Click to change selected color",
                            "&bShift Click to delete last color"),
                    target, false, "%color%", Aliases.COLOR.getName(selectedFadeColor)), null, true));
            item.setItemMeta(meta);
            item.setAmount(Math.max(fadeColors.size(), 1));
            return item;
        }

        public ItemStack getTrailFlickerItem() {
            if (!active)
                return null;
            ItemStack item;
            try {
                item = new ItemStack(trail ? Material.DIAMOND : Material.GRAY_DYE);
            } catch (Throwable t) {
                item = Util.getItemFromColor(DyeColor.GRAY);
            }
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            if (flicker)
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.setDisplayName(config.getString(
                    subPath + "buttons.flags."
                            + (trail ? (flicker ? "both" : "trail") : (flicker ? "flicker" : "none")),
                    trail ? (flicker ? "&eTrail&b and &eFlicker &aActive" : "&eTrail &aActive")
                            : (flicker ? "&eFlicker &aActive" : "&cNothing active"),
                    true));
            meta.setLore(
                    config.getStringList(
                            subPath + "buttons.flags.info", Arrays.asList("&bTrail and Flicker Activator", "",
                                    "&bRight Click to toggle Flicker effect", "&bLeft Click to toggle Trail effect"),
                            true));
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getPositionItem() {
            ItemStack item;
            try {
                item = new ItemStack(
                        active ? (colors.isEmpty() ? Material.RED_DYE : Material.LIME_DYE) : Material.GRAY_DYE);
            } catch (Throwable t) {
                item = Util
                        .getItemFromColor(active ? (colors.isEmpty() ? DyeColor.RED : DyeColor.LIME) : DyeColor.GRAY);
            }
            ItemMeta meta = item.getItemMeta();

            meta.addItemFlags(ItemFlag.values());
            List<String> list = new ArrayList<>(
                    config.getStringList(
                            subPath + "buttons.position", Arrays.asList("&bEffect Controller", "",
                                    "&bMiddle Click to toggle effect", "&bLeft/Right Click to move this effect"),
                            true));
            meta.setDisplayName(list.size() > 0 ? list.remove(0) : "");
            meta.setLore(list);
            item.setItemMeta(meta);
            return item;
        }

        public FireworkEffect getEffect() {
            return FireworkEffect.builder().with(type).flicker(flicker).trail(trail)
                    .withFade(translateToColor(fadeColors)).withColor(translateToColor(colors)).build();
        }

        public void onClick(int line, InventoryClickEvent event) {
            switch (line) {
                case 1: {// type
                    if (event.isLeftClick())
                        type = FireworkEffect.Type.values()[(type.ordinal() + 1) % FireworkEffect.Type.values().length];
                    else
                        type = FireworkEffect.Type.values()[(type.ordinal() - 1 + FireworkEffect.Type.values().length)
                                % FireworkEffect.Type.values().length];
                }
                return;
                case 2: {// color
                    if (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.CREATIVE) {
                        if (colors.size() < 9)
                            colors.add(selectedColor);
                    } else if (event.isShiftClick()) {
                        if (colors.size() > 0)
                            colors.remove(colors.size() - 1);
                    } else if (event.isRightClick())
                        selectedColor = DyeColor.values()[(selectedColor.ordinal() + 1) % DyeColor.values().length];
                    else
                        selectedColor = DyeColor.values()[(selectedColor.ordinal() - 1 + DyeColor.values().length)
                                % DyeColor.values().length];
                    /*
                     * if (!event.isShiftClick()) { if (event.isRightClick()) selectedColor =
                     * DyeColor.values()[(selectedColor.ordinal() + 1) % DyeColor.values().length];
                     * else selectedColor = DyeColor.values()[(selectedColor.ordinal() -
                     * 1+DyeColor.values().length) % DyeColor.values().length]; } else { if
                     * (event.isRightClick()) { if (colors.size() < 9) colors.add(selectedColor); }
                     * else if (event.isLeftClick()) { if (colors.size() > 0)
                     * colors.remove(colors.size() - 1); } }
                     */
                }
                return;
                case 3: {// fadecolor
                    if (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.CREATIVE) {
                        if (fadeColors.size() < 9)
                            fadeColors.add(selectedFadeColor);
                    } else if (event.isShiftClick()) {
                        if (fadeColors.size() > 0)
                            fadeColors.remove(fadeColors.size() - 1);
                    } else if (event.isRightClick())
                        selectedFadeColor = DyeColor.values()[(selectedFadeColor.ordinal() + 1) % DyeColor.values().length];
                    else
                        selectedFadeColor = DyeColor.values()[(selectedFadeColor.ordinal() - 1 + DyeColor.values().length)
                                % DyeColor.values().length];

                    /*
                     * if (!event.isShiftClick()) { if (event.isRightClick()) selectedFadeColor =
                     * DyeColor.values()[(selectedFadeColor.ordinal() + 1) %
                     * DyeColor.values().length]; else selectedFadeColor =
                     * DyeColor.values()[(selectedFadeColor.ordinal() - 1+DyeColor.values().length)
                     * % DyeColor.values().length]; } else { if (event.isRightClick()) { if
                     * (fadeColors.size() < 9) fadeColors.add(selectedFadeColor); } else if
                     * (event.isLeftClick()) { if (fadeColors.size() > 0)
                     * fadeColors.remove(fadeColors.size() - 1); } }
                     */
                }
                return;
                case 4: {// flags
                    if (event.isLeftClick())
                        trail = !trail;
                    if (event.isRightClick())
                        flicker = !flicker;
                }
                return;
            }
        }

    }

    private static List<DyeColor> translateToDyeColor(List<Color> colors) {
        if (colors == null)
            return null;
        List<DyeColor> list = new ArrayList<>();
        for (Color color : colors) {
            DyeColor col = DyeColor.getByFireworkColor(color);
            if (col != null)
                list.add(col);
        }
        return list;
    }

    private static List<Color> translateToColor(List<DyeColor> colors) {
        if (colors == null)
            return null;
        List<Color> list = new ArrayList<>();
        for (DyeColor color : colors) {
            Color col = color.getFireworkColor();
            if (col != null)
                list.add(col);
        }
        return list;
    }

    public FireworkEditor(Player target, ItemStack item) {
        if (item == null || !(item.getItemMeta() instanceof FireworkMeta))
            try {
                item = new ItemStack(Material.FIREWORK_ROCKET);
            } catch (Exception e) {
                item = new ItemStack(Material.valueOf("FIREWORK"));
            }
        this.firework = item;
        this.meta = (FireworkMeta) firework.getItemMeta();
        this.target = target;
        String title = UtilsString.fix(config.loadString(subPath + "gui.title", "", false), target, true,
                "%player_name%", target.getName());
        this.inventory = Bukkit.createInventory(this, (6) * 9, title);
        for (int i = 0; i < 9; i++) {
            if (i < meta.getEffects().size())
                effects.add(new FireworkEffectData(meta.getEffects().get(i)));
            else
                effects.add(new FireworkEffectData());
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
        if (event.getSlot() < 9) {
            if (event.getClick() == ClickType.MIDDLE || event.getClick() == ClickType.CREATIVE) {
                effects.get(event.getSlot()).active = !effects.get(event.getSlot()).active;
            }
            if (event.isLeftClick()) {
                if (event.getSlot() == 0)
                    return;
                effects.add(event.getSlot() - 1, effects.remove(event.getSlot()));
            } else if (event.isRightClick()) {
                if (event.getSlot() == 8)
                    return;
                effects.add(event.getSlot() + 1, effects.remove(event.getSlot()));
            }
            updateInventory();
            return;
        }
        if (event.getSlot() < 45) {
            if (!effects.get(event.getSlot() % 9).active)
                return;
            effects.get(event.getSlot() % 9).onClick(event.getSlot() / 9, event);
            updateInventory();
            return;
        }
        if (event.getSlot() == 47) {
            if (event.isLeftClick())
                meta.setPower((meta.getPower() + 1) % 6);
            else
                meta.setPower((meta.getPower() - 1) % 6);
            updateInventory();
            return;
        }
        if (event.getSlot() == 49) {
            if (event.isLeftClick())
                try {
                    target.getInventory().setItemInMainHand(firework);
                } catch (Throwable t) {
                    target.getInventory().setItemInHand(firework);
                }
            else
                target.getInventory().addItem(firework);
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

    private void updateInventory() {
        meta.clearEffects();
        for (int i = 0; i < 9; i++) {
            ItemStack item = effects.get(i).getPositionItem();
            item.setAmount(i + 1);
            this.getInventory().setItem(i, item);
            this.getInventory().setItem(i + 9, effects.get(i).getTypeItem());
            this.getInventory().setItem(i + 18, effects.get(i).getColorsItem());
            this.getInventory().setItem(i + 27, effects.get(i).getFadeColorsItem());
            this.getInventory().setItem(i + 36, effects.get(i).getTrailFlickerItem());
            if (effects.get(i).active && !effects.get(i).colors.isEmpty())
                meta.addEffect(effects.get(i).getEffect());
        }
        firework.setItemMeta(meta);
        this.getInventory().setItem(49, firework);
        ItemStack item;
        try {
            item = new ItemStack(Material.GUNPOWDER);

        } catch (Throwable t) {
            item = new ItemStack(Material.valueOf("SULPHUR"));
        }
        item.setAmount(meta.getPower() + 1);
        ItemMeta powerMeta = item.getItemMeta();
        powerMeta.addItemFlags(ItemFlag.values());
        List<String> list = new ArrayList<>(config.getStringList(subPath + "buttons.power",
                Arrays.asList("&bPower: &e%power%", "", "&bLeft/Right Click to change"), null, true, "%power%",
                String.valueOf(meta.getPower() + 1)));
        powerMeta.setDisplayName(list.size() > 0 ? list.remove(0) : "");
        powerMeta.setLore(list);
        item.setItemMeta(powerMeta);
        this.getInventory().setItem(47, item);
        item = Util.getItemFromColor(DyeColor.LIGHT_BLUE);
        /*
         * ItemMeta confirmMeta = item.getItemMeta();
         * confirmMeta.addItemFlags(ItemFlag.values()); list = new
         * ArrayList<>(config.getStringList(subPath + "buttons.confirm",
         * Arrays.asList("&bLeft Click to override you item",
         * "&bRight Click to obtain a copy"), null, true)); if (list.size() > 0)
         * confirmMeta.setDisplayName(list.size() > 0 ? list.remove(0) : "");
         * confirmMeta.setLore(list); item.setItemMeta(confirmMeta);
         * this.getInventory().setItem(51, item);
         */
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
