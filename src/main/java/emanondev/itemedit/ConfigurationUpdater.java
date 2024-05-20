package emanondev.itemedit;

import java.util.Locale;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 8;

    static void update() {
        ItemEdit plugin = ItemEdit.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;
        if (version <= 3) {
            plugin.getConfig().set("check-updates", true);
        }
        if (version <= 4) {
            YMLConfig conf = plugin.getConfig();
            conf.set("storage.type", "YAML");
            conf.set("storage.mongodb.uri", "mongodb://127.0.0.1:27017");
            conf.set("storage.mongodb.database", "itemedit");
            conf.set("storage.mongodb.collection_prefix", "itemedit");
            conf.save();

            conf = ItemEdit.get().getConfig("aliases.yml");
            for (String name : new String[]{"quartz", "redstone", "emerald", "copper", "iron", "lapis",
                    "diamond", "gold", "netherite", "amethyst"})
                conf.set("trim_material.minecraft:" + name.toLowerCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH));
            for (String name : new String[]{"rib", "snout", "wild", "coast", "spire", "wayfinder", "shaper", "tide",
                    "silence", "vex", "sentry", "dune", "raiser", "eye", "host", "ward"})
                conf.set("trim_pattern.minecraft:" + name.toLowerCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH));
            conf.save();
        }

        if (version <= 6) {
            YMLConfig conf = ItemEdit.get().getConfig();
            conf.set("blocked.lore-line-limit", 16);
            conf.save();
        }

        if (version <= 7) {
            YMLConfig conf = ItemEdit.get().getConfig("aliases.yml");
            conf.set("attribute.generic_max_absorption", "max_absorption");
            conf.save();
        }

        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}
