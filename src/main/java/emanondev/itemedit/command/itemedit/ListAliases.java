package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListAliases extends SubCmd {

    public ListAliases(ItemEditCommand cmd) {
        super("listaliases", cmd, false, false);
    }

    // ie listaliases [type]
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        switch (args.length) {
            case 1 -> oneArg(sender, alias, args);
            case 2 -> twoArgs(sender, alias, args);
            default -> onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.getTypes().keySet());
        }
        return List.of();
    }

    private void oneArg(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Component prefix = translate("prefix_line", sender);
        Component postfix = translate("postfix_line", sender);
        Component colorOne = translate("first_color", sender);
        Component colorTwo = translate("second_color", sender);
        Component hover = translate("hover_type", sender);
        Component feedback = Component.empty();
        if (Util.hasRenderableContent(prefix)) {
            feedback = feedback.append(prefix).appendNewline();
        }
        boolean counter = true;
        List<String> values = new ArrayList<>(Aliases.getTypes().keySet());
        Collections.sort(values);
        for (String id : values) {
            Component label = counter ? colorOne : colorTwo;
            if (label == null) {
                label = Component.empty();
            }
            Component clickable = Util.asSuggestCommand(label.append(Component.text(id)),
                    "/" + alias + " " + this.getName() + " " + id);
            feedback = feedback.append(Util.asHover(clickable, hover)).appendSpace();
            counter = !counter;
        }
        if (Util.hasRenderableContent(postfix)) {
            feedback = feedback.appendNewline().append(postfix);
        }
        Util.sendMessage2(sender, feedback);
    }

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    private void twoArgs(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        IAliasSet set = Aliases.getTypes().get(args[1].toLowerCase(Locale.ENGLISH));
        if (set == null) {
            this.onFail(sender, alias);
            return;
        }

        Component prefix = translate("prefix_line", sender);
        Component postfix = translate("postfix_line", sender);
        Component colorOne = translate("first_color", sender);
        Component colorTwo = translate("second_color", sender);
        Component feedback = Component.empty();
        if (Util.hasRenderableContent(prefix)) {
            feedback = feedback.append(prefix).appendNewline();
        }
        boolean counter = true;
        for (String aliasS : (List<String>) set.getAliases()) {
            Component label = counter ? colorOne : colorTwo;
            if (label == null) {
                label = Component.empty();
            }
            String defaultName = set.getName(set.convertAlias(aliasS));
            Component hover = translate("hover_info", sender, "%default%", defaultName);
            Component clickable = label.append(Component.text(aliasS));
            feedback = feedback.append(Util.asHover(clickable, hover)).appendSpace();
            counter = !counter;
        }
        if (Util.hasRenderableContent(postfix)) {
            feedback = feedback.appendNewline().append(postfix);
        }
        Util.sendMessage2(sender, feedback);
    }

}
