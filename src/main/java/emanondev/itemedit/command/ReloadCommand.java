package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


/**
 * Utility command, create a reload command for a plugin,<br>
 * plugin must extend APlugin class for reload and onReload methods<br>
 * <br>
 * permission for the command is lowcased (pluginname).(pluginname)reload<br>
 * the command name is (pluginname)reload<br>
 * <br>
 * those info should be added to your plugin.yml
 */
public class ReloadCommand implements TabExecutor {

    private final APlugin plugin;

    public ReloadCommand(APlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission(plugin.getName().toLowerCase()+"."+plugin.getName().toLowerCase()+"reload")) {
            plugin.reload();
            Util.sendMessage(sender, ItemEdit.get().getConfig(plugin.getName().toLowerCase()+"reload.yml").loadString("success", "", true));
        } else
            Util.sendMessage(sender, ItemEdit.get().getConfig(plugin.getName().toLowerCase()+"reload.yml")
                    .loadString("lack-permission", "", true).replace("%permission%", plugin.getName().toLowerCase()+"."+plugin.getName().toLowerCase()+"reload"));
        return true;
    }

    public void register() {
        plugin.registerCommand(plugin.getName().toLowerCase()+"reload",this,null);
    }
}
