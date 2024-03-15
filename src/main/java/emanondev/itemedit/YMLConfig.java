package emanondev.itemedit;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YMLConfig extends YamlConfiguration {
    private final JavaPlugin plugin;
    private final File file;
    private final String name;

    /**
     * Returns the file path of this config.
     *
     * @return the file path starting by
     * {@link #getPlugin()}.{@link JavaPlugin#getDataFolder()
     * getDataFolder()}.
     */
    public String getFileName() {
        return name;
    }

    /**
     * Return the plugin associated with this Config.
     *
     * @return the plugin associated with this Config
     */
    public @NotNull JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Constructs a new Configuration File.<br>
     * Used file is located at plugin.{@link JavaPlugin#getDataFolder()
     * getDataFolder()}/{@link YMLConfig#fixName(String) YMLConfig.fixName(name)}.<br>
     * If the plugin jar has a file on name path, that file is used to generate the
     * config file.
     *
     * @param plugin associated plugin to grab the folder
     * @param name   raw name of the file sub path
     * @throws NullPointerException     if name is null
     * @throws IllegalArgumentException if name is empty
     */
    public YMLConfig(@NotNull JavaPlugin plugin, @NotNull String name) {
        this.plugin = plugin;
        name = fixName(name);
        this.name = name;
        this.file = new File(plugin.getDataFolder(), name);
        reload();
    }

    /**
     * Fix the given name for a yaml file name
     *
     * @param name the raw name of the file with or without .yml
     * @return fixed name
     * @throws NullPointerException     if name is null
     * @throws IllegalArgumentException if name is empty
     */
    public static String fixName(@NotNull String name) {
        if(name.isEmpty())
            throw new IllegalArgumentException("YAML file must have a name!");
        if (!name.endsWith(".yml"))
            name += ".yml";
        return name;
    }

    /**
     * Reload config object in RAM to that of the file.<br>
     * Lose any unsaved changes.<br>
     *
     * @return true if file existed
     */
    public boolean reload() {
        boolean existed = file.exists();
        if (!file.exists()) {
            if (!file.getParentFile().exists())  // Create parent folders if they don't exist
                if (!file.getParentFile().mkdirs())
                    new Exception("unable to create parent folder").printStackTrace();

            if (plugin.getResource(name.replace('\\', '/')) != null) {
                plugin.saveResource(name, true); // Save the one from the JAR if possible
            } else
                try {
                    if (!file.createNewFile())
                        new Exception("unable to create file").printStackTrace();
                } // Create a blank file if there's not one to copy from the JAR
                catch (IOException e) {
                    e.printStackTrace();
                }
        }
        try {
            this.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream resource = plugin.getResource(name.replace('\\', '/'));
        if (resource != null)
            // Set up defaults in case their config is broken.
            this.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(resource, StandardCharsets.UTF_8)));

        return existed;
    }

    /**
     * Save the config object in RAM to the file.<br>
     * Overwrites any changes that the configurator has made to the file unless
     * {@link #reload()} has been called since.
     */
    public void save() {
        try {
            this.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the file of the config.
     *
     * @return the file associated to the config
     */
    public @NotNull File getFile() {
        return file;
    }

    /**
     * Gets a set of sub keys at path.
     *
     * @param path yaml path on file
     * @return sub-keys at selected path
     */
    public @NotNull Set<String> getKeys(@NotNull String path) {
        ConfigurationSection section = this.getConfigurationSection(path);
        if (section == null)
            return new LinkedHashSet<>();
        else
            return section.getKeys(false);
    }

    /**
     * Gets the object from the config or set the default.<br>
     * Get the object from path, if the object is null or of different class default
     * value is returned and saved on disk else return the object
     *
     * @param path  yaml path on file
     * @param def   default value
     * @param clazz value class
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _ -> !null")
    public <T> T load(String path, T def, Class<T> clazz) {
        Object value = get(path, null);
        if (value == null) {
            value = this.getDefaults() == null ? null : this.getDefaults().get(path);
            if (value == null) {
                if (def == null)
                    return null;
                else {
                    set(path, def);
                    save();
                    return def;
                }
            }
            if (!clazz.isInstance(value)) {
                set(path, def);
                save();
                return def;
            }
            set(path, value);
            save();
            return (T) value;
        }
        if (!clazz.isInstance(value)) {
            set(path, def);
            save();
            return def;
        }
        return (T) value;
    }

    /**
     * Gets the object from the config or set the default.<br>
     * Get the object from path, if null or of another class default is returned
     *
     * @param path  yaml path on file
     * @param def   default value
     * @param clazz value class
     * @return object or default
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _ -> !null")
    public <T> T get(String path, T def, Class<T> clazz) {
        Object value = get(path);
        if (value == null) {
            return def;
        }
        if (!clazz.isInstance(value)) {
            return def;
        }
        return (T) value;
    }

    @Contract("_, !null -> !null")
    public @Nullable Double loadDouble(@NotNull String path, @Nullable Double def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.doubleValue();
    }

    @Contract("_, !null -> !null")
    public @Nullable Boolean loadBoolean(@NotNull String path, @Nullable Boolean def) {
        return load(path, def, Boolean.class);
    }

    /**
     * Gets the value from the config
     * Set default if no value is set.
     * Use {@link #loadInteger(String, Integer)}
     *
     * @param path yaml path on file
     * @param def  default value
     * @return Gets the value from the config<br>
     */
    @Deprecated
    public int loadInt(@NotNull String path, @Nullable Integer def) {
        Number val = load(path, def, Number.class);
        return val == null ? 0 : val.intValue();
    }

    @Contract("_, !null -> !null")
    public @Nullable Integer loadInteger(@NotNull String path, @Nullable Integer def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.intValue();
    }

    @Contract("_, !null -> !null")
    public @Nullable Integer getInteger(@NotNull String path, @Nullable Integer def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.intValue();
    }

    @Contract("_, !null -> !null")
    public @Nullable Long loadLong(@NotNull String path, @Nullable Long def) {
        Number val = load(path, def, Number.class);
        return val == null ? null : val.longValue();
    }

    /**
     * Load String value.<br>
     * target = null
     *
     * @param path  yaml path on file
     * @param def   default value
     * @param color convert colors?
     * @param args  holders and replacer
     * @return the value found or default if none
     * @see #loadMessage(String, String, Player, boolean, String...) loadString(path,
     * def, null, color, args)
     * @see #load(String, Object, Class)
     */
    @Contract("_, !null, _, _ -> !null")
    public @Nullable String loadMessage(@NotNull String path, @Nullable String def, boolean color, String... args) {
        return loadMessage(path, def, null, color, args);
    }

    /**
     * Load String value.<br>
     * target = null
     *
     * @param path yaml path on file
     * @param def  default value
     * @param args holders and replacer
     * @return the value found or default if none
     * @see #loadMessage(String, String, Player, boolean, String...) loadString(path,
     * def, null, color, args)
     * @see #load(String, Object, Class)
     */
    @Contract("_, !null, _ -> !null")
    public @Nullable String loadMessage(@NotNull String path, @Nullable String def, String... args) {
        return loadMessage(path, def, null, true, args);
    }

    /**
     * Load String value.<br>
     *
     * @param path   yaml path on file
     * @param def    default value
     * @param target target user for papi support
     * @param color  convert colors?
     * @param args   holders and replacer
     * @return the value found or default if none
     * @see #load(String, Object, Class)
     */
    @Contract("_, !null, _, _, _ -> !null")
    public @Nullable String loadMessage(@NotNull String path, @Nullable String def, @Nullable Player target,
                                        boolean color, String... args) {
        if (args.length > 0) {
            if (Util.isVersionAfter(1, 18, 1)) {
                if (getComments(path).isEmpty()) {
                    if (this.contains(path + "_HOLDERS"))
                        this.set(path + "_HOLDERS", null);

                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < args.length; i += 2)
                        build.append(args[i]).append(" ");
                    this.setComments(path, Collections.singletonList(build.substring(0, build.length() - 1)));
                }
            }
        }
        return UtilsString.fix(load(path, def, String.class), target, color, args);
    }

    /**
     * Get String value.<br>
     *
     * @param path   yaml path on file
     * @param def    default value
     * @param target target user for papi support
     * @param color  convert colors?
     * @param args   holders and replacer
     * @return the value found or default if none
     * @see #get(String, Object, Class)
     */
    @Contract("_, !null, _, _, _ -> !null")
    public @Nullable String getMessage(@NotNull String path, @Nullable String def, @Nullable Player target,
                                       boolean color, String... args) {
        if (args.length > 0) {
            if (Util.isVersionAfter(1, 18, 1)) {
                if (getComments(path).isEmpty()) {
                    if (this.contains(path + "_HOLDERS"))
                        this.set(path + "_HOLDERS", null);

                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < args.length; i += 2)
                        build.append(args[i]).append(" ");
                    this.setComments(path, Collections.singletonList(build.substring(0, build.length() - 1)));
                }
            }
        }
        return UtilsString.fix(get(path, def, String.class), target, color, args);
    }

    /**
     * Get String value.<br>
     * target = null
     *
     * @param path  yaml path on file
     * @param def   default value
     * @param color convert colors?
     * @param args  holders and replacer
     * @return the value found or default if none
     * @see #getMessage(String, String, Player, boolean, String...) getString(path,
     * def, null, color, args)
     * @see #get(String, Object, Class)
     */
    @Contract("_, !null, _, _ -> !null")
    public @Nullable String getMessage(@NotNull String path, @Nullable String def, boolean color, String... args) {
        return this.getMessage(path, def, null, color, args);
    }

    /**
     * Get String value.<br>
     * target = null
     *
     * @param path yaml path on file
     * @param def  default value
     * @param args holders and replacer
     * @return the value found or default if none
     * @see #getMessage(String, String, Player, boolean, String...) getString(path,
     * def, null, color, args)
     * @see #get(String, Object, Class)
     */
    @Contract("_, !null, _ -> !null")
    public @Nullable String getMessage(@NotNull String path, @Nullable String def, String... args) {
        return this.getMessage(path, def, null, true, args);
    }

    /**
     * target = null
     * color = true
     *
     * @param path    yaml path on file
     * @param def     default value
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @Contract("_, !null, _ -> !null")
    public @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def, String... holders) {
        return loadMultiMessage(path, def, null, true, holders);
    }

    /**
     * target = null
     *
     * @param path    yaml path on file
     * @param def     default value
     * @param color   convert colors?
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @Contract("_, !null, _, _ -> !null")
    public @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def, boolean color, String... holders) {
        return loadMultiMessage(path, def, null, color, holders);
    }

    /**
     * @param path    yaml path on file
     * @param def     default value
     * @param target  target user for papi support
     * @param color   convert colors?
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _, _, _ -> !null")
    public @Nullable List<String> loadMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                   @Nullable Player target, boolean color, String... holders) {
        if (holders.length > 0) {
            if (Util.isVersionAfter(1, 18, 1)) {
                if (getComments(path).isEmpty()) {
                    if (this.contains(path + "_HOLDERS"))
                        this.set(path + "_HOLDERS", null);

                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < holders.length; i += 2)
                        build.append(holders[i]).append(" ");
                    this.setComments(path, Collections.singletonList(build.substring(0, build.length() - 1)));
                }
            }
        }
        try {
            return UtilsString.fix(load(path, def, List.class), target, color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return UtilsString.fix(def, target, color, holders);
        }
    }

    /**
     * target = null
     *
     * @param path    yaml path on file
     * @param def     default value
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @Contract("_, !null, _ -> !null")
    public @Nullable List<String> getMultiMessage(@NotNull String path, @Nullable List<String> def, String... holders) {
        return getMultiMessage(path, def, null, true, holders);
    }

    /**
     * target = null
     *
     * @param path    yaml path on file
     * @param def     default value
     * @param color   convert colors?
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @Contract("_, !null, _, _ -> !null")
    public @Nullable List<String> getMultiMessage(@NotNull String path, @Nullable List<String> def, boolean color, String... holders) {
        return getMultiMessage(path, def, null, color, holders);
    }

    /**
     * @param path    yaml path on file
     * @param def     default value
     * @param target  target user for papi support
     * @param color   convert colors?
     * @param holders holders and replacer
     * @return the value found or default if none
     */
    @SuppressWarnings("unchecked")
    @Contract("_, !null, _, _, _ -> !null")
    public @Nullable List<String> getMultiMessage(@NotNull String path, @Nullable List<String> def,
                                                  @Nullable Player target, boolean color, String... holders) {
        if (holders.length > 0) {
            if (Util.isVersionAfter(1, 18, 1)) {
                if (getComments(path).isEmpty()) {
                    if (this.contains(path + "_HOLDERS"))
                        this.set(path + "_HOLDERS", null);

                    StringBuilder build = new StringBuilder();
                    for (int i = 0; i < holders.length; i += 2)
                        build.append(holders[i]).append(" ");
                    this.setComments(path, Collections.singletonList(build.substring(0, build.length() - 1)));
                }
            }
        }
        try {
            return UtilsString.fix(get(path, def, List.class), target, color, holders);
        } catch (Exception e) {
            e.printStackTrace();
            return UtilsString.fix(def, target, color, holders);
        }
    }

    @Contract("_, !null -> !null")
    public @Nullable ItemStack loadItemStack(@NotNull String path, @Nullable ItemStack def) {
        return load(path, def, ItemStack.class);
    }

    /**
     * assumes that enum constants are all uppercase
     *
     * @param <T>   class of enum
     * @param path  yaml path on file
     * @param clazz class of enum
     * @param def   default value
     * @return if path lead to a string attempts return the matching Enum value, if
     * the string is empty return def
     */
    @Contract("_, !null, _ -> !null")
    public @Nullable <T extends Enum<T>> T loadEnum(@NotNull String path, @Nullable T def, @NotNull Class<T> clazz) {
        return stringToEnum(loadMessage(path, def == null ? null : def.name(), false), def, clazz, path);
    }

    @Contract("_, !null, _ -> !null")
    public @Nullable <T extends Enum<T>> T getEnum(@NotNull String path, @Nullable T def, @NotNull Class<T> clazz) {
        return stringToEnum(getString(path, def == null ? null : def.name()), def, clazz, path);
    }

    @Contract("_, !null, _, _ -> !null")
    private @Nullable <T extends Enum<T>> T stringToEnum(@Nullable String value, @Nullable T def,
                                                         @NotNull Class<T> clazz, @NotNull String errorPath) {
        try {
            if (value == null || value.isEmpty())
                return def;
            return Enum.valueOf(clazz, value);
        } catch (IllegalArgumentException e) {
            try {
                return Enum.valueOf(clazz, value.toUpperCase());
            } catch (IllegalArgumentException e2) {
                e2.printStackTrace();
                new IllegalArgumentException(getError(errorPath) + "; can't find value for '" + value + "' from enum '"
                        + clazz.getName() + "' using default").printStackTrace();
            }
        }
        return def;
    }

    /**
     * assumes that enum constants are all uppercase null enum values contained in
     * def might be lost
     *
     * @param <T>   class of enum
     * @param path  yaml path on file
     * @param clazz class of enum
     * @param def   default value
     * @return the value found or default if none
     */
    public @NotNull <T extends Enum<T>> List<T> loadEnumList(@NotNull String path, @Nullable Collection<T> def,
                                                             @NotNull Class<T> clazz) {
        return stringListToEnumCollection(new ArrayList<>(),
                loadMultiMessage(path, enumCollectionToStringList(def), false), clazz, path);
    }

    public @NotNull <T extends Enum<T>> EnumSet<T> loadEnumSet(@NotNull String path, @Nullable Collection<T> def,
                                                               @NotNull Class<T> clazz) {
        return stringListToEnumCollection(EnumSet.noneOf(clazz),
                loadMultiMessage(path, enumCollectionToStringList(def), false), clazz, path);
    }

    @Contract("!null -> !null; null -> null")
    private <T extends Enum<T>> ArrayList<String> enumCollectionToStringList(@Nullable Collection<T> enums) {
        if (enums == null)
            return null;
        ArrayList<String> list = new ArrayList<>();
        for (T enumValue : enums)
            list.add(enumValue.name());
        return list;
    }

    private <T extends Enum<T>, K extends Collection<T>> K stringListToEnumCollection(K destination,
                                                                                      Collection<String> from, Class<T> clazz, String errPath) {
        if (from == null || from.isEmpty())
            return destination;
        for (String value : from) {
            T val = stringToEnum(value, null, clazz, errPath);
            if (val != null)
                destination.add(val);
        }
        return destination;
    }

    @SuppressWarnings("unchecked")
    @Contract("_, !null -> !null")
    public @Nullable <T> Map<String, T> loadMap(@NotNull String path, @Nullable Map<String, T> def) {
        try {
            if (!this.contains(path)) {
                this.set(path, def);
                save();
                return def;
            }

            Map<String, Object> subMap = ((ConfigurationSection) this.get(path)).getValues(true);
            try {
                return (Map<String, T>) subMap;
            } catch (Exception e) {
            }

            Map<String, T> result = new LinkedHashMap<>();
            for (String key : subMap.keySet()) {
                try {
                    result.put(key, (T) subMap.get(key));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return def;
        }
    }

    @Contract("_, !null -> !null")
    public @Nullable Sound loadSound(@NotNull String path, @Nullable Sound def) {
        return loadEnum(path, def, Sound.class);
    }

    @Contract("_, !null -> !null")
    public @Nullable Sound getSound(@NotNull String path, @Nullable Sound def) {
        return getEnum(path, def, Sound.class);
    }

    @Contract("_, !null -> !null")
    public @Nullable Material loadMaterial(@NotNull String path, @Nullable Material def) {
        return loadEnum(path, def, Material.class);
    }

    public @NotNull List<Material> loadMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
        return loadEnumList(path, def, Material.class);
    }

    public @NotNull EnumSet<Material> loadMaterialSet(@NotNull String path, @Nullable Collection<Material> def) {
        return loadEnumSet(path, def, Material.class);
    }

    public @NotNull ItemFlag[] loadItemFlags(@NotNull String path, ItemFlag[] def) {
        return loadEnumSet(path, def == null ? null : Arrays.asList(def), ItemFlag.class).toArray(new ItemFlag[0]);
    }

    private @NotNull String getError(String path) {
        return "Value has wrong type or wrong value at '" + path + ":' on file " + file.getName();
    }
}