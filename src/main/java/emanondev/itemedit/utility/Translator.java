package emanondev.itemedit.utility;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Translator {

    private final Map<String, List<YMLConfig>> fallbackList =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    private final Map<String, YMLConfig> configurations =
            VersionUtils.hasFoliaAPI() ? new ConcurrentHashMap<>() : new HashMap<>();
    @Getter
    private final APlugin plugin;
    @Getter
    private boolean useMultiLanguage;
    @Getter
    private String defaultLocale;
    @Getter
    private boolean logMissingMessages;

    public Translator(APlugin plugin) {
        this.plugin = plugin;
        this.useMultiLanguage = plugin.getConfig().getBoolean("language.use_multilanguage", true);
        this.defaultLocale = plugin.getConfig().getString("language.default_language", "en");
        logMissingMessages = plugin.getConfig().getBoolean("language.log_missing_messages", true);
    }

    public void reload() {
        this.useMultiLanguage = plugin.getConfig().getBoolean("language.use_multilanguage", true);
        this.defaultLocale = plugin.getConfig().getString("language.default_language", "en");
        logMissingMessages = plugin.getConfig().getBoolean("language.log_missing_messages", true);
        fallbackList.clear();
        configurations.clear();
    }

    public List<String> translateList(CommandSender target, String path, String... holders) {
        return translateList(target, path, true, holders);
    }

    @SuppressWarnings("unchecked")
    public List<String> translateList(CommandSender target, String path, boolean placeholderApi, String... holders) {
        String locale = VersionUtils.isAfter(1, 12)
                && isUseMultiLanguage()
                && target instanceof Player ?
                ((Player) target).getLocale() : null;
        if (locale == null) {
            locale = getDefaultLocale();
        }
        List<YMLConfig> locales = getLocaleFiles(locale);
        List<String> message = null;
        for (YMLConfig file : locales) {
            Object obj = file.get(path, null);
            if (obj instanceof String) {
                message = List.of((String) obj);
                break;
            }
            try {
                if (obj instanceof List) {
                    message = (List<String>) obj;
                    break;
                }
            } catch (Exception ignored) {
            }
            if (logMissingMessages) {
                getPlugin().log("Missing language messages at: '" + path + "' for " + file.getFileName());
            }
        }
        if (message == null) {
            return List.of();
        }
        return UtilsString.fix(message, placeholderApi && target instanceof Player ? ((Player) target) : null, true, holders);
    }

    public String translate(CommandSender target, String path, String... holders) {
        return translate(target, path, true, holders);
    }

    public String translate(CommandSender target, String path, boolean placeholderApi, String... holders) {
        String locale = VersionUtils.isAfter(1, 12)
                && isUseMultiLanguage()
                && target instanceof Player ?
                ((Player) target).getLocale() : null;
        if (locale == null) {
            locale = getDefaultLocale();
        }
        List<YMLConfig> locales = getLocaleFiles(locale);
        String message = null;
        for (YMLConfig file : locales) {
            Object obj = file.get(path, null);
            if (obj instanceof String) {
                message = (String) obj;
                break;
            }
            try {
                if (obj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> values = (List<String>) obj;
                    message = String.join("\n", values);
                    break;
                }
            } catch (Exception ignored) {
            }
            if (logMissingMessages) {
                getPlugin().log("Missing language messages at: '" + path + "' for " + file.getFileName());
            }
        }
        if (message == null) {
            return null;
        }
        return UtilsString.fix(message, placeholderApi && target instanceof Player ? ((Player) target) : null, true, holders);
    }


    public String translateOrEmpty(CommandSender target, String path, String... holders) {
        return translateOrEmpty(target, path, true, holders);
    }

    public String translateOrEmpty(CommandSender target, String path, boolean placeholderApi, String... holders) {
        String result = translate(target, path, placeholderApi, holders);
        if (result == null) {
            return "";
        }
        return result;
    }

    public void send(CommandSender target, String path, String... holders) {
        Util.sendMessage(target, translate(target, path, holders));
    }

    private List<YMLConfig> getLocaleFiles(String fullLocale) {
        fullLocale = fullLocale.toLowerCase();
        if (fallbackList.containsKey(fullLocale)) {
            return fallbackList.get(fullLocale);
        }
        List<YMLConfig> configs = new ArrayList<>();
        List<String> locales = loadLocales(fullLocale);
        for (int i = 0; i < locales.size(); i++) {
            YMLConfig conf = loadFile(locales.get(i), i == locales.size() - 1);
            if (conf != null) {
                configs.add(conf);
            }
        }
        fallbackList.put(fullLocale, configs);
        return configs;
    }

    private List<String> loadLocales(String fullLocale) {
        List<String> locales = new ArrayList<>();
        if (fullLocale.matches("^[A-Za-z0-9_]+$")) {
            locales.add(fullLocale);

            if (fullLocale.contains("_")) {
                locales.add(fullLocale.split("_")[0]);
            }
        }
        if (!locales.contains(getDefaultLocale())) {
            locales.add(getDefaultLocale());
        }
        return locales;
    }

    private YMLConfig loadFile(String locale, boolean forced) {
        locale = locale.toLowerCase(Locale.ENGLISH);
        if (configurations.containsKey(locale)) {
            return configurations.get(locale);
        }
        String fileName = "languages" + File.separator + locale + ".yml";
        if (forced
                || new File(plugin.getDataFolder(), fileName).exists()
                || plugin.getResource("languages/" + locale + ".yml") != null) {
            YMLConfig conf = new YMLConfig(plugin, fileName);
            configurations.put(locale, conf);
            return conf;
        }
        return null;
    }
}
