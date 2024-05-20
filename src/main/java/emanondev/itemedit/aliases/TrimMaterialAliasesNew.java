package emanondev.itemedit.aliases;

import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class TrimMaterialAliasesNew extends RegistryAliasSet<TrimMaterial> implements TrimMaterialAliases {
    public TrimMaterialAliasesNew() {
        super("trim_material", Registry.TRIM_MATERIAL);
    }

}
