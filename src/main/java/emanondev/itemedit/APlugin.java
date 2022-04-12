package emanondev.itemedit;

import emanondev.itemedit.command.AbstractCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class APlugin extends JavaPlugin {

    private final HashMap<String, YMLConfig> configs = new HashMap<>();

    /**
     * Gets plugin Config file.
     *
     * @return Plugin config file
     * @see #getConfig(String) getConfig("config.yml");
     */
    public @NotNull YMLConfig getConfig() {
        return getConfig("config.yml");
    }

    /**
     * Gets config file.<br>
     * Also keep tracks of the file and reload it on {@link #reloadConfig()} method
     * calls.<br>
     * Append ".yml" to file name if not present.
     *
     * @param fileName might contains folder separator for file inside folders
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

    /**
     * Print on console with '[(PluginName)] (log)' format
     * @param log log
     */
    public void log(String log) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "["
                + ChatColor.WHITE + this.getName() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }
    /**
     * Print on console with '[(PluginName)] (prefix) (log)' format
     * @param color prefix color
     * @param prefix prefix
     * @param log log
     */
    public void log(ChatColor color, String prefix, String log) {
        log(color + prefix + " " + ChatColor.WHITE + log);
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
     * @param listener Listener to register
     */
    public void registerListener(@NotNull Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private CooldownAPI cooldownApi = null;

    public CooldownAPI getCooldownAPI() {
        if (cooldownApi == null)
            cooldownApi = new CooldownAPI(this);
        return cooldownApi;
    }

    public void registerCommand(@NotNull AbstractCommand executor, @Nullable List<String> aliases) {
        registerCommand(executor.getName(), executor, aliases);
    }


    public void registerCommand(@NotNull String commandName, @NotNull TabExecutor executor, @Nullable List<String> aliases) {
        PluginCommand command = getCommand(commandName);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
        if (aliases != null)
            command.setAliases(aliases);
    }

    /**
     * This call reload configuration files and suggest to the plugin to update/reload his info
     *
     * @see #onReload()
     */
    public final void reload() {
        long now = System.currentTimeMillis();
        reloadConfigs();
        onReload();
        log(ChatColor.GREEN, "#", "Reloaded (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

    /**
     * Called by reload(), configuration files are already updated
     * This method should update any variable read from configuration
     *
     * @see #reload()
     */
    public abstract void onReload();

    /**
     * Utility class to explain users what when wrong on plugin load/enable and why commands are not working
     */
    protected final class TabExecutorError implements TabExecutor {

        private final String msg;

        public TabExecutorError(String msg) {
            this.msg = msg;
            for (int i = 0; i < 20; i++)
                APlugin.this.log(msg);
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
            return Collections.emptyList();
        }

        @Override
        public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            sender.sendMessage(msg);
            return true;
        }
    }

}
