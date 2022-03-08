package emanondev.itemedit.aliases;

import java.util.Collection;
import java.util.EnumSet;

import org.bukkit.entity.EntityType;

public class EggTypeAliases extends EnumAliasSet<EntityType> {

    public EggTypeAliases() {
        super("mob_type", EntityType.class);
    }

    public Collection<EntityType> getValues() {
        EnumSet<EntityType> set = EnumSet.noneOf(EntityType.class);
        for (EntityType type : EntityType.values())
            if (type.isAlive() && type.isSpawnable())
                set.add(type);
        return set;
    }
}
