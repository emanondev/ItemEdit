package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.utility.TagContainer;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Tag;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class Aliases {

    private static final Map<String, IAliasSet<?>> types = new HashMap<>();
    public static final EnchAliases ENCHANT = createAndRegister(true, () ->
            VersionUtils.isUpTo(1, 12) ? new EnchAliasesOld() : new EnchAliases());
    public static final AliasSet<PatternType> PATTERN_TYPE = createAndRegister(true, () -> {
        try {
            if (VersionUtils.isAfter(1, 20, 6)) {
                return new BannerPatternAliasesNew();
            }
        } catch (Throwable ignored) {
        }
        return new BannerPatternAliasesOld();
    });
    public static final GenAliases BOOK_TYPE = createAndRegister(
            !VersionUtils.isUpTo(1, 9), GenAliases::new);
    public static final AliasSet<PotionEffectType> POTION_EFFECT = createAndRegister(true, () ->
            new AliasSet<PotionEffectType>("potion_effect", ItemEdit.get()) {

                private final Collection<PotionEffectType> values = grabValues();

                @Override
                public String getName(PotionEffectType type) {
                    String name = type.getName().toLowerCase(Locale.ENGLISH);
                    if (name.startsWith("minecraft:")) {
                        name = name.substring(10);
                    }
                    return name;
                }

                @Override
                public Collection<PotionEffectType> getValues() {
                    return values;
                }

                private Collection<PotionEffectType> grabValues() {
                    HashSet<PotionEffectType> set = new HashSet<>();
                    for (PotionEffectType val : PotionEffectType.values()) {
                        if (val != null) {
                            set.add(val);
                        }
                    }
                    return set;
                }

            });
    public static final AliasSet<DyeColor> COLOR =
            createAndRegister(true, () ->
                    new EnumAliasSet<>("color", ItemEdit.get(), DyeColor.class));
    public static final AnimationAliases ANIMATION =
            createAndRegister(VersionUtils.isAfter(1, 21, 4),
                    AnimationAliases::new);
    public static final AliasSet<ItemFlag> FLAG_TYPE = createAndRegister(true, () -> new EnumAliasSet<ItemFlag>("flag_type", ItemEdit.get(), ItemFlag.class) {
        @Override
        public String getName(ItemFlag type) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            if (name.startsWith("hide_")) {
                name = name.substring("hide_".length());
            }
            return name;
        }
    });
    public static final AliasSet<Boolean> BOOLEAN = createAndRegister(true,
            () -> new AliasSet<Boolean>("boolean", ItemEdit.get()) {

                @Override
                public String getName(Boolean value) {
                    return value ? "true" : "false";
                }

                @Override
                public Collection<Boolean> getValues() {
                    return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
                }

            });
    public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS =
            createAndRegister(true, () ->
                    new EnumAliasSet<>("equip_slot", ItemEdit.get(), EquipmentSlot.class));
    public static final AttributeAliases ATTRIBUTE =
            createAndRegister(!VersionUtils.isUpTo(1, 11), () ->
                    VersionUtils.isUpTo(1, 21, 2) ?
                            new AttributeAliasesOld() :
                            new AttributeAliasesNew()
            );
    public static final OperationAliases OPERATIONS =
            createAndRegister(!VersionUtils.isUpTo(1, 11), OperationAliases::new
            );
    public static final RarityAliases RARITY = createAndRegister(!VersionUtils.isUpTo(1, 20, 4), RarityAliases::new);
    public static final TropicalFishPatternAliases TROPICALPATTERN =
            createAndRegister(true, () -> {
                if (VersionUtils.isUpTo(1, 12)) {
                    return null;
                }
                return new TropicalFishPatternAliases();
            });
    public static final TrimMaterialAliases TRIM_MATERIAL =
            createAndRegister(true, () -> {
                if (VersionUtils.isUpTo(1, 19, 4))
                    return null;
                try {
                    if (VersionUtils.isAfter(1, 20, 2)) {
                        return new TrimMaterialAliasesNew();
                    } else {
                        return new TrimMaterialAliasesOld();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            });
    public static final TrimPatternAliases TRIM_PATTERN =
            createAndRegister(true, () -> {
                if (VersionUtils.isUpTo(1, 19, 4)) {
                    return null;
                }
                try {
                    if (VersionUtils.isAfter(1, 20, 2)) {
                        return new TrimPatternAliasesNew();
                    } else {
                        return new TrimPatternAliasesOld();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    return null;
                }
            });
    public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE =
            createAndRegister(true, () -> new EnumAliasSet<>("firework_type", ItemEdit.get(), FireworkEffect.Type.class));
    public static final AxolotlVariantAliases AXOLOTL_VARIANT =
            createAndRegister(true, () -> {
                if (VersionUtils.isUpTo(1, 17)) {
                    return null;
                }
                return new AxolotlVariantAliases();
            });
    public static final GoatHornSoundAliases GOAT_HORN_SOUND =
            createAndRegister(true, () -> {
                if (VersionUtils.isUpTo(1, 19, 2)) {
                    return null;
                }
                try {
                    return new GoatHornSoundAliases();
                } catch (Throwable e) {
                    return null;
                }
            });
    public static final EquipmentSlotGroupAliases EQUIPMENT_SLOTGROUPS =
            createAndRegister(true, () ->
                    VersionUtils.isAfter(1, 21) ? new EquipmentSlotGroupAliases() : null);
    public static final SoundAliases SOUND =
            createAndRegister(true, () -> {
                if (VersionUtils.isAfter(1, 20, 5)) {
                    return new SoundAliases();
                }
                return null;
            });
    public static final AliasSet<EntityType> ENTITY_TYPE =
            createAndRegister(true, () -> new EnumAliasSet<>(ItemEdit.get(), EntityType.class));
    public static final AliasSet<TagContainer<EntityType>> ENTITY_GROUPS =
            createAndRegister(true, () ->
                    VersionUtils.isAfter(1, 21) ?
                            new TagAliasSet<>("entitygroups", ItemEdit.get(), EntityType.class, Tag.REGISTRY_ENTITY_TYPES) : null);
    private static boolean loaded = false;

    public static <T> void registerAliasType(@Nullable IAliasSet<T> set) {
        registerAliasType(set, false);
    }

    public static <T> void registerAliasType(@Nullable IAliasSet<T> set, boolean forced) {
        if (set == null) {
            return;
        }
        if (!forced && types.containsKey(set.getId())) {
            throw new IllegalArgumentException("Duplicate id");
        }
        types.put(set.getId(), set);
    }

    public static <T> void registerAliasType(@Nullable Supplier<IAliasSet<T>> supplier) {
        registerAliasType(supplier, false);
    }

    public static <T> void registerAliasType(@Nullable Supplier<IAliasSet<T>> supplier, boolean forced) {
        if (supplier == null) {
            return;
        }
        try {
            IAliasSet<T> set = supplier.get();
            if (!forced && types.containsKey(set.getId())) {
                throw new IllegalArgumentException("Duplicate id");
            }
            types.put(set.getId(), set);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static IAliasSet<?> getAliasType(@NotNull String id) {
        return types.get(id);
    }

    public static void reload() {
        if (!loaded) {
            loaded = true;
        }
        for (IAliasSet<?> set : types.values()) {
            set.reload();
        }
    }

    public static Map<String, IAliasSet<?>> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    private static <K, T extends AliasSet<K>> T createAndRegister(boolean condition, Supplier<T> supplier) {
        if (!condition) {
            return null;
        }
        try {
            T value = supplier.get();
            if (value == null) {
                return value;
            }
            registerAliasType(value);
            return value;
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

}
