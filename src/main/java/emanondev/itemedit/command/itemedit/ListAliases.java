package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.aliases.IAliasSet;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
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
        String prefix = translate("prefix_line", sender);
        String postfix = translate("postfix_line", sender);
        String colorOne = translate("first_color", sender);
        String colorTwo = translate("second_color", sender);
        String hover = translate("hover_type", sender);
        StringBuilder feedback = new StringBuilder((prefix != null && !prefix.isEmpty()) ? prefix + "\n" : "");
        boolean counter = true;
        List<String> values = new ArrayList<>(Aliases.getTypes().keySet());
        Collections.sort(values);
        for (String id : values) {
            feedback.append(
                    Util.asHover(Util.asSuggestCommand(
                                    (counter ? colorOne : colorTwo) + id,
                                    "/" + alias + " " + this.getName() + " " + id),
                            hover)
            ).append(" ");
            counter = !counter;
        }
        if (postfix != null && !postfix.isEmpty()) {
            feedback.append("\n").append(postfix);
        }
        Util.sendMessage(sender, feedback.toString());
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

        StringBuilder feedback = new StringBuilder((prefix != null && !prefix.isEmpty()) ? prefix + "\n" : "");
        boolean counter = true;
        for (String aliasS : (List<String>) set.getAliases()) {
            feedback.append(Util.asHover((counter ? colorOne : colorTwo) + aliasS,
                            hover.replace("%default%", set.getName(set.convertAlias(aliasS)))))
                    .append(" ");
            counter = !counter;
        }
        if (postfix != null && !postfix.isEmpty()) {
            feedback.append("\n").append(postfix);
        }
        Util.sendMessage(sender, feedback.toString());
    }

}