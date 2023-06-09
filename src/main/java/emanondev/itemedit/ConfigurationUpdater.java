package emanondev.itemedit;

import java.util.Arrays;
import java.util.Locale;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 5;

    static void update() {
        ItemEdit plugin = ItemEdit.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;

        if (version <= 1) {
            YMLConfig lang = ItemEdit.get().getLanguageConfig(null);
            lang.set("gui.middleclick.creative", "Middle Click");
            lang.set("gui.middleclick.other", "Press 1");
            if (Arrays.asList("&6&lNext Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.previous-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lPrevious Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));
            if (Arrays.asList("&6&lPrevious Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.next-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lNext Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));

            if (Arrays.asList("&6Pattern Controller", "", "&7[&fMiddle Click&7] &bToggle pattern",
                            "&7[&fLeft/Right Click&7]&b << Move pattern >>")
                    .equals(lang.get("gui.banner.buttons.position")))
                lang.set("gui.banner.buttons.position", Arrays.asList("&6Pattern Controller", "", "&7[&f%middle_click%&7] &bToggle pattern",
                        "&7[&fLeft/Right Click&7]&b << Move pattern >>"));

            if (Arrays.asList("&bEffect Controller", "", "&7[&fMiddle Click&7]&b Toggle effect",
                            "&7[&fLeft/Right Click&7]&b Move this effect")
                    .equals(lang.get("gui.firework.buttons.position")))
                lang.set("gui.firework.buttons.position", Arrays.asList("&bEffect Controller", "", "&7[&f%middle_click%&7]&b Toggle effect",
                        "&7[&fLeft/Right Click&7]&b Move this effect"));
            lang.set("gui.colorselector.title", "&9Choose colors");
            lang.set("gui.colorselector.buttons.color", Arrays.asList("&6Colors:", "&e%colors%"
                    , "&7[&fRight Click&7]&b Add &e%color%", "&7[&fLeft Click&7]&b Remove last color"
                    , "&7[&fMiddle Click&7]&b Clear colors"));
            lang.set("gui.firework.buttons.colors", Arrays.asList("&6Colors Selector"
                    , "&bColors: &e%colors%"
                    , ""
                    , "&7[&fClick&7]&b to change colors"));
            lang.set("gui.firework.buttons.fadecolors", Arrays.asList("&6Fade Colors Selector"
                    , "&bFade Colors: &e%colors%"
                    , ""
                    , "&7[&fClick&7]&b to change colors"));
            lang.save();
        }
        if (version <= 2) {
            YMLConfig conf = ItemEdit.get().getLanguageConfig(null);
            conf.set("itemedit.goathornsound.wrong-type", "&4[&cItemEdit&4] &cItem must be a goat horn");
            conf.set("itemedit.goathornsound.wrong-sound", "&4[&cItemEdit&4] &cWrong Sound Value! &4[&6hover here&4]");
            conf.set("itemedit.goathornsound.description", Arrays.asList("&b&lSet Goat Horn sound", ""
                    , "&e<type> &bthe type of sound"));
            conf.set("itemedit.goathornsound.params", "<type>");
            conf.save();

            conf = ItemEdit.get().getConfig("commands.yml");
            conf.set("itemedit.goathornsound.name", "goathornsound");
            conf.save();

            conf = ItemEdit.get().getConfig("aliases.yml");
            conf.set("goat_horn_sound.sing_goat_horn", "sing");
            conf.set("goat_horn_sound.call_goat_horn", "call");
            conf.set("goat_horn_sound.dream_goat_horn", "dream");
            conf.set("goat_horn_sound.yearn_goat_horn", "yearn");
            conf.set("goat_horn_sound.ponder_goat_horn", "ponder");
            conf.set("goat_horn_sound.admire_goat_horn", "admire");
            conf.set("goat_horn_sound.feel_goat_horn", "feel");
            conf.set("goat_horn_sound.seek_goat_horn", "seek");
            conf.save();
        }
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
            conf = ItemEdit.get().getLanguageConfig(null);
            conf.set("itemedit.armortrim.wrong-type", "&4[&cItemEdit&4] &cItem must be an armor");
            conf.set("itemedit.armortrim.wrong-material", "&4[&cItemEdit&4] &cWrong Trim Material Value! &4[&6hover here&4]");
            conf.set("itemedit.armortrim.wrong-pattern", "&4[&cItemEdit&4] &cWrong Trim Pattern Value! &4[&6hover here&4]");
            conf.set("itemedit.armortrim.description", Arrays.asList("&b&lSet Armor Trim", "",
                    "&e<material> &bthe trim material", "&e<pattern> &bthe trim pattern"));
            conf.set("itemedit.armortrim.params", "<material> <pattern>");
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

        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}
