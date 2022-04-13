package emanondev.itemedit.aliases;

import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.EnumSet;

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
