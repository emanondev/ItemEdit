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
import emanondev.itemedit.utility.InventoryUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.function.Predicate;

public class ItemEdit extends APlugin {

    private static ItemEdit plugin = null;
    @Getter
    private PlayerStorage playerStorage;
    @Getter
    private ServerStorage serverStorage;
    @Nullable
    private MongoStorage mongoStorage;

    public static ItemEdit get() {
        return plugin;
    }

    @Override
    public void onLoad() {
        plugin = this;
    }

    @NotNull
    public StorageType getStorageType() { //TODO if invalid return YAML, maybe i should add a feedback
        return StorageType.byName(this.getConfig().load("storage.type", "", String.class))
                .orElse(StorageType.YAML);
    }

    @Override
    public void enable() {
        if (Util.hasMiniMessageAPI()) {
            ItemEdit.get().log("Hooking into <rainbow>MiniMessageAPI</rainbow><white> see https://webui.advntr.dev/");
        }
        Aliases.reload();
        Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);

        StorageType storageType = getStorageType();
        log("Selected Storage Type: " + storageType.name());
        switch (storageType) {
            case YAML:
                this.playerStorage = new YmlPlayerStorage();
                this.serverStorage = new YmlServerStorage();
                break;
            case MONGODB: {
                String connectionString = this.getConfig().load("storage.mongodb.uri", "mongodb://127.0.0.1:27017", String.class);
                String database = this.getConfig().load("storage.mongodb.database", "itemedit", String.class);
                String collectionPrefix = this.getConfig().load("storage.mongodb.collection_prefix", "itemedit-", String.class);

                this.mongoStorage = new MongoStorage(connectionString, database, collectionPrefix);
                this.playerStorage = new MongoPlayerStorage(this.mongoStorage, this.getLogger());
                this.serverStorage = new MongoServerStorage(this.mongoStorage);
                break;
            }
            default: {
                this.enableWithError("Selected storage type is invalid, please fix it: open plugins/ItemEdit/config.yml and set storage: -> type: 'YAML' then restart the server");
                return;
            }
        }

        initCommands();
        initHooks();

    }

    @Override
    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (InventoryUtils.getTopInventory(p).getHolder() instanceof Gui)
                p.closeInventory();

        if (this.mongoStorage != null) this.mongoStorage.close();
    }

    @Override
    public void reload() {
        Aliases.reload();
        ItemEditCommand.get().reload();
        ItemStorageCommand.get().reload();
        ServerItemCommand.get().reload();
        getPlayerStorage().reload();
        getServerStorage().reload();
    }

    @Override
    protected void updateConfigurations(int oldConfigVersion) {
        YMLConfig conf = this.getConfig();
        YMLConfig aliases = this.getConfig("aliases.yml");
        if (oldConfigVersion <= 3) {
            conf.set("check-updates", true);
        }
        if (oldConfigVersion <= 4) {
            conf.set("storage.type", "YAML");
            conf.set("storage.mongodb.uri", "mongodb://127.0.0.1:27017");
            conf.set("storage.mongodb.database", "itemedit");
            conf.set("storage.mongodb.collection_prefix", "itemedit");

            for (String name : new String[]{"quartz", "redstone", "emerald", "copper", "iron", "lapis",
                    "diamond", "gold", "netherite", "amethyst"})
                aliases.set("trim_material.minecraft:" + name.toLowerCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH));
            for (String name : new String[]{"rib", "snout", "wild", "coast", "spire", "wayfinder", "shaper", "tide",
                    "silence", "vex", "sentry", "dune", "raiser", "eye", "host", "ward"})
                aliases.set("trim_pattern.minecraft:" + name.toLowerCase(Locale.ENGLISH), name.toLowerCase(Locale.ENGLISH));
        }

        if (oldConfigVersion <= 6) {
            conf.set("blocked.lore-line-limit", 16);
        }

        if (oldConfigVersion <= 7) {
            aliases.set("attribute.generic_max_absorption", "max_absorption");
        }
    }

    @Override
    protected boolean addLanguagesMetrics() {
        return true;
    }

    @Override
    @NotNull
    protected Predicate<Player> languagesMetricsIsAdmin() {
        return p -> p.hasPermission("itemedit.admin");
    }

    @Override
    @NotNull
    protected Predicate<Player> languagesMetricsIsUser() {
        return p -> p.hasPermission("itemedit.creativeuser")
                || p.hasPermission("itemedit.itemedit.rename")
                || p.hasPermission("itemedit.itemedit.lore")
                || p.hasPermission("itemedit.itemedit.color");
    }

    private void initHooks() {
        if (Hooks.isPAPIEnabled()) {
            try {
                this.log("Hooking into PlaceHolderAPI");
                new Placeholders().register();
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
    }

    private void initCommands() {
        registerCommand(new ItemEditCommand(), Collections.singletonList("ie"));
        registerCommand(new ItemStorageCommand(), Collections.singletonList("is"));
        registerCommand(new ServerItemCommand(), Collections.singletonList("si"));
        registerCommand("itemeditinfo", new ItemEditInfoCommand(), null);
        new ReloadCommand(this).register();
        registerCommand("itemeditimport", new ItemEditImportCommand(), null);
        //TODO add a command to change storage type (aka conversion)
    }
}
