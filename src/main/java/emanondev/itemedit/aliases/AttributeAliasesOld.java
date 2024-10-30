package emanondev.itemedit.aliases;

import org.bukkit.attribute.Attribute;

import java.util.Locale;

public class AttributeAliasesOld extends EnumAliasSet<Attribute> implements AttributeAliases {

    public AttributeAliasesOld() {
        super(Attribute.class);
    }

    @Override
    public String getName(Attribute type) {
        String name = type.name().toLowerCase(Locale.ENGLISH);
        if (name.startsWith("generic_"))
            name = name.substring("generic_".length());
        return name;
    }
}