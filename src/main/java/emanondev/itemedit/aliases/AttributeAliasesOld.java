package emanondev.itemedit.aliases;

import org.bukkit.attribute.Attribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

public class AttributeAliasesOld extends AliasSet<Attribute> implements AttributeAliases {

    public AttributeAliasesOld() {
        super("attribute");
    }

    @Override
    public Collection<Attribute> getValues() {
        return Arrays.asList(Attribute.class.getEnumConstants());
    }

    @Override
    public String getName(final Attribute type) {
        String name = ((Enum<?>) type).name().toLowerCase(Locale.ENGLISH);
        if (name.startsWith("generic_")) {
            name = name.substring("generic_".length());
        }
        return name;
    }
}