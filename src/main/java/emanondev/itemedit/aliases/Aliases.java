package emanondev.itemedit.aliases;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import emanondev.itemedit.ItemEdit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Aliases {

    public static final EnchAliases ENCHANT = getEnchAliases();
    public static final AliasSet<PatternType> PATTERN_TYPE = new EnumAliasSet<>("banner_pattern", PatternType.class);
    public static final GenAliases BOOK_TYPE = getGenAliases();
    public static final AliasSet<PotionEffectType> POTION_EFFECT = new AliasSet<PotionEffectType>("potion_effect") {

        @Override
        public String getName(PotionEffectType type) {
            return type.getName().toLowerCase();
        }

        private final Collection<PotionEffectType> values = grabValues();

        private Collection<PotionEffectType> grabValues() {
            HashSet<PotionEffectType> set = new HashSet<>();
            for (PotionEffectType val : PotionEffectType.values())
                if (val != null)
                    set.add(val);
            return set;
        }

        @Override
        public Collection<PotionEffectType> getValues() {
            return values;
        }

    };
    public static final AliasSet<DyeColor> COLOR = new EnumAliasSet<>("color", DyeColor.class);
    public static final EggTypeAliases EGG_TYPE = getEggTypeAliases();
    public static final AliasSet<ItemFlag> FLAG_TYPE = new EnumAliasSet<>("flag_type", ItemFlag.class);
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
    public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS = new EnumAliasSet<>("equip_slot", EquipmentSlot.class);
    public static final AttributeAliases ATTRIBUTE = getAttributeAliases();
    public static final OperationAliases OPERATIONS = getAttributeOperationAliases();
    public static final TropicalFishPatternAliases TROPICALPATTERN = getTropicalPatternAliases();

    private final static Map<String, AliasSet<?>> types = new HashMap<>();
    public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE = new EnumAliasSet<>("firework_type", FireworkEffect.Type.class);
    public static final AxolotlVariantAliases AXOLOTL_VARIANT = getAxolotlVariantAliases();
    private static boolean loaded = false;


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
    }

    public static <T> void registerAliasType(@Nullable AliasSet<T> set){
        if (set==null)
            return;
        if (types.containsKey(set.getID()))
            throw new IllegalArgumentException("Duplicate id");
        types.put(set.getID(),set);
    }

    public static AliasSet<?> getAliasType(@NotNull String id){
        return types.get(id);
    }

    public static void reload() {
        if (!loaded) {
            loaded = true;
            loadTypesMap();
        }
        for (AliasSet<?> set : types.values()) {
            set.reload();
        }
    }

    public static Map<String, AliasSet<?>> getTypes() {
        return Collections.unmodifiableMap(types);
    }

    private static EggTypeAliases getEggTypeAliases() {
        if (!ItemEdit.NMS_VERSION.startsWith("v1_11_R") && !ItemEdit.NMS_VERSION.startsWith("v1_12_R"))
            return null;
        return new EggTypeAliases();
    }

    private static TropicalFishPatternAliases getTropicalPatternAliases() {
        if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_12_R"))
            return null;
        return new TropicalFishPatternAliases();
    }

    private static GenAliases getGenAliases() {
        if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R"))
            return null;
        return new GenAliases();
    }

    @NotNull
    private static EnchAliases getEnchAliases() {
        if (ItemEdit.NMS_VERSION.startsWith("v1_8_") || ItemEdit.NMS_VERSION.startsWith("v1_9_")
                || ItemEdit.NMS_VERSION.startsWith("v1_10") || ItemEdit.NMS_VERSION.startsWith("v1_11")
                || ItemEdit.NMS_VERSION.startsWith("v1_12")) {
            return new EnchAliasesOld();
        }
        return new EnchAliases();
    }

    private static AttributeAliases getAttributeAliases() {
        if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R"))
            return null;
        return new AttributeAliases();
    }

    private static OperationAliases getAttributeOperationAliases() {
        if (ItemEdit.NMS_VERSION.startsWith("v1_8_R") || ItemEdit.NMS_VERSION.startsWith("v1_9_R")
                || ItemEdit.NMS_VERSION.startsWith("v1_10_R") || ItemEdit.NMS_VERSION.startsWith("v1_11_R"))
            return null;
        return new OperationAliases();
    }

    private static AxolotlVariantAliases getAxolotlVariantAliases() {
        String[] args = ItemEdit.NMS_VERSION.split("_");
        if (args[0].equals("v1") && Integer.parseInt(args[1]) < 17)
            return null;
        return new AxolotlVariantAliases();
    }
}
