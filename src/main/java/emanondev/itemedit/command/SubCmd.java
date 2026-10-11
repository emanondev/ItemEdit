package emanondev.itemedit.command;

import emanondev.itemedit.*;
import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.Translator;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public abstract class SubCmd {

    @Getter
    private final String id;
    @Getter
    private final String permission;
    private final String PATH;
    private final YMLConfig config;
    @Getter
    private final boolean playerOnly;
    private final boolean checkNonNullItem;
    @Getter
    private @NotNull
    final AbstractCommand command;
    @Getter
    private @NotNull String name;

    public SubCmd(@NotNull String id, @NotNull AbstractCommand command, boolean playerOnly, boolean checkNonNullItem) {
        if (id.isEmpty() || id.contains(" "))
            throw new IllegalArgumentException();
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.command = command;
        this.playerOnly = playerOnly;
        this.checkNonNullItem = checkNonNullItem;
        this.PATH = getCommand().getName() + "." + this.id + ".";
        config = this.getPlugin().getConfig("commands.yml");
        load();
        this.permission = this.getPlugin().getName().toLowerCase(Locale.ENGLISH) + "."
                + command.getName() + "." + this.id;
    }

    public @NotNull APlugin getPlugin() {
        return getCommand().getPlugin();
    }

    public boolean checkNonNullItem() {
        return this.checkNonNullItem;
    }

    public void reload() {
        load();
    }

    public @NotNull String getHelp(@NotNull CommandSender sender, @NotNull String alias) {
        return MiniMessage.miniMessage().serialize(getHelpComponent(sender, alias));
    }

    public @NotNull Component getHelpComponent(@NotNull CommandSender sender, @NotNull String alias) {
        String params = translateOrEmptyRaw("params", sender);
        Component help = getPlugin().getTranslator().translate(sender, "generic.help.command-format",
                "%command%", alias,
                "%sub%", this.name,
                "%params%", params);
        return Util.asHover(Util.asSuggestCommand(help,
                "/" + alias + " " + this.name + " "), getDescription(sender));
    }

    public void onFail(@NotNull CommandSender target, @NotNull String alias) {
        Component params = translateOrEmpty("params", target);
        String paramsText = params == null ? "" : MiniMessage.miniMessage().serialize(params);
        Component feedback = Util.asHover(Util.asSuggestCommand(
                        MiniMessage.miniMessage().deserialize("<red>/" + alias + " " + this.name + " " + paramsText),
                        "/" + alias + " " + this.name + " " + paramsText),
                getDescription(target));
        Util.sendComponent(target, feedback);
    }

    abstract public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args);

    abstract public List<String> onComplete(@NotNull CommandSender sender, String[] args);

    protected @NotNull ItemStack getItemInMainHand(@NotNull Player p) {
        return ItemUtils.getHandMainItem(p);
    }

    protected void setItemInHand(@NotNull Player p, ItemStack item) {
        ItemUtils.setHandMainItem(p, item);
    }

    protected Component craftFailFeedback(String alias, String params, List<Component> desc) {
        if (params == null) {
            params = "";
        }
        Component hover = null;
        if (desc != null) {
            List<Component> lines = desc.stream().filter(java.util.Objects::nonNull).toList();
            if (!lines.isEmpty()) {
                hover = Component.join(net.kyori.adventure.text.JoinConfiguration.newlines(), lines);
            }
        }
        Component text = MiniMessage.miniMessage().deserialize(
                "<red>/" + alias + " " + this.name + " " + params);
        return Util.asHover(Util.asSuggestCommand(text, "/" + alias + " " + this.name + " " + params), hover);
    }

    protected void onSubFail(CommandSender target, String alias, String subSubCommand) {
        Component params = translate(subSubCommand + ".params", target);
        String paramsText = params == null ? "" : PlainTextComponentSerializer.plainText().serialize(params);
        Util.sendComponent(target, craftFailFeedback(alias, subSubCommand
                        + ((!Util.hasRenderableContent(params)) ? "" : " " + paramsText),
                translateList(subSubCommand + ".description", target)));
    }

    protected <T> void onWrongAlias(CommandSender sender, IAliasSet<T> set) {
        Translator pluginTranslator = getPlugin().getTranslator();
        Translator itemeditTranslator = ItemEdit.get().getTranslator();
        Component msg = pluginTranslator.translate(sender, "generic.wrong-alias." + set.getId(),
                "%prefix%", getPlugin().getTranslator().translateOrEmptyRaw(sender, "generic.help.prefix"));
        if (!Util.hasRenderableContent(msg)) {
            return;
        }
        Component hover = itemeditTranslator.translate(sender, "generic.wrong-alias.error-pre-hover")
                .append(Component.newline());

        Component color1 = itemeditTranslator.translate(sender, "generic.wrong-alias.first_color");
        Component color2 = itemeditTranslator.translate(sender, "generic.wrong-alias.second_color");
        boolean color = true;
        int counter = 0;
        for (T value : set.getValues()) {
            String alias = set.getName(value);
            counter += alias.length() + 1;
            hover = hover.append(color ? color1 : color2).append(Component.text(alias));
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover = hover.append(Component.newline());
            } else {
                hover = hover.append(Component.space());
            }
        }
        String command = "/" + ItemEditCommand.get().getName() + " "
                + ItemEdit.get().getConfig("commands.yml")
                .getString("itemedit.listaliases.name") + " " + set.getId();
        sender.sendMessage(Util.asHover(Util.asSuggestCommand(msg, command), hover));
    }

    @Deprecated
    protected <T> void onWrongAlias(String pathMessage, CommandSender sender, IAliasSet<T> set, String... holders) {
        Component msg = translate(pathMessage, sender, holders);
        if (!Util.hasRenderableContent(msg)) {
            return;
        }
        Translator translator = getPlugin().getTranslator();
        Component hover = translator.translateOrEmpty(sender, "wrong-alias.error-pre-hover")
                .append(Component.newline());

        Component color1 = translator.translateOrEmpty(sender, "wrong-alias.first_color");
        Component color2 = translator.translateOrEmpty(sender, "wrong-alias.second_color");
        boolean color = true;
        int counter = 0;
        for (T value : set.getValues()) {
            String alias = set.getName(value);
            counter += alias.length() + 1;
            hover = hover.append(color ? color1 : color2).append(Component.text(alias));
            color = !color;
            if (counter > 30) {
                counter = 0;
                hover = hover.append(Component.newline());
            } else {
                hover = hover.append(Component.space());
            }
        }
        String command = "/" + ItemEditCommand.get().getName() + " "
                + ItemEdit.get().getConfig("commands.yml")
                .getString("itemedit.listaliases.name") + " " + set.getId();

        sender.sendMessage(Util.asHover(Util.asSuggestCommand(msg, command), hover));
    }

    protected Component translate(String path, CommandSender sender, String... holders) {
        String[] prefix = new String[]{"%prefix%", getPlugin().getTranslator().translateOrEmptyRaw(sender, "generic.help.prefix")};
        holders = holders == null ? prefix : Stream.concat(Stream.of(holders), Stream.of(prefix)).toArray(String[]::new);
        return getPlugin().getTranslator().translate(sender, this.PATH + path, holders);
    }

    protected Component translateOrEmpty(String path, CommandSender sender, String... holders) {
        String[] prefix = new String[]{"%prefix%", getPlugin().getTranslator().translateOrEmptyRaw(sender, "generic.help.prefix")};
        holders = holders == null ? prefix : Stream.concat(Stream.of(holders), Stream.of(prefix)).toArray(String[]::new);
        return getPlugin().getTranslator().translateOrEmpty(sender, this.PATH + path, holders);
    }

    protected String translateOrEmptyRaw(String path, CommandSender sender, String... holders) {
        return getPlugin().getTranslator().translateOrEmptyRaw(sender, this.PATH + path, holders);
    }

    protected void onSuccess(CommandSender sender, String... holders) {
        Util.sendComponent(sender, translate("feedback", sender, holders));
    }

    protected void onSubSuccess(CommandSender sender, String subSubCommand, String... holders) {
        Util.sendComponent(sender, translate(subSubCommand + ".feedback", sender, holders));
    }

    protected void sendFeedback(CommandSender target,
                                @NotNull String feedbackPath,
                                String... holders) {
        Util.sendComponent(target, this.translate(feedbackPath, target, holders));
    }

    protected void sendSubFeedback(CommandSender target,
                                   @NotNull String subSubCommand,
                                   @NotNull String feedbackPath,
                                   String... holders) {
        Util.sendComponent(target, this.translate(subSubCommand + "." + feedbackPath, target, holders));
    }

    protected void sendLanguageString(String path, CommandSender sender, String... holders) {
        Util.sendComponent(sender, translate(path, sender, holders));
    }

    protected List<Component> translateList(String path, CommandSender sender, String... holders) {
        return getPlugin().getTranslator().translateList(sender, this.PATH + path, holders);
    }

    protected String getConfigString(String path, String... holders) {
        return config.loadMessage(this.PATH + path, "", null, true, holders);
    }

    protected int getConfigInt(String path) {
        return config.loadInteger(this.PATH + path, 0);
    }

    protected Component getDescription(@NotNull CommandSender target) {
        Component description = UtilsString.toSingleComponent(translateList("description", target));
        return description == null ? Component.empty() : description;
    }

    protected void updateView(@NotNull Player player) {
        InventoryUtils.updateView(player);
    }

    private void load() {
        name = this.getConfigString("name").toLowerCase(Locale.ENGLISH);
        if (name.isEmpty() || name.contains(" ")) {
            name = id;
        }
    }

}
