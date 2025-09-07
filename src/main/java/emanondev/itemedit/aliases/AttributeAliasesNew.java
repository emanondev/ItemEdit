package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

public class AttributeAliasesNew extends RegistryAliasSet<Attribute> implements AttributeAliases {
    public AttributeAliasesNew() {
        super("attribute", ItemEdit.get(), Registry.ATTRIBUTE);
    }

    @Override
    public String getName(Attribute type) {
        String name = super.getName(type);
        if (name.startsWith("generic_")) {
            name = name.substring("generic_".length());
        }
        return name;
    }
}