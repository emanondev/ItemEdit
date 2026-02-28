package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class Glow extends SubCmd {

    public Glow(ItemEditCommand cmd) {
        super("glow", cmd, true, true);
    }

    //ie glow <true/false/default>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));

        if (args.length > 2) {
            onFail(p, alias);
            return;
        }
        Boolean value = args.length == 1 ? null : Aliases.BOOLEAN.convertAlias(args[1]);
        if (value == null && args.length == 2) {
            onFail(p, alias);
            return;
        }

        item.setEnchantmentGlintOverride(value).build();
        if (value == null) {
            sendFeedback(p, "feedback-reset");
        } else {
            onSuccess(p, "%value%", args[1].toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return CompleteUtility.complete(args[1], Aliases.BOOLEAN);
    }
}