package emanondev.itemedit.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand implements TabExecutor {

    private final String name;
    private final APlugin plugin;
    private final YMLConfig config;

    public final @NotNull String getName() {
        return name;
    }

    @Deprecated
    public AbstractCommand(@NotNull String name) {
        this.name = name.toLowerCase();
        this.plugin = ItemEdit.get();
        config = ItemEdit.get().getConfig(getName() + ".yml");
    }


    public AbstractCommand(@NotNull String name,@NotNull APlugin plugin) {
        this.name = name.toLowerCase();
        this.plugin = plugin;
        config = plugin.getConfig(getName() + ".yml");
    }

    public final @NotNull APlugin getPlugin() {
        return plugin;
    }

    private final List<SubCmd> cmds = new ArrayList<>();

    public void reload() {
        config.reload();
        for (SubCmd sub : cmds)
            sub.reload();
    }

    public void registerSubCommand(@NotNull SubCmd sub) {
        cmds.add(sub);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        SubCmd subCmd = args.length > 0 ? getSubCmd(args[0]) : null;
        if (!validateRequires(subCmd, sender))
            return true;
        subCmd.onCmd(sender, args);
        return true;
    }

    public String getPermissionLackMessage(@NotNull String permission) {
        return config.loadString("lack-permission", "&cYou lack of permission %permission%", true)
                .replace("%permission%", permission);
    }

    private boolean validateRequires(SubCmd sub,@NotNull CommandSender sender) {
        if (sub == null) {
            help(sender);
            return false;
        }

        if (!sender.hasPermission(sub.getPermission())) {
            Util.sendMessage(sender, getPermissionLackMessage(sub.getPermission()));
            return false;
        }
        if (sub.isPlayerOnly() && !(sender instanceof Player)) {
            Util.sendMessage(sender, config.loadString("player-only", "&cCommand for Players only", true));
            return false;
        }
        if (sub.isPlayerOnly() && sub.checkNonNullItem()) {

            @SuppressWarnings("deprecation")
            ItemStack item = ((Player) sender).getInventory().getItemInHand();
            if (item == null || item.getType() == Material.AIR) {

                Util.sendMessage(sender,
                        config.loadString("no-item-on-hand", "&cYou need to hold an item in hand", true));
                return false;
            }
        }
        return true;
    }

    private void help(CommandSender sender) {
        ComponentBuilder help = new ComponentBuilder(
                config.loadString("help-header", "&3&l" + getName() + " - Help", true) + "\n");
        boolean c = false;
        for (int i = 0; i < cmds.size(); i++) {
            if (sender.hasPermission(cmds.get(i).getPermission())) {
                if (c)
                    help.append("\n");
                else
                    c = true;
                help = cmds.get(i).getHelp(help);
            }
        }
        if (c)
            Util.sendMessage(sender, help.create());
        else
            Util.sendMessage(sender, config.loadString("lack-permission-generic",
                    "&cYou don't have permission to use this command", true));
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        List<String> l = new ArrayList<>();

        if (args.length == 1) {
            completeCmd(l, args[0], sender);
            return l;
        }
        if (args.length > 1) {
            SubCmd subCmd = getSubCmd(args[0]);
            if (subCmd != null && sender.hasPermission(subCmd.getPermission()))
                l = subCmd.complete(sender, args);
        }
        return l;
    }

    public SubCmd getSubCmd(String cmd) {
        for (int i = 0; i < cmds.size(); i++) {
            if (cmds.get(i).getName().equalsIgnoreCase(cmd))
                return cmds.get(i);
        }
        return null;
    }

    public void completeCmd(List<String> l, String prefix, CommandSender sender) {
        final String text = prefix.toLowerCase();
        cmds.forEach((cmd) -> {
            if (cmd.getName().startsWith(text) && sender.hasPermission(cmd.getPermission()))
                l.add(cmd.getName());
        });
    }

}
