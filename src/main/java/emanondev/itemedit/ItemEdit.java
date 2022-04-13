package emanondev.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.*;
import emanondev.itemedit.gui.Gui;
import emanondev.itemedit.gui.GuiHandler;
import emanondev.itemedit.storage.PlayerStorage;
import emanondev.itemedit.storage.ServerStorage;
import emanondev.itemedit.storage.YmlPlayerStorage;
import emanondev.itemedit.storage.YmlServerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ItemEdit extends APlugin {
    private static ItemEdit plugin = null;
    public static final String NMS_VERSION = getNmsver();
    public static final int GAME_MAIN_VERSION = Integer.parseInt(NMS_VERSION.split("_")[0].substring(1));
    public static final int GAME_VERSION = Integer.parseInt(NMS_VERSION.split("_")[1]);
    public static final int GAME_SUB_VERSION = Integer.parseInt(NMS_VERSION.split("_")[2].substring(1));

    public static ItemEdit get() {
        return plugin;
    }

    public void onLoad() {
        plugin = this;
    }

    private final static int PROJECT_ID = 40993;

    private static String getNmsver() {
        String txt = Bukkit.getServer().getClass().getPackage().getName();
        return txt.substring(txt.lastIndexOf(".") + 1);
    }

    @Override
    public Integer getProjectId() {
        return PROJECT_ID;
    }

    public void enable() {
        Aliases.reload();

        Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);

        pStorage = new YmlPlayerStorage(); //may implement more type of storage and allow selecting storage type from config
        sStorage = new YmlServerStorage();

        registerCommand(new ItemEditCommand(), Collections.singletonList("ie"));
        registerCommand(new ItemStorageCommand(), Collections.singletonList("is"));
        registerCommand(new ServerItemCommand(), Collections.singletonList("si"));
        new ReloadCommand(this).register();
        registerCommand("itemeditimport", new ItemEditImportCommand(), null);

        //hooks
        if (Hooks.isPAPIEnabled()) {
            new PlaceHolders().register();
        }
        if (Hooks.isShopGuiPlusEnabled()) {
            try {
                new ShopGuiPlusItemProvider().register();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

    }

    public void disable() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof Gui)
                p.closeInventory();
    }

    public void reload() {
        Aliases.reload();
        ItemEditCommand.get().reload();
        ItemStorageCommand.get().reload();
        ServerItemCommand.get().reload();
        getPlayerStorage().reload();
        getServerStorage().reload();
    }

    private PlayerStorage pStorage;

    public PlayerStorage getPlayerStorage() {
        return pStorage;
    }

    private ServerStorage sStorage;

    public ServerStorage getServerStorage() {
        return sStorage;
    }

}
