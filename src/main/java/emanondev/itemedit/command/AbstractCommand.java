package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class AbstractCommand implements TabExecutor {

    private final String PATH;
    private final String name;
    private final APlugin plugin;
    private final YMLConfig config;
    private final List<SubCmd> subCmds = new ArrayList<>();
    //private final boolean multiPageHelp;
    private final HelpSubCommand helpSubCommand;

    public AbstractCommand(@NotNull String name, @NotNull APlugin plugin) {
        this(name, plugin, false);
    }

    public AbstractCommand(@NotNull String name, @NotNull APlugin plugin, boolean multiPageHelp) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.plugin = plugin;
        this.PATH = getName();
        config = plugin.getConfig("commands.yml");
        //this.multiPageHelp = multiPageHelp;
        if (multiPageHelp) {
            //HelpSubCommand sub = ;
            //registerSubCommand(sub);
            helpSubCommand = new HelpSubCommand(this);
        } else {
            helpSubCommand = null;
        }
    }

    public final @NotNull String getName() {
        return name;
    }

    public final @NotNull APlugin getPlugin() {
        return plugin;
    }

    public void reload() {
        config.reload();
        for (SubCmd sub : subCmds)
            sub.reload();
        if (helpSubCommand != null)
            helpSubCommand.reload();
    }

    public List<SubCmd> getAllowedSubCommands(CommandSender sender) {
        List<SubCmd> list = new ArrayList<>();
        subCmds.forEach(sub -> {
            if (sender.hasPermission(sub.getPermission()))
                list.add(sub);
        });
        if (helpSubCommand != null && !subCmds.isEmpty())
            list.add(helpSubCommand);
        return list;
    }

    public void registerSubCommand(@NotNull SubCmd sub) {
        subCmds.add(sub);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        SubCmd subCmd = args.length > 0 ? getSubCmd(args[0], sender) : null;
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

    @Contract("null,_,_-> false")
    private boolean validateRequires(SubCmd sub, @NotNull CommandSender sender, String alias) {
        if (sub == null) {
            help(sender, alias);
            return false;
        }

        if (!sender.hasPermission(sub.getPermission()) && sub != helpSubCommand) {
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
            if (Util.isAirOrNull(item)) {
                sendNoItemInHand(sender);
                return false;
            }
        }
        return true;
    }

    private void help(CommandSender sender, String alias) {
        if (helpSubCommand != null) {
            helpSubCommand.help(sender, alias, 1);
            return;
        }
        ComponentBuilder help = new ComponentBuilder(
                this.getLanguageString("help-header", "&3&l" + getName() + " - Help", sender) + "\n");
        boolean c = false;
        for (SubCmd cmd : subCmds) {
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
            SubCmd subCmd = getSubCmd(args[0], sender);
            if (subCmd != null && sender.hasPermission(subCmd.getPermission()))
                l = subCmd.onComplete(sender, args);
        }
        return l;
    }

    public SubCmd getSubCmd(String cmd, CommandSender sender) {
        for (SubCmd subCmd : subCmds) {
            if (subCmd.getName().equalsIgnoreCase(cmd))
                return subCmd;
        }
        if (helpSubCommand != null && helpSubCommand.getName().equalsIgnoreCase(cmd) && !getAllowedSubCommands(sender).isEmpty())
            return helpSubCommand;
        return null;
    }

    public void completeCmd(List<String> l, String prefix, CommandSender sender) {
        final String text = prefix.toLowerCase(Locale.ENGLISH);
        subCmds.forEach((cmd) -> {
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

    private class HelpSubCommand extends SubCmd {

        private int commandPerPage;

        private int getMaxPageFor(int elements) {
            return elements / commandPerPage + (elements % commandPerPage == 0 ? 0 : 1);
        }

        public HelpSubCommand(@NotNull AbstractCommand cmd) {
            super("help", cmd, false, false);
            this.commandPerPage = Math.max(4, this.getConfigInt("commands_per_page"));
        }

        @Override
        public void onCommand(CommandSender sender, String alias, String[] args) {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (Exception ignored) {
                }
            }
            help(sender, alias, page);
        }

        public void help(CommandSender sender, String alias, int page) {
            List<SubCmd> cmds = getAllowedSubCommands(sender);
            if (!cmds.isEmpty()) {
                page = Math.max(1, Math.min(getMaxPageFor(cmds.size()), page));
                ComponentBuilder help = new ComponentBuilder("");
                injectClickablePages(help,
                        this.getLanguageString("header", "&3&l" + getName() + " - Help", sender),
                        sender, alias, page);
                help.append("\n");

                for (SubCmd cmd : cmds.subList(commandPerPage * (page - 1), Math.min(cmds.size(), commandPerPage * page))) {
                    help = cmd.getHelp(help, sender, alias);
                    help.append("\n");
                }
                injectClickablePages(help,
                        this.getLanguageString("footer", "&3&l" + getName() + " - Help", sender),
                        sender, alias, page);
                Util.sendMessage(sender, help.create());
            } else
                sendPermissionLackGenericMessage(sender);
        }

        @SuppressWarnings("deprecation")
        private void injectClickablePages(ComponentBuilder comp, String text, CommandSender sender, String alias, int page) {
            int maxPage = getMaxPageFor(getAllowedSubCommands(sender).size());
            text = text.replace("%page%", String.valueOf(page)).replace("%max_page%", String.valueOf(maxPage));
            int index = text.indexOf("%prev_clickable%");
            String text1;
            String text2;
            if (index != -1) {
                text1 = text.substring(0, index);
                text2 = text.substring(index + "%prev_clickable%".length());
            } else {
                text1 = text;
                text2 = null;
            }
            Bukkit.getConsoleSender().spigot().sendMessage(comp.create());
            Bukkit.getConsoleSender().sendMessage("aaaa");
            String text11;
            String text12;
            String text21 = null;
            String text22 = null;
            index = text1.indexOf("%next_clickable%");
            if (index != -1) {
                text11 = text1.substring(0, index);
                text12 = text1.substring(index + "%next_clickable%".length());
            } else {
                text11 = text1;
                text12 = null;
            }
            if (text2 != null) {
                index = text2.indexOf("%next_clickable%");
                if (index != -1) {
                    text21 = text2.substring(0, index);
                    text22 = text2.substring(index + "%next_clickable%".length());
                } else {
                    text21 = text2;
                }
            }
            comp.retain(ComponentBuilder.FormatRetention.NONE).append(text11);
            if (text12 != null) {
                if (page < maxPage) {
                    comp.append(getLanguageString("next_text", ">>>>", sender,
                                    "%target%", String.valueOf(page + 1), "%page%", String.valueOf(page)))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + alias + " " + getName() + " " + (page + 1)))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            getLanguageString("next_hover", "Go to page %target%", sender,
                                                    "%target%", String.valueOf(page + 1),
                                                    "%page%", String.valueOf(page),
                                                    "%max_page%", String.valueOf(maxPage))

                                    ).create()));
                } else {
                    comp.append(getLanguageString("next_void", ">>>>", sender,
                            "%page%", String.valueOf(page), "%max_page%", String.valueOf(maxPage)));
                }
                comp.append(text12).retain(ComponentBuilder.FormatRetention.FORMATTING);
            }
            if (text21 != null) {
                if (page > 1) {
                    comp.append(getLanguageString("prev_text", "<<<<", sender,
                                    "%target%", String.valueOf(page - 1), "%page%", String.valueOf(page)))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + alias + " " + getName() + " " + (page - 1)))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            getLanguageString("prev_hover", "Go to page %target%", sender,
                                                    "%target%", String.valueOf(page - 1),
                                                    "%page%", String.valueOf(page),
                                                    "%max_page%", String.valueOf(maxPage))
                                    ).create()));
                } else {
                    comp.append(getLanguageString("prev_void", "<<<<", sender,
                            "%page%", String.valueOf(page), "%max_page%", String.valueOf(maxPage)));
                }
                comp.append(text21).retain(ComponentBuilder.FormatRetention.FORMATTING);
            }
            if (text22 != null) {
                if (page < maxPage) {
                    comp.append(getLanguageString("next_text", ">>>>", sender,
                                    "%target%", String.valueOf(page + 1), "%page%", String.valueOf(page)))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + alias + " " + getName() + " " + (page + 1)))
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            getLanguageString("next_hover", "Go to page %target%", sender,
                                                    "%target%", String.valueOf(page + 1),
                                                    "%page%", String.valueOf(page),
                                                    "%max_page%", String.valueOf(maxPage))
                                    ).create()));
                } else {
                    comp.append(getLanguageString("next_void", ">>>>", sender,
                            "%page%", String.valueOf(page), "%max_page%", String.valueOf(maxPage)));
                }
                comp.append(text22).retain(ComponentBuilder.FormatRetention.FORMATTING);
            }
        }

        @Override
        public List<String> onComplete(CommandSender sender, String[] args) {
            ArrayList<String> tabs = new ArrayList<>();
            for (int i = 0; i < getMaxPageFor(getAllowedSubCommands(sender).size()); i++)
                tabs.add(String.valueOf(i + 1));
            return tabs;
        }

        public void reload() {
            super.reload();
            this.commandPerPage = Math.max(4, this.getConfigInt("commands_per_page"));
        }
    }

}
