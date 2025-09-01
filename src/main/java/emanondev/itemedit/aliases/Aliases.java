package emanondev.itemedit.aliases;

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

public class Aliases {

    public static final EnchAliases ENCHANT = getEnchAliases();
    public static final AliasSet<PatternType> PATTERN_TYPE = getPatternAlias();
    public static final GenAliases BOOK_TYPE = getGenAliases();
    public static final AliasSet<PotionEffectType> POTION_EFFECT = new AliasSet<PotionEffectType>("potion_effect") {

        private final Collection<PotionEffectType> values = grabValues();

        @Override
        public String getName(PotionEffectType type) {
            String name = type.getName().toLowerCase(Locale.ENGLISH);
            if (name.startsWith("minecraft:")) {
                name = name.substring(10);
            }
            return name;
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

        @Override
        public Collection<PotionEffectType> getValues() {
            return values;
        }

    };
    public static final AliasSet<DyeColor> COLOR = new EnumAliasSet<>("color", DyeColor.class);
    public static final AnimationAliases ANIMATION = VersionUtils.isVersionAfter(1, 21, 4) ? new AnimationAliases() : null;
    public static final AliasSet<String> ANIMATION_OLD = VersionUtils.isVersionInRange(1, 20, 5, 1, 21, 3) ?
            new AliasSet<String>("animations") {

                private final LinkedHashSet<String> values = new LinkedHashSet<>(craftValues());

                @Override
                public String getName(String type) {
                    return type.toLowerCase(Locale.ENGLISH);
                }

                private List<String> craftValues() {
                    return Arrays.asList("drink", "eat", "crossbow",
                            "none", "block", "bow", "spear", "spyglass", "toot_horn", "brush");
                }

                @Override
                public Collection<String> getValues() {
                    return values;
                }
            } : null;
    public static final EggTypeAliases EGG_TYPE = getEggTypeAliases();
    public static final AliasSet<ItemFlag> FLAG_TYPE = new EnumAliasSet<ItemFlag>("flag_type", ItemFlag.class) {
        @Override
        public String getName(ItemFlag type) {
            String name = type.name().toLowerCase(Locale.ENGLISH);
            if (name.startsWith("hide_")) {
                name = name.substring("hide_".length());
            }
            return name;
        }
    };
    public static final AliasSet<Boolean> BOOLEAN = new AliasSet<Boolean>("boolean") {

        @Override
        public String getName(Boolean value) {
            return value ? "true" : "false";
        }

        @Override
        public Collection<Boolean> getValues() {
            return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
        }

    };
    public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS =
            new EnumAliasSet<>("equip_slot", EquipmentSlot.class);
    public static final AttributeAliases ATTRIBUTE = getAttributeAliases();
    public static final OperationAliases OPERATIONS = getAttributeOperationAliases();
    public static final RarityAliases RARITY = getRarityAliases();
    public static final TropicalFishPatternAliases TROPICALPATTERN = getTropicalPatternAliases();
    public static final TrimMaterialAliases TRIM_MATERIAL = getTrimMaterialAliases();
    public static final TrimPatternAliases TRIM_PATTERN = getTrimPatternAliases();
    public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE =
            new EnumAliasSet<>("firework_type", FireworkEffect.Type.class);
    public static final AxolotlVariantAliases AXOLOTL_VARIANT = getAxolotlVariantAliases();
    public static final GoatHornSoundAliases GOAT_HORN_SOUND = getGoatHornSoundAliases();
    public static final EquipmentSlotGroupAliases EQUIPMENT_SLOTGROUPS =
            VersionUtils.isVersionAfter(1, 21) ? new EquipmentSlotGroupAliases() : null;
    public static final SoundAliases SOUND = getSoundAliases();
    public static final AliasSet<EntityType> ENTITY_TYPE = new EnumAliasSet<>(EntityType.class);
    public static final AliasSet<TagContainer<EntityType>> ENTITY_GROUPS =
            VersionUtils.isVersionAfter(1, 21) ?
                    new TagAliasSet<>("entitygroups", EntityType.class, Tag.REGISTRY_ENTITY_TYPES) : null;
    public static final AliasSet<String> CONSUMABLE_EFFECT = VersionUtils.isVersionAfter(1, 21, 4)?new AliasSet<String>("consumable_effect") {
        @Override
        public String getName(String value) {
            return value;
        }

        @Override
        public Collection<String> getValues() {
            return Arrays.asList(
                    "apply",
                    "applymany",
                    "remove",
                    "clear",
                    "sound",
                    "teleport"
            );
        }
    }:null;

    private static final Map<String, IAliasSet<?>> types = new HashMap<>();
    private static boolean loaded = false;

    private static AliasSet<PatternType> getPatternAlias() {
        try {
            if (VersionUtils.isVersionAfter(1, 20, 6)) {
                return new BannerPatternAliasesNew();
            }
        } catch (Throwable ignored) {
        }
        return new BannerPatternAliasesOld();
    }

    private static TrimMaterialAliases getTrimMaterialAliases() {
        if (VersionUtils.isVersionUpTo(1, 19, 4))
            return null;
        try {
            if (VersionUtils.isVersionAfter(1, 20, 2)) {
                return new TrimMaterialAliasesNew();
            } else {
                return new TrimMaterialAliasesOld();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static TrimPatternAliases getTrimPatternAliases() {
        if (VersionUtils.isVersionUpTo(1, 19, 4)) {
            return null;
        }
        try {
            if (VersionUtils.isVersionAfter(1, 20, 2)) {
                return new TrimPatternAliasesNew();
            } else {
                return new TrimPatternAliasesOld();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void loadTypesMap() {
        registerAliasType(ENCHANT);
        registerAliasType(PATTERN_TYPE);
        registerAliasType(BOOK_TYPE);
        registerAliasType(POTION_EFFECT);
        registerAliasType(COLOR);
        registerAliasType(EGG_TYPE);
        registerAliasType(FLAG_TYPE);
        registerAliasType(BOOLEAN);
        registerAliasType(EQUIPMENT_SLOTS);
        registerAliasType(FIREWORK_TYPE);
        registerAliasType(ATTRIBUTE);
        registerAliasType(OPERATIONS);
        registerAliasType(TROPICALPATTERN);
        registerAliasType(AXOLOTL_VARIANT);
        registerAliasType(GOAT_HORN_SOUND);
        registerAliasType(TRIM_MATERIAL);
        registerAliasType(TRIM_PATTERN);
        registerAliasType(RARITY);
        registerAliasType(EQUIPMENT_SLOTGROUPS);
        registerAliasType(ANIMATION_OLD);
        registerAliasType(SOUND);
        registerAliasType(ENTITY_TYPE);
        registerAliasType(ENTITY_GROUPS);
    }

    public static <T> void registerAliasType(@Nullable IAliasSet<T> set) {
        registerAliasType(set, false);
    }

    public static <T> void registerAliasType(@Nullable IAliasSet<T> set, boolean forced) {
        if (set == null) {
            return;
        }
        if (!forced && types.containsKey(set.getID())) {
            throw new IllegalArgumentException("Duplicate id");
        }
        types.put(set.getID(), set);
    }

    public static IAliasSet<?> getAliasType(@NotNull String id) {
        return types.get(id);
    }

    public static void reload() {
        if (!loaded) {
            loaded = true;
            loadTypesMap();
        }
        for (IAliasSet<?> set : types.values()) {
            set.reload();
        }
    }

    public static Map<String, IAliasSet<?>> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    private static EggTypeAliases getEggTypeAliases() {
        if (VersionUtils.isVersionInRange(1, 11, 1, 12)) {
            return new EggTypeAliases();
        }
        return null;
    }

    private static TropicalFishPatternAliases getTropicalPatternAliases() {
        if (VersionUtils.isVersionUpTo(1, 12)) {
            return null;
        }
        return new TropicalFishPatternAliases();
    }

    private static GenAliases getGenAliases() {
        if (VersionUtils.isVersionUpTo(1, 9)) {
            return null;
        }
        return new GenAliases();
    }

    @NotNull
    private static EnchAliases getEnchAliases() {
        if (VersionUtils.isVersionUpTo(1, 12)) {
            return new EnchAliasesOld();
        }
        return new EnchAliases();
    }

    private static AttributeAliases getAttributeAliases() {
        if (VersionUtils.isVersionUpTo(1, 11)) {
            return null;
        }
        if (VersionUtils.isVersionUpTo(1, 21, 2)) {
            return new AttributeAliasesOld();
        }
        return new AttributeAliasesNew();
    }

    private static OperationAliases getAttributeOperationAliases() {
        if (VersionUtils.isVersionUpTo(1, 11)) {
            return null;
        }
        return new OperationAliases();
    }

    private static RarityAliases getRarityAliases() {
        if (VersionUtils.isVersionUpTo(1, 20, 4)) {
            return null;
        }
        try {
            return new RarityAliases();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private static AxolotlVariantAliases getAxolotlVariantAliases() {
        if (VersionUtils.isVersionUpTo(1, 17)) {
            return null;
        }
        return new AxolotlVariantAliases();
    }


    private static GoatHornSoundAliases getGoatHornSoundAliases() {
        if (VersionUtils.isVersionUpTo(1, 19, 2)) {
            return null;
        }
        try {
            return new GoatHornSoundAliases();
        } catch (Throwable e) {
            return null;
        }
    }

    private static SoundAliases getSoundAliases() {
        if (VersionUtils.isVersionAfter(1, 20, 5)) {
            return new SoundAliases();
        }
        return null;
    }

}
