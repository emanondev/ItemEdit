package emanondev.itemedit.aliases;

import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TrimMaterialAliases extends RegistryAliasSet<TrimMaterial> {
    public TrimMaterialAliases() {
        super("trim_material", Registry.TRIM_MATERIAL);
    }

}
