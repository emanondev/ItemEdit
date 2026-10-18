package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.utility.TagContainer;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
public class Aliases {

    private static final Map<String, IAliasSet<?>> types = new HashMap<>();

    public static final AliasSet<ItemUseAnimation> ANIMATION;
    public static final AliasSet<Attribute> ATTRIBUTE;
    public static final EnumAliasSet<Axolotl.Variant> AXOLOTL_VARIANT;
    public static final EnumAliasSet<BookMeta.Generation> BOOK_TYPE;
    public static final AliasSet<Boolean> BOOLEAN;
    public static final EnumAliasSet<DyeColor> COLOR;
    public static final AliasSet<Enchantment> ENCHANT;
    public static final AliasSet<TagContainer<EntityType>> ENTITY_GROUPS;
    public static final AliasSet<EntityType> ENTITY_TYPE;
    public static final EquipmentSlotGroupAliases EQUIPMENT_SLOTGROUPS;
    public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS;
    public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE;
    public static final AliasSet<ItemFlag> FLAG_TYPE;
    public static final AliasSet<MusicInstrument> GOAT_HORN_SOUND;
    public static final AliasSet<AttributeModifier.Operation> OPERATIONS;
    public static final AliasSet<PatternType> PATTERN_TYPE;
    public static final AliasSet<PotionEffectType> POTION_EFFECT;
    public static final EnumAliasSet<ItemRarity> RARITY;
    public static final AliasSet<Sound> SOUND;
    public static final AliasSet<TrimMaterial> TRIM_MATERIAL;
    public static final AliasSet<TrimPattern> TRIM_PATTERN;
    public static final AliasSet<TropicalFish.Pattern> TROPICALPATTERN;

    static {
        ANIMATION = createAndRegister(() -> new EnumAliasSet<>("animation", ItemEdit.get(), ItemUseAnimation.class));
        ATTRIBUTE = createAndRegister(() -> new RegistryAliasSet<>("attribute", ItemEdit.get(), Registry.ATTRIBUTE));
        AXOLOTL_VARIANT = createAndRegister(() -> new EnumAliasSet<>("axolotl_variant", ItemEdit.get(), Axolotl.Variant.class));
        BOOK_TYPE = createAndRegister(() -> new EnumAliasSet<>("book_type", ItemEdit.get(), BookMeta.Generation.class));
        BOOLEAN = createAndRegister(() -> new AliasSet<Boolean>("boolean", ItemEdit.get()) {
            @Override
            public String getName(Boolean value) {
                return value.toString();
            }

            @Override
            public Collection<Boolean> getValues() {
                return List.of(Boolean.FALSE, Boolean.TRUE);
            }
        });
        COLOR = createAndRegister(() -> new EnumAliasSet<>(ItemEdit.get(), DyeColor.class));
        ENCHANT = createAndRegister(() -> new RegistryAliasSet<>("enchant", ItemEdit.get(), Registry.ENCHANTMENT)); //TODO deprecated code
        ENTITY_GROUPS = createAndRegister(() -> new TagAliasSet<>("entitygroups", ItemEdit.get(), EntityType.class, Tag.REGISTRY_ENTITY_TYPES));
        ENTITY_TYPE = createAndRegister(() -> new EnumAliasSet<>(ItemEdit.get(), EntityType.class));
        EQUIPMENT_SLOTGROUPS = createAndRegister(EquipmentSlotGroupAliases::new);
        EQUIPMENT_SLOTS = createAndRegister(() -> new EnumAliasSet<>("equip_slot", ItemEdit.get(), EquipmentSlot.class));
        FIREWORK_TYPE = createAndRegister(() -> new EnumAliasSet<>("firework_type", ItemEdit.get(), FireworkEffect.Type.class));
        FLAG_TYPE = createAndRegister(() -> new EnumAliasSet<>("flag_type", ItemEdit.get(), ItemFlag.class));
        GOAT_HORN_SOUND = createAndRegister(() -> new RegistryAliasSet<>("goat_horn_sound", ItemEdit.get(), Registry.INSTRUMENT));
        OPERATIONS = createAndRegister(() -> new EnumAliasSet<>(ItemEdit.get(), AttributeModifier.Operation.class));
        PATTERN_TYPE = createAndRegister(BannerPatternAliases::new);
        POTION_EFFECT = createAndRegister(() -> new RegistryAliasSet<>("potion_effect", ItemEdit.get(), Registry.EFFECT));
        RARITY = createAndRegister(() -> new EnumAliasSet<>(ItemEdit.get(), ItemRarity.class));
        SOUND = createAndRegister(() -> new RegistryAliasSet<>("sound", ItemEdit.get(), Registry.SOUNDS));
        TRIM_MATERIAL = createAndRegister(() -> new RegistryAliasSet<>("trim_material", ItemEdit.get(), Registry.TRIM_MATERIAL));
        TRIM_PATTERN = createAndRegister(() -> new RegistryAliasSet<>("trim_pattern", ItemEdit.get(), Registry.TRIM_PATTERN));
        TROPICALPATTERN = createAndRegister(() -> new EnumAliasSet<>("tropical_fish_pattern", ItemEdit.get(), TropicalFish.Pattern.class));
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
