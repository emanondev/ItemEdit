package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.AliasSet;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ListAliases extends SubCmd {

    public ListAliases(ItemEditCommand cmd) {
        super("listaliases", cmd, false, false);
    }

    // ie listaliases [type]
    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        try {
            if ((args.length != 1) && (args.length != 2))
                throw new IllegalArgumentException("Wrong param number");
            if (args.length == 1) {
                String prefix = getLanguageString("prefix_line", null, sender);
                String postfix = getLanguageString("postfix_line", null, sender);
                String colorOne = getLanguageString("first_color", null, sender);
                String colorTwo = getLanguageString("second_color", null, sender);
                String hover = getLanguageString("hover_type", null, sender);
                ComponentBuilder comp;
                if (prefix != null && !prefix.isEmpty())
                    comp = new ComponentBuilder(prefix + "\n");
                else
                    comp = new ComponentBuilder("");
                boolean counter = true;
                for (String id : Aliases.getTypes().keySet()) {
                    comp.retain(FormatRetention.NONE).append((counter ? colorOne : colorTwo) + id)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                    "/" + alias + " " + this.getName() + " " + id))
                            .append(" ");
                    counter = !counter;
                }
                if (postfix != null && !postfix.isEmpty())
                    comp.retain(FormatRetention.NONE).append("\n" + postfix);
                Util.sendMessage(sender, comp.create());

            } else {
                AliasSet set = Aliases.getTypes().get(args[1].toLowerCase());
                if (set == null) {
                    this.onFail(sender, alias);
                    return;
                }

                String prefix = getLanguageString("prefix_line", null, sender);
                String postfix = getLanguageString("postfix_line", null, sender);
                String colorOne = getLanguageString("first_color", null, sender);
                String colorTwo = getLanguageString("second_color", null, sender);
                String hover = getLanguageString("hover_info", null, sender, "%default%", "%default%");
                ComponentBuilder comp;
                if (prefix != null && !prefix.isEmpty())
                    comp = new ComponentBuilder(prefix + "\n");
                else
                    comp = new ComponentBuilder("");
                boolean counter = true;
                for (String aliasS : (List<String>) set.getAliases()) {
                    comp.retain(FormatRetention.NONE).append((counter ? colorOne : colorTwo) + aliasS)
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(
                                            hover.replace("%default%", set.getName(set.convertAlias(aliasS)))).create()))
                            .append(" ");
                    counter = !counter;
                }
                if (postfix != null && !postfix.isEmpty())
                    comp.retain(FormatRetention.NONE).append("\n" + postfix);
                Util.sendMessage(sender, comp.create());
            }
        } catch (Exception e) {
            onFail(sender, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.getTypes().keySet());
        return Collections.emptyList();
    }

}