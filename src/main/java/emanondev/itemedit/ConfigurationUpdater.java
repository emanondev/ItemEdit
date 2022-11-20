package emanondev.itemedit;

import java.util.Arrays;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 2;

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
        }


        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}
