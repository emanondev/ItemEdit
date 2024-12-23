package emanondev.itemedit.aliases;

import org.bukkit.attribute.Attribute;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

public class AttributeAliasesOld extends AliasSet<Attribute> implements AttributeAliases {

    public AttributeAliasesOld() {
        super("attribute");
    }

    @Override
    public Collection<Attribute> getValues() {
        return Arrays.asList(Attribute.values());
    }

    @Override
    public String getName(Attribute type) {
        String name = type.name().toLowerCase(Locale.ENGLISH);
        if (name.startsWith("generic_"))
            name = name.substring("generic_".length());
        return name;
    }
}