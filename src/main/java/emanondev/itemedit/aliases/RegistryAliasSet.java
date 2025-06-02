package emanondev.itemedit.aliases;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.Collection;
import java.util.stream.Collectors;

//since 1.20.1
public class RegistryAliasSet<T extends Keyed> extends AliasSet<T> {

    private final Registry<T> registry;

    public RegistryAliasSet(final String path, final Registry<T> registry) {
        super(path);
        this.registry = registry;
    }

    @Override
    public String getName(final T type) {
        return (type.getKey().getNamespace().equals(NamespacedKey.MINECRAFT) ?
                type.getKey().getKey() : type.getKey().toString()).replace(".", "_");
    }

    @Override
    public Collection<T> getValues() {
        return registry.stream().collect(Collectors.toList());
    }

}
