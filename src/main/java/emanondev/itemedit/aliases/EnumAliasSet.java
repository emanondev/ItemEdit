package emanondev.itemedit.aliases;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

public class EnumAliasSet<T extends Enum<T>> extends AliasSet<T> {

    private final Class<T> clazz;

    public EnumAliasSet(final Class<T> clazz) {
        this(clazz.getSimpleName().toLowerCase(Locale.ENGLISH), clazz);
    }

    public EnumAliasSet(final String path, final Class<T> clazz) {
        super(path);
        this.clazz = clazz;
    }

    @Override
    public String getName(final T type) {
        return type.name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public Collection<T> getValues() {
        return EnumSet.allOf(clazz);
    }
}
