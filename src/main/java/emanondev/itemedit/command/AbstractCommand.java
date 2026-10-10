package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.Translator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractCommand implements TabExecutor {

    private final String PATH;
    @Getter
    private final String name;
    @Getter
    private final APlugin plugin;
    private final YMLConfig config;
    private final List<SubCmd> subCmds = new ArrayList<>();
    private final HelpSubCommand helpSubCommand;

    /**
     * Creates an AbstractCommand with help support disabled.
     *
     * @param name   command name (used as label)
     * @param plugin plugin instance
     */
    public AbstractCommand(@NotNull String name, @NotNull APlugin plugin) {
        this(name, plugin, false);
    }

    /**
     * Creates an AbstractCommand with an optional paginated help system.
     *
     * @param name          command name (used as label)
     * @param plugin        plugin instance
     * @param multiPageHelp whether to enable multi-page help system
     */
    public AbstractCommand(@NotNull String name, @NotNull APlugin plugin, boolean multiPageHelp) {
        this.name = name.toLowerCase(Locale.ENGLISH);
        this.plugin = plugin;
        this.PATH = getName();
        config = plugin.getConfig("commands.yml");
        if (multiPageHelp) {
            helpSubCommand = new HelpSubCommand(this);
        } else {
            helpSubCommand = null;
        }
    }

    /**
     * Reloads the command's configuration and all registered sub-commands.
     */
    public void reload() {
        config.reload();
        for (SubCmd sub : subCmds) {
            sub.reload();
        }
        if (helpSubCommand != null) {
            helpSubCommand.reload();
        }
    }

    /**
     * Returns sub-commands available to a specific sender based on permission.
     *
     * @param sender The command sender
     * @return A list of sub-commands the sender has permission to use
     */
    public @NotNull List<SubCmd> getAllowedSubCommands(@NotNull CommandSender sender) {
        List<SubCmd> list = new ArrayList<>();
        subCmds.forEach(sub -> {
            if (sender.hasPermission(sub.getPermission())) {
                list.add(sub);
            }
        });
        if (helpSubCommand != null && !subCmds.isEmpty()) {
            list.add(helpSubCommand);
        }
        return list;
    }

    /**
     * Registers a sub-command.
     *
     * @param sub the sub-command to register
     */
    public void registerSubCommand(@NotNull SubCmd sub) {
        subCmds.add(sub);
    }

    /**
     * Registers a sub-command from a supplier.
     * Catches and logs exceptions thrown by the supplier.
     *
     * @param sub the supplier of a sub-command
     * @return true if registration was successful
     */
    public boolean registerSubCommand(@NotNull Supplier<SubCmd> sub) {
        try {
            SubCmd subCommand = sub.get();
            if (subCommand != null) {
                subCmds.add(subCommand);
                return true;
            }
        } catch (Throwable t) {
            log.warn(t.getMessage(), t);
        }
        return false;
    }

    /**
     * Registers a sub-command conditionally.
     *
     * @param sub       the supplier of a sub-command
     * @param condition if true, the command will be registered
     * @return true if the command was registered
     */
    public boolean registerSubCommand(@NotNull Supplier<SubCmd> sub, boolean condition) {
        if (!condition) {
            return false;
        }
        return registerSubCommand(sub);
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        SubCmd subCmd = args.length > 0 ? getSubCmd(args[0], sender) : null;
        if (validateRequires(subCmd, sender, label)) {
            try {
                subCmd.onCommand(sender, label, args);
            } catch (Throwable t) {
                subCmd.onFail(sender, label);
                Util.logCommandError(this, args, sender);
                getPlugin().getLogger().info(
                        String.join("\n",
                                Arrays.stream(t.getStackTrace()).map(StackTraceElement::toString).toList()));
                t.printStackTrace();
            }
        }
        return true;
    }

    public void sendPermissionLackMessage(@NotNull String permission, @NotNull CommandSender sender) {
        getPlugin().getTranslator().send(sender, "lack-permission", "%permission%", permission);
    }

    public void sendPermissionLackGenericMessage(@NotNull CommandSender sender) {
        getPlugin().getTranslator().send(sender, "lack-permission-generic");
    }

    public void sendPlayerOnly(@NotNull CommandSender sender) {
        getPlugin().getTranslator().send(sender, "player-only");
    }

    public void sendNoItemInHand(@NotNull CommandSender sender) {
        getPlugin().getTranslator().send(sender, "no-item-on-hand");
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> l = new ArrayList<>();

        if (args.length == 1) {
            completeCmd(l, args[0], sender);
            return l;
        }
        if (args.length > 1) {
            SubCmd subCmd = getSubCmd(args[0], sender);
            if (subCmd != null && sender.hasPermission(subCmd.getPermission())) {
                l = subCmd.onComplete(sender, args);
            }
        }
        return l;
    }

    public SubCmd getSubCmd(@NotNull String cmd, @NotNull CommandSender sender) {
        for (SubCmd subCmd : subCmds) {
            if (subCmd.getName().equalsIgnoreCase(cmd)) {
                return subCmd;
            }
        }
        if (helpSubCommand != null && helpSubCommand.getName().equalsIgnoreCase(cmd) && !getAllowedSubCommands(sender).isEmpty()) {
            return helpSubCommand;
        }
        return null;
    }

    public void completeCmd(@NotNull List<String> l,
                            @NotNull String prefix,
                            @NotNull CommandSender sender) {
        String text = prefix.toLowerCase(Locale.ENGLISH);
        getAllowedSubCommands(sender).forEach((cmd) -> {
            if (cmd.getName().startsWith(text)) {
                l.add(cmd.getName());
            }
        });
    }

    public PluginCommand getCommand() {
        return Objects.requireNonNull(plugin.getCommand(getName()));
    }

    protected String getLanguageString(String path, CommandSender sender, String... holders) {
        return MiniMessage.miniMessage().serialize(
                getPlugin().getTranslator().translateOrEmpty(sender, this.PATH + "." + path, holders));
    }

    @Contract("null,_,_-> false")
    private boolean validateRequires(@Nullable SubCmd sub, @NotNull CommandSender sender, @NotNull String alias) {
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
            ItemStack item = ItemUtils.getHandMainItem((Player) sender);
            if (ItemUtils.isAirOrNull(item)) {
                sendNoItemInHand(sender);
                return false;
            }
        }
        return true;
    }

    private void help(@NotNull CommandSender sender, @NotNull String alias) {
        if (helpSubCommand != null) {
            helpSubCommand.help(sender, alias, 1);
            return;
        }
        Component message = Component.empty()
                .append(getPlugin().getTranslator().translateOrEmpty(sender, PATH + ".help-header"));
        boolean any = false;
        for (SubCmd cmd : subCmds) {
            if (sender.hasPermission(cmd.getPermission())) {
                any = true;
                message = message.append(Component.newline()).append(cmd.getHelpComponent(sender, alias));
            }
        }
        if (any) {
            Util.sendMessage2(sender, message);
        } else {
            sendPermissionLackGenericMessage(sender);
        }
    }

    private class HelpSubCommand extends SubCmd {

        private int commandPerPage;

        public HelpSubCommand(@NotNull AbstractCommand cmd) {
            super("help", cmd, false, false);
            this.commandPerPage = Math.max(4, this.getConfigInt("commands_per_page"));
        }

        @Override
        public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (Exception ignored) {
                    SubCmd sub = getSubCmd(args[1], sender);
                    if (sub != null) {
                        help(sender, alias, sub);
                        return;
                    }
                }
            }
            help(sender, alias, page);
        }

        public void help(CommandSender sender, String alias, SubCmd sub) {
            Component message = Component.empty()
                    .append(this.translateOrEmpty("header-sub", sender, "%sub%", sub.getName()))
                    .append(Component.newline())
                    .append(sub.getHelpComponent(sender, alias))
                    .append(Component.newline())
                    .append(sub.getDescription(sender));
            Util.sendMessage2(sender, message);
        }

        public void help(CommandSender sender, String alias, int page) {
            List<SubCmd> cmds = getAllowedSubCommands(sender);
            if (cmds.isEmpty()) {
                sendPermissionLackGenericMessage(sender);
                return;
            }
            int maxPage = getMaxPageFor(cmds.size());
            page = Math.max(1, Math.min(maxPage, page));

            Component body = Component.empty()
                    .append(this.translateOrEmpty("header", sender))
                    .append(Component.newline());
            for (SubCmd cmd : cmds.subList(commandPerPage * (page - 1), Math.min(cmds.size(), commandPerPage * page))) {
                body = body.append(cmd.getHelpComponent(sender, alias)).append(Component.newline());
            }
            body = body.append(injectClickablePages(sender, alias, page, maxPage));
            Util.sendMessage2(sender, body);
        }

        @Override
        public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
            if (args.length != 2) {
                return List.of();
            }
            ArrayList<String> tabs = new ArrayList<>();
            List<SubCmd> subs = getAllowedSubCommands(sender);
            for (int i = 0; i < getMaxPageFor(subs.size()); i++) {
                tabs.add(String.valueOf(i + 1));
            }
            for (SubCmd sub : subs) {
                tabs.add(sub.getName());
            }
            return CompleteUtility.complete(args[1], tabs);
        }

        public void reload() {
            super.reload();
            this.commandPerPage = Math.max(4, this.getConfigInt("commands_per_page"));
        }

        private int getMaxPageFor(int elements) {
            return elements / commandPerPage + (elements % commandPerPage == 0 ? 0 : 1);
        }

        private Component injectClickablePages(CommandSender sender,
                                               String alias, int page, int maxPage) {
            Translator translator = getPlugin().getTranslator();
            String currentPage = String.valueOf(page);
            String lastPage = String.valueOf(maxPage);
            Component footer = translateOrEmpty("footer", sender,
                    "%page%", currentPage, "%max_page%", lastPage);
            Component previous = page > 1
                    ? pageControl(sender, translator, "prev", alias, page, page - 1, maxPage)
                    : translator.translateOrEmpty(sender, "generic.help.prev_void",
                    "%page%", currentPage, "%max_page%", lastPage);
            Component next = page < maxPage
                    ? pageControl(sender, translator, "next", alias, page, page + 1, maxPage)
                    : translator.translateOrEmpty(sender, "generic.help.next_void",
                    "%page%", currentPage, "%max_page%", lastPage);

            return footer.replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%prev_clickable%")
                            .replacement(previous)
                            .build())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%next_clickable%")
                            .replacement(next)
                            .build());
        }

        private Component pageControl(CommandSender sender, Translator translator, String direction,
                                      String alias, int page, int targetPage, int maxPage) {
            Component text = translator.translateOrEmpty(sender, "generic.help." + direction + "_text",
                    "%target%", String.valueOf(targetPage), "%page%", String.valueOf(page));
            Component hover = translator.translateOrEmpty(sender, "generic.help." + direction + "_hover",
                    "%target%", String.valueOf(targetPage),
                    "%page%", String.valueOf(page),
                    "%max_page%", String.valueOf(maxPage));
            return Util.asHover(Util.asExecuteCommand(text,
                    "/" + alias + " " + getName() + " " + targetPage), hover);
        }

    }

}
