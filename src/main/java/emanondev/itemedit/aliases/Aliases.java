package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.utility.TagContainer;
import emanondev.itemedit.utility.VersionUtils;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Registry;
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

@Slf4j
public class Aliases {

    private static final Map<String, IAliasSet<?>> types = new HashMap<>();

    public static final AnimationAliases ANIMATION;
    public static final AttributeAliases ATTRIBUTE;
    public static final AxolotlVariantAliases AXOLOTL_VARIANT;
    public static final GenAliases BOOK_TYPE;
    public static final AliasSet<Boolean> BOOLEAN;
    public static final AliasSet<DyeColor> COLOR;
    public static final EnchAliases ENCHANT;
    public static final AliasSet<TagContainer<EntityType>> ENTITY_GROUPS;
    public static final AliasSet<EntityType> ENTITY_TYPE;
    public static final EquipmentSlotGroupAliases EQUIPMENT_SLOTGROUPS;
    public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS;
    public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE;
    public static final AliasSet<ItemFlag> FLAG_TYPE;
    public static final GoatHornSoundAliases GOAT_HORN_SOUND;
    public static final OperationAliases OPERATIONS;
    public static final AliasSet<PatternType> PATTERN_TYPE;
    public static final AliasSet<PotionEffectType> POTION_EFFECT;
    public static final RarityAliases RARITY;
    public static final SoundAliases SOUND;
    public static final TrimMaterialAliases TRIM_MATERIAL;
    public static final TrimPatternAliases TRIM_PATTERN;
    public static final TropicalFishPatternAliases TROPICALPATTERN;

    static {
        ANIMATION = createAndRegister(VersionUtils.isAfter(1, 21, 4), AnimationAliases::new);
        ATTRIBUTE = createAndRegister(() -> VersionUtils.isUpTo(1, 21, 2)
                ? new AttributeAliasesOld()
                : new AttributeAliasesNew());
        AXOLOTL_VARIANT = createAndRegister(AxolotlVariantAliases::new);
        BOOK_TYPE = createAndRegister(GenAliases::new);
        BOOLEAN = createAndRegister(() -> new AliasSet<Boolean>("boolean", ItemEdit.get()) {

                @Override
                public String getName(Boolean value) {
                    return value.toString();
                }

                @Override
                public Collection<Boolean> getValues() {
                    return Arrays.asList(Boolean.FALSE, Boolean.TRUE);
                }

            });
        COLOR = createAndRegister(() -> new EnumAliasSet<>("color", ItemEdit.get(), DyeColor.class));
        ENCHANT = createAndRegister(EnchAliases::new);
        ENTITY_GROUPS = createAndRegister(() -> VersionUtils.isAfter(1, 21)
                ? new TagAliasSet<>("entitygroups", ItemEdit.get(), EntityType.class, Tag.REGISTRY_ENTITY_TYPES)
                : null);
        ENTITY_TYPE = createAndRegister(() -> new EnumAliasSet<>(ItemEdit.get(), EntityType.class));
        EQUIPMENT_SLOTGROUPS = createAndRegister(EquipmentSlotGroupAliases::new);
        EQUIPMENT_SLOTS = createAndRegister(() -> new EnumAliasSet<>("equip_slot", ItemEdit.get(), EquipmentSlot.class));
        FIREWORK_TYPE = createAndRegister(() -> new EnumAliasSet<>("firework_type", ItemEdit.get(), FireworkEffect.Type.class));
        FLAG_TYPE = createAndRegister(() -> new EnumAliasSet<ItemFlag>("flag_type", ItemEdit.get(), ItemFlag.class) {
                @Override
                public String getName(ItemFlag type) {
                    String name = type.name().toLowerCase(Locale.ENGLISH);
                    if (name.startsWith("hide_")) {
                        name = name.substring("hide_".length());
                    }
                    return name;
                }
            });
        GOAT_HORN_SOUND = createAndRegister(GoatHornSoundAliases::new);
        OPERATIONS = createAndRegister(OperationAliases::new);
        PATTERN_TYPE = createAndRegister(BannerPatternAliases::new);
        POTION_EFFECT = createAndRegister(() ->
            new AliasSet<PotionEffectType>("potion_effect", ItemEdit.get()) {

                private final Collection<PotionEffectType> values = grabValues();

                @Override
                public String getName(PotionEffectType type) {
                    String name = type.getKey().getKey().toLowerCase(Locale.ENGLISH);
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
                    return new HashSet<>(Registry.MOB_EFFECT.stream().toList());
                }
            });
        RARITY = createAndRegister(RarityAliases::new);
        SOUND = createAndRegister(SoundAliases::new);
        TRIM_MATERIAL = createAndRegister(TrimMaterialAliases::new);
        TRIM_PATTERN = createAndRegister(TrimPatternAliases::new);
        TROPICALPATTERN = createAndRegister(TropicalFishPatternAliases::new);
    }

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
            log.warn("Unable to register alias type; skipping it", t);
        }
    }

    public static IAliasSet<?> getAliasType(@NotNull String id) {
        return types.get(id);
    }

    public static void reload() {
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
        return createAndRegister(supplier);
    }

    private static <K, T extends AliasSet<K>> T createAndRegister(Supplier<T> supplier) {
        try {
            T value = supplier.get();
            if (value == null) {
                return value;
            }
            registerAliasType(value);
            return value;
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
            return null;
        }
    }
}
