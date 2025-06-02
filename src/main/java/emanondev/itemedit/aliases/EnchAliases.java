package emanondev.itemedit.aliases;

import org.bukkit.enchantments.Enchantment;

import java.util.Collection;
import java.util.HashSet;

public class EnchAliases extends AliasSet<Enchantment> {

    public EnchAliases() {
        super("enchant");
    }

    @Override
    public String getName(final Enchantment ench) {
        return ench.getKey().getKey();
    }

    @Override
    public Collection<Enchantment> getValues() {
        HashSet<Enchantment> set = new HashSet<>();
        for (Enchantment ench : Enchantment.values()) {
            if (ench != null) {
                set.add(ench);
            }
        }
        return set;
    }
}
