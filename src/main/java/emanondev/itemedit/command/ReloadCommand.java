package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
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
    private final String permission;

    public ReloadCommand(APlugin plugin) {
        this.plugin = plugin;
        this.permission = plugin.getName().toLowerCase() + "." + plugin.getName().toLowerCase() + "reload";
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return new ArrayList<>();
    }

    public void sendPermissionLackMessage(@NotNull String permission, CommandSender sender) {
        Util.sendMessage(sender, plugin.getLanguageConfig(sender).loadMessage("lack-permission", "&cYou lack of permission %permission%",
                sender instanceof Player ? (Player) sender : null, true
                , "%permission%", permission));
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender.hasPermission(permission)) {
            plugin.onReload();
            Util.sendMessage(sender, plugin.getLanguageConfig(sender).loadMessage(plugin.getName().toLowerCase() + "reload.success", "", true));
        } else
            sendPermissionLackMessage(permission, sender);
        return true;
    }

    public void register() {
        plugin.registerCommand(plugin.getName().toLowerCase() + "reload", this, null);
    }
}
