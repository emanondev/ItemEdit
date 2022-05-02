package emanondev.itemedit;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 1;

    static void update() {
        ItemEdit plugin = ItemEdit.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;
        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);

    }
}
