package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.AliasSet;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;

public class ListAliases extends SubCmd {

	public ListAliases(ItemEditCommand cmd) {
		super("listaliases", cmd, false, false);
	}

	// ie listaliases [type]
	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	@Override
	public void onCmd(CommandSender sender, String[] args) {
		try {
			if ((args.length != 1) && (args.length != 2))
				throw new IllegalArgumentException("Wrong param number");
			if (args.length == 1) {
				String prefix = getConfString("prefix_line");
				String postfix = getConfString("postfix_line");
				String colorOne = getConfString("first_color");
				String colorTwo = getConfString("second_color");
				String hover = getConfString("hover_type");
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
									"/ie " + this.getName() + " " + id))
							.append(" ");
					counter = !counter;
				}
				if (postfix != null && !postfix.isEmpty())
					comp.retain(FormatRetention.NONE).append("\n" + postfix);
				Util.sendMessage(sender, comp.create());

			} else {
				AliasSet set = Aliases.getTypes().get(args[1].toLowerCase());
				if (set == null) {
					this.onFail(sender);
					return;
				}

				String prefix = getConfString("prefix_line");
				String postfix = getConfString("postfix_line");
				String colorOne = getConfString("first_color");
				String colorTwo = getConfString("second_color");
				String hover = getConfString("hover_info");
				ComponentBuilder comp;
				if (prefix != null && !prefix.isEmpty())
					comp = new ComponentBuilder(prefix + "\n");
				else
					comp = new ComponentBuilder("");
				boolean counter = true;
				for (String alias : (List<String>) set.getAliases()) {
					comp.retain(FormatRetention.NONE).append((counter ? colorOne : colorTwo) + alias)
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(
											hover.replace("%default%", set.getName(set.convertAlias(alias)))).create()))
							.append(" ");
					counter = !counter;
				}
				if (postfix != null && !postfix.isEmpty())
					comp.retain(FormatRetention.NONE).append("\n" + postfix);
				Util.sendMessage(sender, comp.create());
			}
		} catch (Exception e) {
			onFail(sender);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Aliases.getTypes().keySet());
		return Collections.emptyList();
	}

}