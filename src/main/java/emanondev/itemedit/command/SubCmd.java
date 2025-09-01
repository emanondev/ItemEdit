package emanondev.itemedit.command;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class SubCmd {

    public final String ID;
    @Getter
    private final String permission;
    private final String PATH;
    private final YMLConfig config;
    @Getter
    private final boolean playerOnly;
    private final boolean checkNonNullItem;
    private final AbstractCommand command;
    @Getter
    private @NotNull String name;

    public SubCmd(@NotNull String id, @NotNull AbstractCommand command, boolean playerOnly, boolean checkNonNullItem) {
        if (id.isEmpty() || id.contains(" "))
            throw new IllegalArgumentException();
        this.ID = id.toLowerCase(Locale.ENGLISH);
        this.command = command;
        this.playerOnly = playerOnly;
        this.checkNonNullItem = checkNonNullItem;
        this.PATH = getCommand().getName() + "." + this.ID + ".";
        config = this.getPlugin().getConfig("commands.yml");
        load();
        this.permission = this.getPlugin().getName().toLowerCase(Locale.ENGLISH) + "."
                + command.getName() + "." + this.ID;
    }

    public @NotNull AbstractCommand getCommand() {
        return command;
    }

    public @NotNull APlugin getPlugin() {
        return command.getPlugin();
    }

    public boolean checkNonNullItem() {
        return this.checkNonNullItem;
    }

    protected @NotNull ItemStack getItemInHand(@NotNull Player p) {
        return ItemUtils.getHandItem(p);
    }

    protected void setItemInHand(@NotNull Player p, ItemStack item) {
        ItemUtils.setHandItem(p, item);
    }

    private void load() {
        name = this.getConfigString("name").toLowerCase(Locale.ENGLISH);
        if (name.isEmpty() || name.contains(" ")) {
            name = ID;
        }
    }

    public void reload() {
        load();
    }

    protected BaseComponent[] craftFailFeedback(String alias, String params, List<String> desc) {
        if (params == null) {
            params = "";
        }
        ComponentBuilder fail = new ComponentBuilder(ChatColor.RED + "/" + alias + " " + this.name + " " + params)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + alias + " " + this.name + " " + params));
        if (desc != null && !desc.isEmpty()) {
            fail.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder(String.join("\n", desc)).create()));
        }
        return fail.create();
    }

    protected void sendFeedbackForSub(CommandSender target,
                                      @NotNull String subSubCommand,
                                      String... holders) {
        sendCustomFeedbackForSub(target, subSubCommand, "feedback", holders);
    }

    protected void sendCustomFeedbackForSub(CommandSender target,
                                      @NotNull String subSubCommand,
                                      @NotNull String feedbackPath,
                                      String... holders) {
        Util.sendMessage(target, this.getLanguageString(subSubCommand + "." + feedbackPath, null, target, holders));
    }

    protected void sendFailFeedbackForSub(CommandSender target, String alias, String subSubCommand) {
        String params = getLanguageString(subSubCommand + ".params", null, target);
        target.spigot().sendMessage(this.craftFailFeedback(alias, subSubCommand
                        + ((params == null || params.isEmpty()) ? "" : " " + params),
                getLanguageStringList(subSubCommand + ".description", null, target)));
    }


    protected <T> void onWrongAlias(CommandSender sender, IAliasSet<T> set, String... holders) {
        YMLConfig language = ItemEdit.get().getLanguageConfig(sender);
        String msg = language.getMessage("generic.wrongalias."+set.getID(),null, holders);
        if (msg == null || msg.isEmpty()) {
            return;
        }
        StringBuilder hover = new StringBuilder(language
                .getMessage("generic.wrongalias.error-pre-hover", "")).append("\n");

        String color1 = language.getMessage("generic.wrongalias.first_color", "");
        String color2 = language.getMessage("generic.wrongalias.second_color", "");
        boolean color = true;
        int counter = 0;
        for (T value : set.getValues()) {
            String alias = set.getName(value);
            counter += alias.length() + 1;
            hover.append(color ? color1 : color2).append(alias);
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover.append("\n");
            } else {
                hover.append(" ");
            }
        }
        Util.sendMessage(sender, new ComponentBuilder(msg).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + ItemEditCommand.get().getName() + " "
                                + ItemEdit.get().getConfig("commands.yml")
                                .getString("itemedit.listaliases.name") + " " + set.getID()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover.toString()).create())).create());//TODO fix
    }

    @Deprecated
    protected <T> void onWrongAlias(String pathMessage, CommandSender sender, IAliasSet<T> set, String... holders) {
        String msg = getLanguageString(pathMessage, null, sender, holders);
        if (msg == null || msg.isEmpty()) {
            return;
        }
        YMLConfig language = ItemEdit.get().getLanguageConfig(sender);
        StringBuilder hover = new StringBuilder(language
                .getMessage("wrongalias.error-pre-hover", "")).append("\n");

        String color1 = language.getMessage("wrongalias.first_color", "");
        String color2 = language.getMessage("wrongalias.second_color", "");
        boolean color = true;
        int counter = 0;
        for (T value : set.getValues()) {
            String alias = set.getName(value);
            counter += alias.length() + 1;
            hover.append(color ? color1 : color2).append(alias);
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover.append("\n");
            } else {
                hover.append(" ");
            }
        }
        Util.sendMessage(sender, new ComponentBuilder(msg).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + ItemEditCommand.get().getName() + " "
                                + ItemEdit.get().getConfig("commands.yml")
                                .getString("itemedit.listaliases.name") + " " + set.getID()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover.toString()).create())).create());//TODO fix
    }

    protected String getLanguageString(String path, String def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMessage(this.PATH + path, def == null ? "" : def,
                sender instanceof Player ? (Player) sender : null, true, holders);
    }

    protected void sendLanguageString(String path, String def, CommandSender sender, String... holders) {
        Util.sendMessage(sender, getLanguageString(path, def, sender, holders));
    }

    protected List<String> getLanguageStringList(String path, List<String> def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMultiMessage(this.PATH + path,
                def == null ? new ArrayList<>() : def, sender instanceof Player ? (Player) sender : null, true, holders);
    }

    protected String getConfigString(String path, String... holders) {
        return config.loadMessage(this.PATH + path, "", null, true, holders);
    }

    protected int getConfigInt(String path) {
        return config.loadInteger(this.PATH + path, 0);
    }

    protected long getConfigLong(String path) {
        return config.loadLong(this.PATH + path, 0L);
    }

    protected boolean getConfigBoolean(String path) {
        return config.loadBoolean(this.PATH + path, true);
    }

    protected List<String> getConfigStringList(String path, String... holders) {
        return config.loadMultiMessage(this.PATH + path, new ArrayList<>(), null, true, holders);
    }

    @SuppressWarnings("deprecation")
    public @NotNull ComponentBuilder getHelp(@NotNull ComponentBuilder base, @NotNull CommandSender sender, @NotNull String alias) {
        String help = ChatColor.DARK_GREEN + "/" + alias + " " + ChatColor.GREEN + this.name + " ";
        base.append(help + getLanguageString("params", "", sender).replace(ChatColor.RESET.toString(),
                        ChatColor.GREEN.toString()))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, ChatColor.stripColor(help)))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(getDescription(sender)).create()));
        return base;
    }

    public void onFail(@NotNull CommandSender target, @NotNull String alias) {
        String params = getLanguageString("params", "", target);

        Util.sendMessage(target, new ComponentBuilder(
                ChatColor.RED + "/" + alias + " " + this.name + " " +
                        ChatColor.stripColor(params))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + alias + " " + this.name + " " + ChatColor.stripColor(params)))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,//TODO fix
                        new ComponentBuilder(getDescription(target)).create()))
                .create());
    }

    protected String getDescription(@NotNull CommandSender target) {
        return String.join("\n", getLanguageStringList("description", null, target));
    }

    abstract public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args);

    abstract public List<String> onComplete(@NotNull CommandSender sender, String[] args);

    protected void updateView(@NotNull Player player) {
        InventoryUtils.updateView(player);
    }

}
