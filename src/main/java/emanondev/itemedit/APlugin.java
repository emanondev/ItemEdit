package emanondev.itemedit;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.compability.Metrics;
import emanondev.itemedit.plugin.PluginAdditionalInfo;
import emanondev.itemedit.utility.ReflectionUtils;
import emanondev.itemedit.utility.VersionUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public abstract class APlugin extends JavaPlugin {

    private final Map<String, YMLConfig> configs =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private final Map<String, YMLConfig> languageConfigs =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    @Getter
    private final PluginAdditionalInfo pluginAdditionalInfo;
    private boolean useMultiLanguage;
    private String defaultLanguage;
    private CooldownAPI cooldownApi = null;
    @Getter
    private Metrics bstatsMetrics;

    protected APlugin() {
        this.pluginAdditionalInfo = new PluginAdditionalInfo(this);
    }

    /**
     * Gets plugin Config file.
     *
     * @return Plugin config file from {@code config.yml}
     * @see #getConfig(String)
     */
    @Override
    @NotNull
    public YMLConfig getConfig() {
        return getConfig("config.yml");
    }

    /**
     * Gets config file.<br>
     * Also keep tracks of the file and reload it on {@link #reloadConfig()} method
     * calls.<br>
     * This method also appends {@code .yml} to file name if not present.
     *
     * @param fileName may contain {@link File#separator} for file inside folders
     * @return config file at specified path inside plugin folder.
     */
    @NotNull
    public YMLConfig getConfig(@NotNull String fileName) {
        fileName = YMLConfig.fixName(fileName);
        if (configs.containsKey(fileName))
            return configs.get(fileName);
        YMLConfig conf = new YMLConfig(this, fileName);
        configs.put(fileName, conf);
        return conf;
    }

    /**
     * Logs a message to the console in the format: [{@code PluginName}] {@code log}.
     *
     * @param log The message to log.
     */
    public void log(@NotNull String log) {
        Bukkit.getConsoleSender().sendMessage(UtilsString.fix(ChatColor.DARK_BLUE + "["
                        + ChatColor.WHITE + this.getName() + ChatColor.DARK_BLUE + "] " + ChatColor.WHITE + log,
                null, true));
    }

    /**
     * Logs a message to the console in the format: [{@code PluginName}]{@code color} {@code prefix} {@code log}.
     *
     * @param color  prefix color
     * @param prefix prefix
     * @param log    log
     */
    public void log(@NotNull ChatColor color,
                    @NotNull String prefix,
                    @NotNull String log) {
        log(color + prefix + " " + ChatColor.WHITE + log);
    }

    /**
     * Registers a listener for events. Ensure event methods have @EventHandler.
     *
     * @param listener The listener to register.
     */
    public void registerListener(@NotNull Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    /**
     * Registers a command with its executor and optional aliases.
     *
     * @param executor The command executor.
     * @param aliases  Optional list of aliases for the command.
     */
    public void registerCommand(@NotNull AbstractCommand executor,
                                @Nullable List<String> aliases) {
        registerCommand(executor.getName(), executor, aliases);
    }

    /**
     * Registers a command with a specified name, executor, and optional aliases.
     *
     * @param commandName The command name.
     * @param executor    The command executor.
     * @param aliases     Optional list of aliases for the command.
     */
    public void registerCommand(@NotNull String commandName,
                                @NotNull TabExecutor executor,
                                @Nullable List<String> aliases) {
        PluginCommand command = getCommand(commandName);
        if (command == null) {
            log("&cUnable to register Command &e" + commandName);
            return;
        }
        command.setExecutor(executor);
        command.setTabCompleter(executor);
        if (aliases != null)
            command.setAliases(aliases);
    }

    /**
     * Retrieves the language configuration for the specified sender.
     * Fallbacks to the default language configuration if necessary.
     *
     * @param sender The command sender.
     * @return The language configuration for the sender.
     */
    @NotNull
    public YMLConfig getLanguageConfig(@Nullable CommandSender sender) {
        String locale = getLocale(sender);

        if (this.languageConfigs.containsKey(locale)) {
            return languageConfigs.get(locale);
        }

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

    /**
     * Called by {@link #onEnable()}.<br>
     * This method should register commands and listeners.
     */
    public abstract void enable();

    /**
     * Called by {@link #onReload()}, configuration files are already updated.<br>
     * This method should update any variable read from configuration.
     */
    public abstract void reload();

    /**
     * Called by {@link #onDisable()}.<br>
     * This method should save persistent data.
     */
    public abstract void disable();

    /**
     * You can update configuration by overriding this method.
     * configuration version is saved as int on {@code config.yml} at path {@code config-version},
     * if not specified it's {@code 1}.
     *
     * @param oldConfigVersion old configuration version you update from
     */
    protected void updateConfigurations(int oldConfigVersion) {
    }

    /**
     * @see #languagesMetricsIsAdmin()
     * @see #languagesMetricsIsUser()
     */
    protected boolean addLanguagesMetrics() {
        return false;
    }

    protected @NotNull Predicate<Player> languagesMetricsIsAdmin() {
        return ServerOperator::isOp;
    }

    protected @NotNull Predicate<Player> languagesMetricsIsUser() {
        return player -> true;
    }

    /**
     * Reloads all configuration files and updates their references.
     */
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
     * Retrieves the {@link CooldownAPI} instance for the plugin.
     * Initializes it if not already available.
     *
     * @return The CooldownAPI instance.
     */
    public @NotNull CooldownAPI getCooldownAPI() {
        if (cooldownApi == null)
            cooldownApi = new CooldownAPI(this);
        return cooldownApi;
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

            if (!ReflectionUtils.isClassPresent("org.spigotmc.SpigotConfig")) {
                enableWithError("CraftBukkit is not supported!!! use Spigot or Paper");
                log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
                return;
            }
            if (!VersionUtils.isVersionAfter(1, 8)) {
                enableWithError("1.7.x is not supported!!! use 1.8+");
                log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
                return;
            }
            initLanguages();
            if (getPluginAdditionalInfo().getSpigotResourceId() != null && getConfig().getBoolean("check-updates", true))
                new UpdateChecker(this).logUpdates();
            initConfigUpdater();
            initMetrics();

            enable();

            log(ChatColor.GREEN, "#", "Enabled (took &e" + (System.currentTimeMillis() - now) + "&f ms)");

        } catch (Throwable e) {
            this.log(ChatColor.RED + "Error while loading " + this.getName() + ", disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

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

    /**
     * Enables the plugin but displays an error message.
     * Registers all plugin commands with a handler that explains why the plugin failed to load properly.
     *
     * @param error The error message to display.
     */
    protected final void enableWithError(@NotNull String error) {
        TabExecutorError exec = new TabExecutorError(ChatColor.RED + error);
        for (String command : this.getDescription().getCommands().keySet())
            registerCommand(command, exec, null);
        log(ChatColor.RED + error);
    }

    @NotNull
    private String getLocale(@Nullable CommandSender sender) {
        String locale;
        if (!(sender instanceof Player))
            locale = this.defaultLanguage;
        else if (VersionUtils.isVersionAfter(1, 12) && this.useMultiLanguage) {
            //apparently zh_tw and zh_cn are quite different, zh_cn and zh_hk will both fall under zh.yml
            locale = ((Player) sender).getLocale().equals("zh_tw") ?
                    ((Player) sender).getLocale() : ((Player) sender).getLocale().split("_")[0];
        } else {
            locale = this.defaultLanguage;
        }
        return locale;
    }

    private void initMetrics() {
        Integer pluginId = getPluginAdditionalInfo().getBstatsPluginId();
        if (pluginId == null) {
            bstatsMetrics = null;
            return;
        }
        try {
            bstatsMetrics = new Metrics(this, pluginId);
            if (addLanguagesMetrics()) {
                Predicate<Player> isAdmin = languagesMetricsIsAdmin();
                Predicate<Player> isUser = languagesMetricsIsUser();

                if (!VersionUtils.isVersionAfter(1, 12)) {
                    return;
                }
                bstatsMetrics.addCustomChart(new Metrics.DrilldownPie("admins_languages", () -> {
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
                bstatsMetrics.addCustomChart(new Metrics.DrilldownPie("users_languages", () -> {
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
        } catch (Throwable t) {
            t.printStackTrace();
        }
        bstatsMetrics = null;
    }

    private void initConfigUpdater() {
        ConfigurationSection def = this.getConfig().getDefaultSection();
        int currentVersion = def == null ? 1 : def.getInt("config-version", 1);
        int oldVersion = this.getConfig().getInt("config-version", 1);
        if (oldVersion >= currentVersion)
            return;
        this.log("Updating configuration version (" + oldVersion + " -> " + currentVersion + ")");
        updateConfigurations(oldVersion);
        this.getConfig().set("config-version", currentVersion);
        this.log("Updated configuration version (" + oldVersion + " -> " + currentVersion + ")");
        this.getConfig().save();
    }

    private void initLanguages() {
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
                        for (File file : list)
                            if (getResource("languages/" + file.getName()) != null) {
                                saveResource("languages/" + file.getName(), true);
                            }
                    }
                }
            }
        }
        getLanguageConfig(null);
    }

    /**
     * Utility class for handling commands when the plugin fails to load properly.
     * Displays an error message for all commands and prevents execution.
     */
    protected final class TabExecutorError implements TabExecutor {

        private final String msg;

        public TabExecutorError(@NotNull String msg) {
            this.msg = msg;
            for (int i = 0; i < 20; i++)
                APlugin.this.log(msg);
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender,
                                          @NotNull Command command,
                                          @NotNull String alias,
                                          @NotNull String[] args) {
            return Collections.emptyList();
        }

        @Override
        public boolean onCommand(@NotNull CommandSender sender,
                                 @NotNull Command command,
                                 @NotNull String label,
                                 @NotNull String[] args) {
            sender.sendMessage(msg);
            return true;
        }
    }
}
