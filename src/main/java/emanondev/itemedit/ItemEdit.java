package emanondev.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.*;
import emanondev.itemedit.compability.*;
import emanondev.itemedit.gui.Gui;
import emanondev.itemedit.gui.GuiHandler;
import emanondev.itemedit.storage.PlayerStorage;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemedit.storage.StorageType;
import emanondev.itemedit.storage.mongo.MongoPlayerStorage;
import emanondev.itemedit.storage.mongo.MongoServerStorage;
import emanondev.itemedit.storage.mongo.MongoStorage;
import emanondev.itemedit.storage.yaml.YmlPlayerStorage;
import emanondev.itemedit.storage.yaml.YmlServerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class ItemEdit extends APlugin {
    private static ItemEdit plugin = null;

    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_MAIN_VERSION = Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[0]);
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_VERSION = Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[1]);
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_SUB_VERSION = Bukkit.getBukkitVersion().split("-")[0].split("\\.").length < 3 ? 0 : Integer.parseInt(
            Bukkit.getBukkitVersion().split("-")[0].split("\\.")[2]);

    public static ItemEdit get() {
        return plugin;
    }

    public void onLoad() {
        plugin = this;
    }

    private final static int PROJECT_ID = 40993;
    private static final int BSTATS_PLUGIN_ID = 15076;

    @NotNull
    public StorageType getStorageType() { //TODO if invalid return YAML, maybe i should add a feedback
        return StorageType.byName(this.getConfig().load("storage.type", "", String.class))
                .orElse(StorageType.YAML);
    }

    @Override
    public Integer getProjectId() {
        return PROJECT_ID;
    }

    public void enable() {
        ConfigurationUpdater.update();
        Aliases.reload();
        Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);

        StorageType storageType = getStorageType();
        this.getLogger().info("Selected Storage Type: " + storageType.name());
        switch (storageType) {
            case YAML:
                pStorage = new YmlPlayerStorage();
                sStorage = new YmlServerStorage();
                break;
            case MONGODB: {
                String connectionString = this.getConfig().load("storage.mongodb.uri", "mongodb://127.0.0.1:27017", String.class);
                String database = this.getConfig().load("storage.mongodb.database", "itemedit", String.class);
                String collectionPrefix = this.getConfig().load("storage.mongodb.collection_prefix", "itemedit-", String.class);

                this.mongoStorage = new MongoStorage(connectionString, database, collectionPrefix);
                this.pStorage = new MongoPlayerStorage(this.mongoStorage, this.getLogger());
                this.sStorage = new MongoServerStorage(this.mongoStorage);
                break;
            }
            default: {
                this.enableWithError("Selected storage type is invalid, please fix it: open plugins/ItemEdit/config.yml and set storage: -> type: 'YAML' then restart the server");
                return;
            }
        }

        registerCommand(new ItemEditCommand(), Collections.singletonList("ie"));
        registerCommand(new ItemStorageCommand(), Collections.singletonList("is"));
        registerCommand(new ServerItemCommand(), Collections.singletonList("si"));
        registerCommand("itemeditinfo", new ItemEditInfoCommand(), null);
        new ReloadCommand(this).register();
        registerCommand("itemeditimport", new ItemEditImportCommand(), null);
        //TODO add a command to change storage type (aka conversion)

        //hooks
        if (Hooks.isPAPIEnabled()) {
            try {
                this.log("Hooking into PlaceholderApi");
                new PlaceHolders().register();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (Hooks.isShopGuiPlusEnabled()) {
            try {
                this.log("Hooking into ShopGuiPlus");
                new ShopGuiPlusItemProvider().register();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (Hooks.isMythicMobsEnabled()) {
            try {
                this.log("Hooking into MythicMobs");
                registerListener(new MythicMobsListener());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (Hooks.isItemBridgeEnabled()) {
            try {
                this.log("Hooking into ItemBridge");
                ItemBridgeItemProvider.setup(this);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        if (Hooks.isDungeonMMOEnabled()) {
            try {
                this.log("Hooking into DungeonMMO");
                DungeonMMOItemProvider.register();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        registerMetrics(BSTATS_PLUGIN_ID);
    }

    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (UtilLegacy.getTopInventory(p).getHolder() instanceof Gui)
                p.closeInventory();

        if (this.mongoStorage != null) this.mongoStorage.close();
    }

    public void reload() {
        Aliases.reload();
        ItemEditCommand.get().reload();
        ItemStorageCommand.get().reload();
        ServerItemCommand.get().reload();
        getPlayerStorage().reload();
        getServerStorage().reload();
    }

    private @Nullable MongoStorage mongoStorage;

    private PlayerStorage pStorage;

    public PlayerStorage getPlayerStorage() {
        return pStorage;
    }

    private ServerStorage sStorage;

    public ServerStorage getServerStorage() {
        return sStorage;
    }

}
