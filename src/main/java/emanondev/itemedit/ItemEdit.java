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
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;

public class ItemEdit extends APlugin {
    private static ItemEdit plugin = null;
    public static final String NMS_VERSION = getNmsver();

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

    public void onEnable() {
        try {
            Aliases.reload();

            Bukkit.getPluginManager().registerEvents(new GuiHandler(), this);

            pStorage = new YmlPlayerStorage(); //may implement more type of storage and allow selecting storage type from config
            sStorage = new YmlServerStorage();

            registerCommand(new ItemEditCommand(), Collections.singletonList("ie"));
            registerCommand(new ItemStorageCommand(), Collections.singletonList("is"));
            registerCommand(new ServerItemCommand(), Collections.singletonList("si"));
            registerCommand("itemeditreload", new ItemEditReloadCommand(), null);
            registerCommand("itemeditimport", new ItemEditImportCommand(), null);

            getConfig(); //force load the config.yml file

            new UpdateChecker(this, PROJECT_ID).logUpdates();
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

        } catch (Throwable e) {
            try {
                Class.forName("org.spigotmc.SpigotConfig");
            } catch (Throwable t) {
                TabExecutorError exec = new TabExecutorError(
                        ChatColor.RED + "CraftBukkit is not supported!!! use Spigot or Paper");
                for (String command : this.getDescription().getCommands().keySet())
                    registerCommand(command, exec, null);
                return;
            }
            if (Bukkit.getServer().getBukkitVersion().startsWith("1.7.")) {
                TabExecutorError exec = new TabExecutorError(ChatColor.RED + "1.7.x is not supported!!! use 1.8+");
                for (String command : this.getDescription().getCommands().keySet())
                    registerCommand(command, exec, null);
                return;
            }
            this.log(ChatColor.RED + "Error while loading ItemEdit, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers())
            if (p.getOpenInventory().getTopInventory().getHolder() instanceof Gui)
                p.closeInventory();
    }

    public void reload() {
        long now = System.currentTimeMillis();
        reloadConfigs();
        Aliases.reload();
        ItemEditCommand.get().reload();
        ItemStorageCommand.get().reload();
        ServerItemCommand.get().reload();
        getPlayerStorage().reload();
        getServerStorage().reload();
        log(ChatColor.GREEN, "#", "Reloaded (took &e" + (System.currentTimeMillis() - now) + "&f ms)");
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
