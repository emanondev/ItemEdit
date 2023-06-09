package emanondev.itemedit.aliases;

import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TrimMaterialAliases extends AliasSet<TrimMaterial> {
    public TrimMaterialAliases() {
        super("trim_material");
        registerValue(TrimMaterial.AMETHYST);
        registerValue(TrimMaterial.COPPER);
        registerValue(TrimMaterial.DIAMOND);
        registerValue(TrimMaterial.EMERALD);
        registerValue(TrimMaterial.GOLD);
        registerValue(TrimMaterial.IRON);
        registerValue(TrimMaterial.LAPIS);
        registerValue(TrimMaterial.NETHERITE);
        registerValue(TrimMaterial.QUARTZ);
        registerValue(TrimMaterial.REDSTONE);
    }

    @Override
    public String getName(TrimMaterial value) {
        return value.getKey().toString();
    }

    public void registerValue(TrimMaterial pattern){
        values.add(pattern);
    }

    private final HashSet<TrimMaterial> values = new HashSet<>();

    @Override
    public Collection<TrimMaterial> getValues() {
        return Collections.unmodifiableCollection(values);
    }
}
