package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
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

    private void oneArg(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        String prefix = translate("prefix_line", sender);
        String postfix = translate("postfix_line", sender);
        String colorOne = translate("first_color", sender);
        String colorTwo = translate("second_color", sender);
        String hover = translate("hover_type", sender);
        ComponentBuilder comp;
        if (prefix != null && !prefix.isEmpty()) {
            comp = new ComponentBuilder(prefix + "\n");
        } else {
            comp = new ComponentBuilder("");
        }
        boolean counter = true;
        List<String> values = new ArrayList<>(Aliases.getTypes().keySet());
        Collections.sort(values);
        for (String id : values) {
            comp.retain(FormatRetention.NONE).append((counter ? colorOne : colorTwo) + id)
                    .event(Util.craftHoverEvent(hover))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/" + alias + " " + this.getName() + " " + id))
                    .append(" ");
            counter = !counter;
        }
        if (postfix != null && !postfix.isEmpty()) {
            comp.retain(FormatRetention.NONE).append("\n" + postfix);
        }
        Util.sendMessage(sender, comp.create());
    }

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    private void twoArgs(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        IAliasSet set = Aliases.getTypes().get(args[1].toLowerCase(Locale.ENGLISH));
        if (set == null) {
            this.onFail(sender, alias);
            return;
        }

        String prefix = translate("prefix_line", sender);
        String postfix = translate("postfix_line", sender);
        String colorOne = translate("first_color", sender);
        String colorTwo = translate("second_color", sender);
        String hover = translate("hover_info", sender, "%default%", "%default%");
        ComponentBuilder comp;
        if (prefix != null && !prefix.isEmpty()) {
            comp = new ComponentBuilder(prefix + "\n");
        } else {
            comp = new ComponentBuilder("");
        }
        boolean counter = true;
        for (String aliasS : (List<String>) set.getAliases()) {
            comp.retain(FormatRetention.NONE).append((counter ? colorOne : colorTwo) + aliasS)
                    .event(Util.craftHoverEvent(
                            hover.replace("%default%", set.getName(set.convertAlias(aliasS)))))
                    .append(" ");
            counter = !counter;
        }
        if (postfix != null && !postfix.isEmpty()) {
            comp.retain(FormatRetention.NONE).append("\n" + postfix);
        }
        Util.sendMessage(sender, comp.create());
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.getTypes().keySet());
        }
        return List.of();
    }

}