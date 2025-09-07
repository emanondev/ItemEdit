package emanondev.itemedit.aliases;

import emanondev.itemedit.APlugin;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

public class EnumAliasSet<T extends Enum<T>> extends AliasSet<T> {

    private final Class<T> clazz;

    @Deprecated
    public EnumAliasSet(Class<T> clazz) {
        this(clazz.getSimpleName().toLowerCase(Locale.ENGLISH), clazz);
    }

    @Deprecated
    public EnumAliasSet(String path, Class<T> clazz) {
        super(path);
        this.clazz = clazz;
    }

    public EnumAliasSet(APlugin plugin, Class<T> clazz) {
        this(clazz.getSimpleName().toLowerCase(Locale.ENGLISH), plugin, clazz);
    }

    public EnumAliasSet(String path, APlugin plugin, Class<T> clazz) {
        super(path, plugin);
        this.clazz = clazz;
    }

    @Override
    public String getName(T type) {
        return type.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Collection<T> getValues() {
        return EnumSet.allOf(clazz);
    }
}
