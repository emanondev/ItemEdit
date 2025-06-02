package emanondev.itemedit.aliases;

import java.util.Collection;
import java.util.List;

public interface IAliasSet<T> {

    String getID();

    void reload();

    /**
     * @return default name, for autogeneration
     */
    String getDefaultName(final T value);

    /**
     * @see #getDefaultName(Object)
     * @see #getPathName(Object)
     */
    @Deprecated
    String getName(final T value);

    /**
     * @return path for specified value (set path not included)
     */
    String getPathName(final T value);

    Collection<T> getValues();

    List<String> getAliases();

    T convertAlias(final String alias);
}
