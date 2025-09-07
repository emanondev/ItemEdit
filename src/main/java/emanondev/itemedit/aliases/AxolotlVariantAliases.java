package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.entity.Axolotl.Variant;

public class AxolotlVariantAliases extends EnumAliasSet<Variant> {

    public AxolotlVariantAliases() {
        super("axolotl_variant", ItemEdit.get(), Variant.class);
    }
}