package emanondev.itemedit;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;

public abstract class APlugin extends JavaPlugin {

	private HashMap<String, YMLConfig> configs = new HashMap<>();

	/**
	 * Gets plugin Config file.
	 * 
	 * @see #getConfig(String) getConfig("config.yml");
	 * @return Plugin config file
	 */
	public @NotNull YMLConfig getConfig() {
		return getConfig("config.yml");
	}

	/**
	 * Gets config file.<br>
	 * Also keep tracks of the file and reload it on {@link #onReload()} method
	 * calls.<br>
	 * Append ".yml" to file name if not present.
	 * 
	 * @param fileName
	 *            might contains folder separator for file inside folders
	 * @return config file at specified path inside plugin folder.
	 */
	public @NotNull YMLConfig getConfig(String fileName) {
		fileName = YMLConfig.fixName(fileName);
		if (configs.containsKey(fileName))
			return configs.get(fileName);
		YMLConfig conf = new YMLConfig(this, fileName);
		configs.put(fileName, conf);
		return conf;
	}

	public void log(String log) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "["
				+ ChatColor.WHITE + this.getName() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
	}

	public void log(ChatColor color, String prefix, String log) {
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "[" + ChatColor.WHITE
						+ this.getName() + ChatColor.DARK_BLUE + "] " + color + prefix + " " + ChatColor.WHITE + log));
	}

	protected void reloadConfigs() {
		boolean check = false;
		for (YMLConfig conf : configs.values())
			try {
				if (conf.getFile().exists())
					conf.reload();
				else
					check = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (check) {
			ArrayList<String> toRemove = new ArrayList<>();
			configs.forEach((k, v) -> {
				try {
					if (!v.getFile().exists())
						toRemove.add(k);
				} catch (Exception e) {
				}
			});
			for (String key : toRemove)
				configs.remove(key);
		}
	}

	/**
	 * Register the Listener (remember to put @EventHandler on listener
	 * methods).<br>
	 * Shortcut for {@link #getServer()}.{@link org.bukkit.Server#getPluginManager()
	 * getPluginManager()}.{@link org.bukkit.plugin.PluginManager#registerEvents(Listener, org.bukkit.plugin.Plugin)
	 * registerEvents(listener, this)};
	 * 
	 * @param listener
	 *            Listener to register
	 */
	protected void registerListener(@NotNull Listener listener) {
		getServer().getPluginManager().registerEvents(listener, this);
	}

	private CooldownAPI cooldownApi = null;

	public CooldownAPI getCooldownAPI() {
		if (cooldownApi == null)
			cooldownApi = new CooldownAPI(this);
		return cooldownApi;
	}

}
