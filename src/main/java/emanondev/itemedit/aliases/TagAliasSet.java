package emanondev.itemedit.aliases;

import emanondev.itemedit.utility.TagContainer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
public class TagAliasSet<T extends Keyed> extends AliasSet<TagContainer<T>> {

    private final Class<T> clazz;
    private final String registry;

    public TagAliasSet(@NotNull String path, @NotNull Class<T> clazz, @NotNull String registry) {
        super(path);
        this.clazz = clazz;
        this.registry = registry;
    }

    @Override
    public String getPathName(TagContainer<T> value) {
        return (value.getTag().getKey().getNamespace().equals(NamespacedKey.MINECRAFT) ?
                value.getTag().getKey().getKey() : value.getTag().getKey().toString()).replace(".", "_");
    }

    @Override
    public String getDefaultName(TagContainer<T> value) {
        return value.getTag().getKey().getKey();
    }

    @Deprecated
    public String getName(TagContainer<T> value) {
        return (value.getTag().getKey().getNamespace().equals(NamespacedKey.MINECRAFT) ?
                value.getTag().getKey().getKey() : value.getTag().getKey().toString()).replace(".", "_");
    }


    @Override
    public Collection<TagContainer<T>> getValues() {
        Set<TagContainer<T>> set = new HashSet<>();
        for (Tag<T> tag : Bukkit.getTags(registry, clazz))
            set.add(new TagContainer<>(tag));
        return set;
    }
}
