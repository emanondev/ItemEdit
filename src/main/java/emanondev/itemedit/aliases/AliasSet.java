package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;

import java.util.*;

public abstract class AliasSet<T> implements IAliasSet<T> {

    private static final YMLConfig config = ItemEdit.get().getConfig("aliases.yml");
    private final String path;
    private final HashMap<String, T> map = new HashMap<>();

    public AliasSet(String path) {
        this.path = path.toLowerCase(Locale.ENGLISH);
    }

    public String getID() {
        return path;
    }

    public void reload() {
        map.clear();
        for (T value : getValues()) {
            String path = this.path + "." + getPathName(value);
            String val = config.getString(path, null);
            boolean ok = true;
            if (val == null || val.isEmpty()) {
                val = getDefaultName(value);
                ok = false;
            }
            if (val == null) {
                new NullPointerException(value == null ? "null value"
                        : value + " of " + value.getClass().getSimpleName() + " has null name")
                        .printStackTrace();
                continue;
            }
            if (val.contains(" ") || !val.equals(val.toLowerCase(Locale.ENGLISH))) {
                val = val.toLowerCase(Locale.ENGLISH).replace(" ", "_");
                ok = false;
            }
            if (map.containsKey(val)) {
                int index = 1;
                while (map.containsKey(val + index)) {
                    index++;
                }
                val = val + index;
                ok = false;
            }
            map.put(val, value);
            if (!ok) {
                config.set(path, val);
                config.save();
            }
        }
    }

    /**
     * @return default name, for autogeneration
     */
    public String getDefaultName(T value) {
        return getPathName(value);
    }

    /**
     * @see #getDefaultName(Object)
     * @see #getPathName(Object)
     */
    @Deprecated
    public abstract String getName(T value);

    /**
     * @return path for specified value (set path not included)
     */
    public String getPathName(T value) {
        return getName(value);
    }

    public abstract Collection<T> getValues();

    protected void set(String alias, T obj) {
        if (obj == null || alias == null)
            throw new NullPointerException();
        if (alias.isEmpty())
            throw new IllegalArgumentException();
        alias = alias.replace(" ", "_").toLowerCase(Locale.ENGLISH);

        String path = this.path + "." + getPathName(obj);
        if (alias.equals(config.get(path)))
            return;

        if (map.containsKey(alias))
            throw new IllegalArgumentException("Alias " + alias
                    + " is already used, check aliases.yml avoid using the same alias for different things");

        map.remove(config.get(path)); //TODO to fix
        map.put(alias, obj);

        config.set(path, alias);
        config.save();
    }

    public List<String> getAliases() {
        return new ArrayList<>(map.keySet());
    }

    public T convertAlias(String alias) {
        return map.get(alias.toLowerCase(Locale.ENGLISH));
    }

}
