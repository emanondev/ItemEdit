package emanondev.itemedit.aliases;

import java.util.*;

import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import emanondev.itemedit.ItemEdit;

public class Aliases {

	public static final EnchAliases ENCHANT = getEnchAliases();
	public static final AliasSet<PatternType> PATTERN_TYPE = new EnumAliasSet<PatternType>("banner_pattern",PatternType.class);
	public static final GenAliases BOOK_TYPE = getGenAliases();
	public static final AliasSet<PotionEffectType> POTION_EFFECT = new AliasSet<PotionEffectType>("potion_effect") {

		@Override
		public String getName(PotionEffectType type) {
			return type.getName().toLowerCase();
		}
		
		private final Collection<PotionEffectType> values = grabValues();
		
		private final Collection<PotionEffectType> grabValues() {
			HashSet<PotionEffectType> set = new HashSet<>();
			for (PotionEffectType val:PotionEffectType.values())
				if (val!=null)
					set.add(val);
			return set;
		}

		@Override
		public Collection<PotionEffectType> getValues() {
			return values;
		}

	};
	public static final AliasSet<DyeColor> COLOR = new EnumAliasSet<DyeColor>("color",DyeColor.class);
	public static final EggTypeAliases EGG_TYPE = getEggTypeAliases();
	public static final AliasSet<ItemFlag> FLAG_TYPE = new EnumAliasSet<ItemFlag>("flag_type",ItemFlag.class);
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
	public static final AliasSet<EquipmentSlot> EQUIPMENT_SLOTS = new EnumAliasSet<EquipmentSlot>("equip_slot",EquipmentSlot.class);
	public static final AttributeAliases ATTRIBUTE = getAttributeAliases();
	public static final OperationAliases OPERATIONS = getAttributeOperationAliases();
	public static final TropicalFishPatternAliases TROPICALPATTERN = getTropicalPatternAliases();
	
	private final static Map<String, AliasSet<?>> types = new HashMap<>();
	public static final EnumAliasSet<FireworkEffect.Type> FIREWORK_TYPE = new EnumAliasSet<FireworkEffect.Type>("firework_type",FireworkEffect.Type.class);
	public static final AxolotlVariantAliases AXOLOTL_VARIANT = getAxolotlVariantAliases();
	private static boolean loaded = false;

	
	private static final void loadTypesMap() {
		if (ENCHANT != null)
			types.put(ENCHANT.getID(), ENCHANT);
		if (PATTERN_TYPE != null)
			types.put(PATTERN_TYPE.getID(), PATTERN_TYPE);
		if (BOOK_TYPE != null)
			types.put(BOOK_TYPE.getID(), BOOK_TYPE);
		if (POTION_EFFECT != null)
			types.put(POTION_EFFECT.getID(), POTION_EFFECT);
		if (COLOR != null)
			types.put(COLOR.getID(), COLOR);
		if (EGG_TYPE != null)
			types.put(EGG_TYPE.getID(), EGG_TYPE);
		if (FLAG_TYPE != null)
			types.put(FLAG_TYPE.getID(), FLAG_TYPE);
		if (BOOLEAN != null)
			types.put(BOOLEAN.getID(), BOOLEAN);
		if (EQUIPMENT_SLOTS != null)
			types.put(EQUIPMENT_SLOTS.getID(), EQUIPMENT_SLOTS);
		if (FIREWORK_TYPE != null)
			types.put(FIREWORK_TYPE.getID(), FIREWORK_TYPE);
		if (ATTRIBUTE != null)
			types.put(ATTRIBUTE.getID(), ATTRIBUTE);
		if (OPERATIONS != null)
			types.put(OPERATIONS.getID(), OPERATIONS);
		if (TROPICALPATTERN != null)
			types.put(TROPICALPATTERN.getID(), TROPICALPATTERN);
		if (AXOLOTL_VARIANT != null)
			types.put(AXOLOTL_VARIANT.getID(), AXOLOTL_VARIANT);
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

	public static final Map<String,AliasSet<?>> getTypes(){
		return types;
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
		if (args[0].equals("v1") && Integer.parseInt(args[1])<17)
			return null;
		return new AxolotlVariantAliases();
	}
}
