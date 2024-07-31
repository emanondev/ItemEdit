package emanondev.itemedit.aliases;

import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.Locale;

public class EnchAliasesOld extends EnchAliases {
    private final HashMap<String, String> enchNick = new HashMap<>();

    public EnchAliasesOld() {
        super();
        enchNick.put("PROTECTION_FIRE", "fire_protection");
        enchNick.put("DAMAGE_ALL", "sharpness");
        enchNick.put("ARROW_FIRE", "flame");
        enchNick.put("WATER_WORKER", "aqua_affinity");
        enchNick.put("ARROW_KNOCKBACK", "punch");
        enchNick.put("DEPTH_STRIDER", "depth_strider");
        enchNick.put("VANISHING_CURSE", "vanishing_curse");
        enchNick.put("DURABILITY", "unbreaking");
        enchNick.put("KNOCKBACK", "knockback");
        enchNick.put("LUCK", "luck_of_the_sea");
        enchNick.put("BINDING_CURSE", "binding_curse");
        enchNick.put("LOOT_BONUS_BLOCKS", "fortune");
        enchNick.put("PROTECTION_ENVIRONMENTAL", "protection");
        enchNick.put("DIG_SPEED", "efficiency");
        enchNick.put("MENDING", "mending");
        enchNick.put("FROST_WALKER", "frost_walker");
        enchNick.put("LURE", "lure");
        enchNick.put("LOOT_BONUS_MOBS", "looting");
        enchNick.put("PROTECTION_EXPLOSIONS", "blast_protection");
        enchNick.put("DAMAGE_UNDEAD", "smite");
        enchNick.put("FIRE_ASPECT", "fire_aspect");
        enchNick.put("SWEEPING_EDGE", "sweeping");
        enchNick.put("THORNS", "thorns");
        enchNick.put("DAMAGE_ARTHROPODS", "bane_of_arthropods");
        enchNick.put("OXYGEN", "respiration");
        enchNick.put("SILK_TOUCH", "silk_touch");
        enchNick.put("PROTECTION_PROJECTILE", "projectile_protection");
        enchNick.put("PROTECTION_FALL", "feather_falling");
        enchNick.put("ARROW_DAMAGE", "power");
        enchNick.put("ARROW_INFINITE", "infinity");
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getName(Enchantment ench) {
        if (enchNick.containsKey(ench.getName()))
            return enchNick.get(ench.getName());
        //Mohist compability
        try {
            return ench.getName().toLowerCase(Locale.ENGLISH);
        } catch (Exception e) {
            //grappling-hook support https://www.spigotmc.org/resources/55955/
            return ench.toString().toLowerCase(Locale.ENGLISH).replace(" ", "_");
        }
    }

}