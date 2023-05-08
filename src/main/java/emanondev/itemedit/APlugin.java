package emanondev.itemedit;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.compability.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public abstract class APlugin extends JavaPlugin {

    private final HashMap<String, YMLConfig> configs = new HashMap<>();
    private final HashMap<String, YMLConfig> languageConfigs = new HashMap<>();
    private boolean useMultiLanguage;
    private String defaultLanguage;

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
     *
     * @param log log
     */
    public void log(@NotNull String log) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "["
                + ChatColor.WHITE + this.getName() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log));
    }

    public abstract @Nullable Integer getProjectId();

    /**
     * Print on console with '[(PluginName)] (prefix) (log)' format
     *
     * @param color  prefix color
     * @param prefix prefix
     * @param log    log
     */
    public void log(@NotNull ChatColor color, @NotNull String prefix, @NotNull String log) {
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
        languageConfigs.clear();
        getLanguageConfig(null);
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


    public YMLConfig getLanguageConfig(@Nullable CommandSender sender) {
        String locale;
        if (!(sender instanceof Player))
            locale = this.defaultLanguage;
        else if (Util.isVersionAfter(1, 11, 2) && this.useMultiLanguage)
            locale = ((Player) sender).getLocale().startsWith("zh") ? //apparently zh_tw and zh_cn are quite different
                    ((Player) sender).getLocale() : ((Player) sender).getLocale().split("_")[0];
        else
            locale = this.defaultLanguage;

        if (this.languageConfigs.containsKey(locale))
            return languageConfigs.get(locale);

        String fileName = "languages" + File.separator + locale + ".yml";

        if (locale.equals(this.defaultLanguage) || new File(getDataFolder(), fileName).exists() || this.getResource(fileName) != null) {
            YMLConfig conf = new YMLConfig(this, fileName);
            languageConfigs.put(locale, conf);
            return conf;
        }

        YMLConfig conf = getLanguageConfig(null);
        languageConfigs.put(locale, conf);
        return conf;
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
     *
     */
    public final void onEnable() {
        try {
            long now = System.currentTimeMillis();
            try {
                Class.forName("org.spigotmc.SpigotConfig");
            } catch (Throwable t) {
                enableWithError("CraftBukkit is not supported!!! use Spigot or Paper");
                log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
                return;
            }
            if (Bukkit.getServer().getBukkitVersion().startsWith("1.7.")) {
                enableWithError("1.7.x is not supported!!! use 1.8+");
                log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
                return;
            }

            getConfig(); //force load the config.yml file
            this.useMultiLanguage = getConfig().getBoolean("language.use_multilanguage", true);
            this.defaultLanguage = getConfig().getString("language.default", "en");
            getLanguageConfig(null);
            if (getProjectId() != null && getConfig().getBoolean("check-updates", true))
                new UpdateChecker(this, getProjectId()).logUpdates();
            enable();
            log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");

        } catch (Throwable e) {
            this.log(ChatColor.RED + "Error while loading " + this.getName() + ", disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    protected void enableWithError(String error) {
        TabExecutorError exec = new TabExecutorError(ChatColor.RED + error);
        for (String command : this.getDescription().getCommands().keySet())
            registerCommand(command, exec, null);
        log(ChatColor.RED + error);
    }

    public abstract void enable();

    /**
     * Called by onReload(), configuration files are already updated
     * This method should update any variable read from configuration
     *
     * @see #onReload()
     */
    public abstract void reload();

    /**
     * This call reload configuration files and suggest to the plugin to update/reload his info
     *
     * @see #reload()
     */
    public final void onReload() {
        long now = System.currentTimeMillis();
        this.useMultiLanguage = getConfig().getBoolean("language.use_multilanguage", true);
        this.defaultLanguage = getConfig().getString("language.default_language", "en");
        reloadConfigs();
        reload();
        log(ChatColor.GREEN, "#", "Reloaded (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
    }

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

    public void onDisable() {
        disable();
    }

    public abstract void disable();

    public Metrics registerMetrics(int pluginId) {
        try {
            return new Metrics(this, pluginId);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

}
