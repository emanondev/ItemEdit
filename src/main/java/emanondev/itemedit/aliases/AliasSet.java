package emanondev.itemedit.aliases;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.YMLConfig;

public abstract class AliasSet<T> {

    private final String path;

    public AliasSet(String path) {
        this.path = path.toLowerCase();
    }

    public String getID() {
        return path;
    }

    private static final YMLConfig config = ItemEdit.get().getConfig("aliases.yml");

    private final HashMap<String, T> map = new HashMap<>();

    public void reload() {
        map.clear();
        for (T value : getValues()) {
            String path = this.path + "." + getName(value);
            String val = config.getString(path, null);
            boolean ok = true;
            if (val == null || val.isEmpty()) {
                val = getName(value);
                ok = false;
            }
            if (val == null) {
                new NullPointerException(value == null ? "null value"
                        : value + " of " + value.getClass().getSimpleName() + " has null name")
                        .printStackTrace();
                continue;
            }
            if (val.contains(" ") || !val.equals(val.toLowerCase())) {
                val = val.toLowerCase().replace(" ", "_");
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

    public abstract String getName(T value);

    public abstract Collection<T> getValues();

    protected void set(String alias, T obj) {
        if (obj == null || alias == null)
            throw new NullPointerException();
        if (alias.isEmpty())
            throw new IllegalArgumentException();
        alias = alias.replace(" ", "_").toLowerCase();

        String path = this.path + "." + getName(obj);
        if (alias.equals(config.get(path)))
            return;

        if (map.containsKey(alias))
            throw new IllegalArgumentException("Alias " + alias
                    + " is already used, check aliases.yml avoid using the same alias for different things");

        map.remove(config.get(path));
        map.put(alias, obj);

        config.set(path, alias);
        config.save();
    }

    public List<String> getAliases() {
        return new ArrayList<>(map.keySet());
    }

    public T convertAlias(String alias) {
        return map.get(alias.toLowerCase());
    }

}
