package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class TrimMaterialAliases extends RegistryAliasSet<TrimMaterial> {
    public TrimMaterialAliases() {
        super("trim_material", ItemEdit.get(), Registry.TRIM_MATERIAL);
    }

}
