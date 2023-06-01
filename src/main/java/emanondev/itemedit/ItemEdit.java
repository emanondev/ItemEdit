package emanondev.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.*;
import emanondev.itemedit.compability.*;
import emanondev.itemedit.gui.Gui;
import emanondev.itemedit.gui.GuiHandler;
import emanondev.itemedit.storage.mongo.MongoPlayerStorage;
import emanondev.itemedit.storage.mongo.MongoStorage;
import emanondev.itemedit.storage.PlayerStorage;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemedit.storage.StorageType;
import emanondev.itemedit.storage.YmlPlayerStorage;
import emanondev.itemedit.storage.YmlServerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;
import org.jetbrains.annotations.Nullable;

public class ItemEdit extends APlugin {
    private static ItemEdit plugin = null;
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final String NMS_VERSION = getNmsver();
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_MAIN_VERSION = Integer.parseInt(NMS_VERSION.split("_")[0].substring(1));
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_VERSION = Integer.parseInt(NMS_VERSION.split("_")[1]);
    /**
     * @see Util#isVersionUpTo(int, int, int)
     * @see Util#isVersionAfter(int, int, int)
     * @see Util#isVersionInRange(int, int, int, int, int, int)
     */
    @Deprecated
    public static final int GAME_SUB_VERSION = Integer.parseInt(NMS_VERSION.split("_")[2].substring(1));

    public static ItemEdit get() {
        return plugin;
    }

    public void onLoad() {
        plugin = this;
    }

    private final static int PROJECT_ID = 40993;
    private static final int BSTATS_PLUGIN_ID = 15076;

    @Deprecated
    private static String getNmsver() {
        String txt = Bukkit.getServer().getClass().getPackage().getName();
        return txt.substring(txt.lastIndexOf(".") + 1);
    }

    @Override
    public Integer getProjectId() {
        return PROJECT_ID;
    }

    public void enable() {
        ConfigurationUpdater.update();
        Aliases.reload();
        Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);

        StorageType storageType = StorageType.byName(this.getConfig().load("storage.type", "", String.class))
                .orElse(StorageType.YAML);
        this.getLogger().info("Selected Storage Type: " + storageType.name());
        if (storageType == StorageType.YAML) {
            pStorage = new YmlPlayerStorage();
            sStorage = new YmlServerStorage();
        } else if (storageType == StorageType.MONGODB) {
            String connectionString = this.getConfig().load("storage.mongodb.uri", "mongodb://127.0.0.1:27017", String.class);
            String database = this.getConfig().load("storage.mongodb.database", "itemedit", String.class);
            String collectionPrefix = this.getConfig().load("storage.mongodb.collection_prefix", "itemedit-", String.class);

            this.mongoStorage = new MongoStorage(connectionString, database, collectionPrefix);
            this.pStorage = new MongoPlayerStorage(this.mongoStorage, this.getLogger());
            // TODO: Set server storage
        } else {
            this.getLogger().info("You selected an unsupported storage type! Disable player and server storage...");
            // TODO: Disable player and server storage
        }

        registerCommand(new ItemEditCommand(), Collections.singletonList("ie"));
        registerCommand(new ItemStorageCommand(), Collections.singletonList("is"));
        registerCommand(new ServerItemCommand(), Collections.singletonList("si"));
        registerCommand("itemeditinfo", new ItemEditInfoCommand(), null);
        new ReloadCommand(this).register();
        registerCommand("itemeditimport", new ItemEditImportCommand(), null);

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
        registerMetrics(BSTATS_PLUGIN_ID);
    }

    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof Gui)
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
