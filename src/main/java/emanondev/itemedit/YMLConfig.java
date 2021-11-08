package emanondev.itemedit;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class YMLConfig extends YamlConfiguration {
	private JavaPlugin plugin;
	private File file;
	private String name;

	/**
	 * Returns the file path of this config.
	 * 
	 * @return the file path starting by
	 *         {@link #getPlugin()}.{@link JavaPlugin#getDataFolder()
	 *         getDataFolder()}.
	 */
	public String getFileName() {
		return name;
	}

	/**
	 * Return the plugin associated with this Config.
	 * 
	 * @return the plugin associated with this Config
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}

	/**
	 * Constructs a new Configuration File.<br>
	 * Used file is located at plugin.{@link JavaPlugin#getDataFolder()
	 * getDataFolder()}/{@link YMLConfig#fixName() YMLConfig.fixName(name)}.<br>
	 * If the plugin jar has a file on name path, that file is used to generate the
	 * config file.
	 * 
	 * @param plugin
	 *            associated plugin to grab the folder
	 * @param name
	 *            raw name of the file subpath
	 * @throws NullPointerException
	 *             if name is null
	 * @throws IllegalArgumentException
	 *             if name is empty
	 */
	public YMLConfig(@NotNull JavaPlugin plugin, @NotNull String name) {
		Validate.notNull(plugin, "plugin is null");
		this.plugin = plugin;
		name = fixName(name);
		this.name = name;
		file = new File(plugin.getDataFolder(), name);
		reload();
	}

	/**
	 * Fix the given name for a yaml file name
	 * 
	 * @param name
	 *            the raw name of the file with or withouth .yml
	 * @return fixed name
	 * @throws NullPointerException
	 *             if name is null
	 * @throws IllegalArgumentException
	 *             if name is empty
	 */
	public static String fixName(String name) {
		Validate.notNull(name, "YAML file must have a name!");
		Validate.notEmpty(name, "YAML file must have a name!");
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

			if (!file.getParentFile().exists()) { // Create parent folders if they don't exist
				file.getParentFile().mkdirs();
			}
			if (plugin.getResource(file.getName()) != null) {
				plugin.saveResource(file.getName(), true); // Save the one from the JAR if possible
			} else {
				try {
					file.createNewFile();
				} // Create a blank file if there's not one to copy from the JAR
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			this.load(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (plugin.getResource(file.getName()) != null) { // Set up defaults in case their config is broken.
			InputStreamReader defConfigStream = null;
			try {
				defConfigStream = new InputStreamReader(plugin.getResource(name), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			this.setDefaults(YamlConfiguration.loadConfiguration(defConfigStream));
		}
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
	 * Gets a set of subkeys at path.
	 * 
	 * @param path
	 * @return subkeys at selected path
	 */
	public @NotNull Set<String> getKeys(@NotNull String path) {
		ConfigurationSection section = this.getConfigurationSection(path);
		if (section == null)
			return new LinkedHashSet<String>();
		else
			return section.getKeys(false);
	}

	/**
	 * Gets the object from the config or set the default.<br>
	 * Get the object from path, if the object is null or of different class default
	 * value is returned and saved on disk else return the object
	 * 
	 * @param path
	 * @param def
	 * @param clazz
	 * @return object or default
	 */
	@SuppressWarnings("unchecked")
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
	 * @param path
	 * @param def
	 * @param clazz
	 * @return object or default
	 */
	@SuppressWarnings("unchecked")
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

	public @Nullable Double loadDouble(@NotNull String path, @Nullable Double def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.doubleValue();
	}

	public @Nullable Boolean loadBoolean(@NotNull String path, @Nullable Boolean def) {
		return load(path, def, Boolean.class);
	}

	/**
	 * Use {@link #loadInteger(String, Integer)}
	 * 
	 * @param path
	 * @param def
	 * @return
	 */
	@Deprecated
	public @Nullable int loadInt(@NotNull String path, @Nullable Integer def) {
		Number val = load(path, def, Number.class);
		return val == null ? 0 : val.intValue();
	}

	public @Nullable Integer loadInteger(@NotNull String path, @Nullable Integer def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.intValue();
	}

	public @Nullable Integer getInteger(@NotNull String path, @Nullable Integer def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.intValue();
	}

	public @Nullable Long loadLong(@NotNull String path, @Nullable Long def) {
		Number val = load(path, def, Number.class);
		return val == null ? null : val.longValue();
	}

	/**
	 * Load String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * target = null
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 * @see #loadString(String, String, Player, boolean, String...) loadString(path,
	 *      def, null, color, args)
	 * @see #load(String, Object, Class)
	 */
	public @Nullable String loadString(@NotNull String path, @Nullable String def, boolean color, String... args) {
		return loadString(path, def, null, color, args);
	}

	/**
	 * Load String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param target
	 *            target user for papi support
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 * @see #loadString(String, String, Player, boolean, String...) loadString(path,
	 *      def, null, color, args)
	 * @see #load(String, Object, Class)
	 */
	public @Nullable String loadString(@NotNull String path, @Nullable String def, @Nullable Player target,
			boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
				save();
			}
		}
		return UtilsString.fix(load(path, def, String.class), target, color, args);
	}

	/**
	 * Get String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param target
	 *            target user for papi support
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 * @see #get(String, Object, Class)
	 */
	public @Nullable String getString(@NotNull String path, @Nullable String def, @Nullable Player target,
			boolean color, String... args) {
		if (args.length > 0 && this.contains(path))
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
				save();
			}
		return UtilsString.fix(get(path, def, String.class), target, color, args);
	}

	/**
	 * Get String value.<br>
	 * adds path+_HOLDERS if any exists to notify usable holders<br>
	 * target = null
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param target
	 *            target user for papi support
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 * @see #getString(String, String, Player, boolean, String...) getString(path,
	 *      def, null, color, args)
	 * @see #get(String, Object, Class)
	 */
	public @Nullable String getString(@NotNull String path, @Nullable String def, boolean color, String... args) {
		return this.getString(path, def, null, color, args);
	}

	/**
	 * target = null
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param color
	 *            convert colors?
	 * @return the value found or default if none
	 */
	public @NotNull List<String> loadStringList(@NotNull String path, @Nullable List<String> def, boolean color) {
		return loadStringList(path, def, null, color);
	}

	/**
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param target
	 *            target user for papi support
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 */
	@SuppressWarnings("unchecked")
	public @NotNull List<String> loadStringList(@NotNull String path, @Nullable List<String> def,
			@Nullable Player target, boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		try {
			return UtilsString.fix(load(path, def, List.class), target, color, args);
		} catch (Exception e) {
			e.printStackTrace();
			return UtilsString.fix(def, target, color, args);
		}
	}

	/**
	 * target = null
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param color
	 *            convert colors?
	 * @return the value found or default if none
	 */
	public @NotNull List<String> getStringList(@NotNull String path, @Nullable List<String> def, boolean color) {
		return getStringList(path, def, null, color);
	}

	/**
	 * adds path+_HOLDERS if any exists to notify usable holders
	 * 
	 * @param path
	 *            the path
	 * @param def
	 *            default value
	 * @param target
	 *            target user for papi support
	 * @param color
	 *            convert colors?
	 * @param args
	 *            holders and replacer
	 * @return the value found or default if none
	 */
	@SuppressWarnings("unchecked")
	public @NotNull List<String> getStringList(@NotNull String path, @Nullable List<String> def,
			@Nullable Player target, boolean color, String... args) {
		if (args.length > 0) {
			if (!this.contains(path + "_HOLDERS")) {
				StringBuilder build = new StringBuilder("");
				for (int i = 0; i < args.length; i += 2)
					build.append(args[i] + " ");
				this.set(path + "_HOLDERS", build.substring(0, build.length() - 1));
			}
		}
		try {
			return UtilsString.fix(get(path, def, List.class), target, color, args);
		} catch (Exception e) {
			e.printStackTrace();
			return UtilsString.fix(def, target, color, args);
		}
	}

	public @Nullable ItemStack loadItemStack(@NotNull String path, @Nullable ItemStack def) {
		return load(path, def, ItemStack.class);
	}

	/**
	 * assumes that enum costants are all uppercased
	 * 
	 * @param <T>
	 *            the class of the enum
	 * @param path
	 *            the path
	 * @param clazz
	 *            the class of the enum
	 * @param def
	 *            default value
	 * @return if path lead to a string attemps return the matching Enum value, if
	 *         the string is empty return def
	 */
	public @Nullable <T extends Enum<T>> T loadEnum(@NotNull String path, @Nullable T def, @NotNull Class<T> clazz) {
		return stringToEnum(loadString(path, def == null ? null : def.name(), false), def, clazz, path);
	}

	public @Nullable <T extends Enum<T>> T getEnum(@NotNull String path, @Nullable T def, @NotNull Class<T> clazz) {
		return stringToEnum(getString(path, def == null ? null : def.name()), def, clazz, path);
	}

	private @Nullable <T extends Enum<T>> T stringToEnum(@Nullable String value, @Nullable T def,
			@NotNull Class<T> clazz, @NotNull String errorPath) {
		try {
			if (value == null || value.isEmpty())
				return def;
			return (T) Enum.valueOf(clazz, value);
		} catch (IllegalArgumentException e) {
			try {
				return (T) Enum.valueOf(clazz, value.toUpperCase());
			} catch (IllegalArgumentException e2) {
				e2.printStackTrace();
				new IllegalArgumentException(getError(errorPath) + "; can't find value for '" + value + "' from enum '"
						+ clazz.getName() + "' using default").printStackTrace();
			}
		}
		return def;
	}

	/**
	 * assumes that enum costants are all uppercased null enum values contained in
	 * def might be lost
	 * 
	 * @param <T>
	 *            the class of the enum
	 * @param path
	 *            the path
	 * @param clazz
	 *            the class of the enum
	 * @param def
	 *            default value
	 * @return the value found or default if none
	 */
	public @NotNull <T extends Enum<T>> List<T> loadEnumList(@NotNull String path, @Nullable Collection<T> def,
			@NotNull Class<T> clazz) {
		return stringListToEnumCollection(new ArrayList<T>(),
				loadStringList(path, enumCollectionToStringList(def), false), clazz, path);
	}

	public @NotNull <T extends Enum<T>> EnumSet<T> loadEnumSet(@NotNull String path, @Nullable Collection<T> def,
			@NotNull Class<T> clazz) {
		return stringListToEnumCollection(EnumSet.noneOf(clazz),
				loadStringList(path, enumCollectionToStringList(def), false), clazz, path);
	}

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

			Map<String, T> result = new LinkedHashMap<String, T>();
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

	public @Nullable Sound loadSound(@NotNull String path, @Nullable Sound def) {
		return loadEnum(path, def, Sound.class);
	}

	public @Nullable Sound getSound(@NotNull String path, @Nullable Sound def) {
		return getEnum(path, def, Sound.class);
	}

	public @Nullable Material loadMaterial(@NotNull String path, @Nullable Material def) {
		return loadEnum(path, def, Material.class);
	}

	public @Nullable List<Material> loadMaterialList(@NotNull String path, @Nullable Collection<Material> def) {
		return loadEnumList(path, def, Material.class);
	}

	public @Nullable EnumSet<Material> loadMaterialSet(@NotNull String path, @Nullable Collection<Material> def) {
		return loadEnumSet(path, def, Material.class);
	}

	public @NotNull ItemFlag[] loadItemFlags(@NotNull String path, ItemFlag[] def) {
		return loadEnumSet(path, def == null ? null : Arrays.asList(def), ItemFlag.class).toArray(new ItemFlag[0]);
	}

	private String getError(String path) {
		return "Value has wrong type or wrong value at '" + path + ":' on file " + file.getName();
	}
}