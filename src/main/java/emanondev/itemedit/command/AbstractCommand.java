package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCommand implements TabExecutor {

    private final String PATH;
    private final String name;
    private final APlugin plugin;
    private final YMLConfig config;
    private final List<SubCmd> cmds = new ArrayList<>();

    public AbstractCommand(@NotNull String name, @NotNull APlugin plugin) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.plugin = plugin;
        this.PATH = getName();
        config = plugin.getConfig("commands.yml");
    }

    public final @NotNull String getName() {
        return name;
    }

    public final @NotNull APlugin getPlugin() {
        return plugin;
    }

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
        if (!validateRequires(subCmd, sender, label))
            return true;
        subCmd.onCommand(sender, label, args);
        return true;
    }

    public void sendPermissionLackMessage(@NotNull String permission, CommandSender sender) {
        Util.sendMessage(sender, getPlugin().getLanguageConfig(sender).loadMessage("lack-permission", "&cYou lack of permission %permission%",
                sender instanceof Player ? (Player) sender : null, true
                , "%permission%",
                permission));
    }

    public void sendPermissionLackGenericMessage(CommandSender sender) {
        Util.sendMessage(sender, getPlugin().getLanguageConfig(sender).loadMessage("lack-permission-generic",
                "&cYou don't have permission to use this command",
                sender instanceof Player ? (Player) sender : null, true
        ));
    }

    public void sendPlayerOnly(CommandSender sender) {
        Util.sendMessage(sender, getPlugin().getLanguageConfig(sender).loadMessage("player-only", "&cCommand for Players only",
                sender instanceof Player ? (Player) sender : null, true
        ));
    }

    public void sendNoItemInHand(CommandSender sender) {
        Util.sendMessage(sender, getPlugin().getLanguageConfig(sender).loadMessage("no-item-on-hand", "&cYou need to hold an item in hand",
                sender instanceof Player ? (Player) sender : null, true
        ));
    }

    private boolean validateRequires(SubCmd sub, @NotNull CommandSender sender, String alias) {
        if (sub == null) {
            help(sender, alias);
            return false;
        }

        if (!sender.hasPermission(sub.getPermission())) {
            sendPermissionLackMessage(sub.getPermission(), sender);
            return false;
        }
        if (sub.isPlayerOnly() && !(sender instanceof Player)) {
            sendPlayerOnly(sender);
            return false;
        }
        if (sub.isPlayerOnly() && sub.checkNonNullItem()) {
            @SuppressWarnings("deprecation")
            ItemStack item = ((Player) sender).getInventory().getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                sendNoItemInHand(sender);
                return false;
            }
        }
        return true;
    }

    private void help(CommandSender sender, String alias) {
        ComponentBuilder help = new ComponentBuilder(
                this.getLanguageString("help-header", "&3&l" + getName() + " - Help", sender) + "\n");
        boolean c = false;
        for (SubCmd cmd : cmds) {
            if (sender.hasPermission(cmd.getPermission())) {
                if (c)
                    help.append("\n");
                else
                    c = true;
                help = cmd.getHelp(help, sender, alias);
            }
        }
        if (c)
            Util.sendMessage(sender, help.create());
        else
            sendPermissionLackGenericMessage(sender);
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
                l = subCmd.onComplete(sender, args);
        }
        return l;
    }

    public SubCmd getSubCmd(String cmd) {
        for (SubCmd subCmd : cmds) {
            if (subCmd.getName().equalsIgnoreCase(cmd))
                return subCmd;
        }
        return null;
    }

    public void completeCmd(List<String> l, String prefix, CommandSender sender) {
        final String text = prefix.toLowerCase(Locale.ENGLISH);
        cmds.forEach((cmd) -> {
            if (cmd.getName().startsWith(text) && sender.hasPermission(cmd.getPermission()))
                l.add(cmd.getName());
        });
    }

    protected String getLanguageString(String path, String def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMessage(this.PATH + "." + path, def == null ? "" : def,
                sender instanceof Player ? (Player) sender : null, true, holders);
    }

    protected List<String> getLanguageStringList(String path, List<String> def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMultiMessage(this.PATH + "." + path,
                def == null ? new ArrayList<>() : def, sender instanceof Player ? (Player) sender : null, true, holders);
    }

    protected String getConfString(String path) {
        return config.loadMessage(this.PATH + "." + path, "", true);
    }

    protected int getConfInt(String path) {
        return config.loadInteger(this.PATH + "." + path, 0);
    }

    protected long getConfLong(String path) {
        return config.loadLong(this.PATH + "." + path, 0L);
    }

    protected boolean getConfBoolean(String path) {
        return config.loadBoolean(this.PATH + "." + path, true);
    }

}
