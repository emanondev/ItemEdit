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
import java.util.*;
import java.util.function.Predicate;

public abstract class APlugin extends JavaPlugin {

    private final HashMap<String, YMLConfig> configs = new HashMap<>();
    private final HashMap<String, YMLConfig> languageConfigs = new HashMap<>();
    private boolean useMultiLanguage;
    private String defaultLanguage;
    private CooldownAPI cooldownApi = null;

    /**
     * Gets plugin Config file.
     *
     * @return Plugin config file
     * @see #getConfig(String) getConfig("config.yml");
     */
    @Override
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
    public @NotNull YMLConfig getConfig(@NotNull String fileName) {
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
    //TODO add minimessage?
    public void log(@NotNull String log) {
        Bukkit.getConsoleSender().sendMessage(
                ChatColor.translateAlternateColorCodes('&', ChatColor.DARK_BLUE + "["
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
                } catch (Exception ignored) {
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

    public @NotNull YMLConfig getLanguageConfig(@Nullable CommandSender sender) {
        String locale;
        if (!(sender instanceof Player))
            locale = this.defaultLanguage;
        else if (Util.isVersionAfter(1, 12) && this.useMultiLanguage)
            locale = ((Player) sender).getLocale().equals("zh_tw") ? //apparently zh_tw and zh_cn are quite different, zh_cn and zh_hk will both fall under zh.yml
                    ((Player) sender).getLocale() : ((Player) sender).getLocale().split("_")[0];
        else
            locale = this.defaultLanguage;

        if (this.languageConfigs.containsKey(locale))
            return languageConfigs.get(locale);

        String fileName = "languages" + File.separator + locale + ".yml";

        if (locale.equals(this.defaultLanguage) || new File(getDataFolder(), fileName).exists()
                || this.getResource("languages/" + locale + ".yml") != null) {
            YMLConfig conf = new YMLConfig(this, fileName);
            languageConfigs.put(locale, conf);
            return conf;
        }

        YMLConfig conf = getLanguageConfig(null);
        languageConfigs.put(locale, conf);
        return conf;
    }

    public @NotNull CooldownAPI getCooldownAPI() {
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
     * @see #enable()
     * @deprecated internal use only
     */
    @Override
    @Deprecated
    public void onEnable() {
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

            //getConfig(); //force load the config.yml file
            this.useMultiLanguage = getConfig().getBoolean("language.use_multilanguage", true);
            this.defaultLanguage = getConfig().getString("language.default", "en");
            if (getConfig().getBoolean("language.regen_files", true)) {
                YMLConfig version = getConfig("version.yml");
                if (!getDescription().getVersion().equals(version.loadMessage("previous_version", "1"))) {
                    version.set("previous_version", getDescription().getVersion());
                    version.save();
                    File langFolder = new File(getDataFolder(), "languages");
                    if (langFolder.exists()) {
                        File[] list = langFolder.listFiles();
                        if (list != null) {
                            log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
                            for (File file : list)
                                if (getResource("languages/" + file.getName()) != null) {
                                    saveResource("languages/" + file.getName(), true);
                                }
                        }
                    }
                }
            }
            getLanguageConfig(null);
            if (getProjectId() != null && getConfig().getBoolean("check-updates", true))
                new UpdateChecker(this, getProjectId()).logUpdates();
            enable();
            log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");

        } catch (
                Throwable e) {
            this.log(ChatColor.RED + "Error while loading " + this.getName() + ", disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    protected void registerLanguagesMetrics(Metrics metrics, Predicate<Player> isAdmin, Predicate<Player> isUser) {
        if (metrics == null)
            return;
        if (!Util.isVersionAfter(1, 12))
            return;
        metrics.addCustomChart(new Metrics.DrilldownPie("admins_languages", () -> {
            Map<String, Map<String, Integer>> mainMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isAdmin.test(player))
                    continue;
                String locale = player.getLocale().toLowerCase(Locale.ENGLISH);
                String pre = locale.split("_")[0];
                if (!mainMap.containsKey(pre))
                    mainMap.put(pre, new HashMap<>());
                Map<String, Integer> subMap = mainMap.get(pre);
                subMap.put(locale, subMap.getOrDefault(locale, 0) + 1);
            }
            return mainMap;
        }));
        metrics.addCustomChart(new Metrics.DrilldownPie("users_languages", () -> {
            Map<String, Map<String, Integer>> mainMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!isUser.test(player))
                    continue;
                String locale = player.getLocale().toLowerCase(Locale.ENGLISH);
                String pre = locale.split("_")[0];
                if (!mainMap.containsKey(pre))
                    mainMap.put(pre, new HashMap<>());
                Map<String, Integer> subMap = mainMap.get(pre);
                subMap.put(locale, subMap.getOrDefault(locale, 0) + 1);
            }
            return mainMap;
        }));
    }

    protected void enableWithError(@NotNull String error) {
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

    @Override
    public void onDisable() {
        disable();
    }

    public abstract void disable();

    public @Nullable Metrics registerMetrics(int pluginId) {
        try {
            return new Metrics(this, pluginId);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    /**
     * Utility class to explain users what when wrong on plugin load/enable and why commands are not working
     */
    protected final class TabExecutorError implements TabExecutor {

        private final String msg;

        public TabExecutorError(@NotNull String msg) {
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
