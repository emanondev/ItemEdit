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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

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

            AbstractCommand c = new ItemEditCommand();
            getCommand(c.getName()).setExecutor(c);
            getCommand(c.getName()).setTabCompleter(c);
            getCommand(c.getName()).setAliases(Collections.singletonList("ie"));
            c = new ItemStorageCommand();
            getCommand(c.getName()).setExecutor(c);
            getCommand(c.getName()).setTabCompleter(c);
            getCommand(c.getName()).setAliases(Collections.singletonList("is"));
            c = new ServerItemCommand();
            getCommand(c.getName()).setExecutor(c);
            getCommand(c.getName()).setTabCompleter(c);
            getCommand(c.getName()).setAliases(Collections.singletonList("si"));
            TabExecutor tabExec = new ItemEditReloadCommand();
            getCommand("itemeditreload").setExecutor(tabExec);
            getCommand("itemeditreload").setTabCompleter(tabExec);
            tabExec = new ItemEditImportCommand();
            getCommand("itemeditimport").setExecutor(tabExec);
            getCommand("itemeditimport").setTabCompleter(tabExec);

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
                if (Class.forName("org.spigotmc.SpigotConfig") == null)
                    throw new NullPointerException();
            } catch (Throwable t) {
                TabExecutorError exec = new TabExecutorError(
                        ChatColor.RED + "CraftBukkit is not supported!!! use Spigot or Paper");

                for (String command : this.getDescription().getCommands().keySet()) {
                    getCommand(command).setExecutor(exec);
                    getCommand(command).setTabCompleter(exec);
                }
                return;
            }
            if (Bukkit.getServer().getBukkitVersion().startsWith("1.7.")) {
                TabExecutorError exec = new TabExecutorError(ChatColor.RED + "1.7.x is not supported!!! use 1.8+");
                for (String command : this.getDescription().getCommands().keySet()) {
                    getCommand(command).setExecutor(exec);
                    getCommand(command).setTabCompleter(exec);
                }
                return;
            }

            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading ItemEdit, disabling it");
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private class TabExecutorError implements TabExecutor {

        private final String msg;

        public TabExecutorError(String msg) {
            this.msg = msg;
            for (int i = 0; i < 20; i++)
                ItemEdit.this.log(msg);
        }

        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
            return Collections.emptyList();
        }

        @Override
        public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
            sender.sendMessage(msg);
            return true;
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
