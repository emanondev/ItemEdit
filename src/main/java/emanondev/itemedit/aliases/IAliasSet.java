package emanondev.itemedit.aliases;

import java.util.Collection;
import java.util.List;

public interface IAliasSet<T> {

    String getID();

    void reload();

    /**
     * @return default name, for autogeneration
     */
    String getDefaultName(T value);

    /**
     * @see #getDefaultName(Object)
     * @see #getPathName(Object)
     */
    @Deprecated
    String getName(T value);

    /**
     *
     * @return path for specified value (set path not included)
     */
    String getPathName(T value);

    Collection<T> getValues();

    List<String> getAliases();

    T convertAlias(String alias);
}
