package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.IntParser;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MaxStackSize extends SubCmd {

    public MaxStackSize(ItemEditCommand cmd) {
        super("maxstacksize", cmd, true, true);
    }

    //ie MaxStackSize <1-99/default>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInMainHand(p));
        if (args.length > 2) {
            onFail(sender, alias);
            return;
        }
        IntParser value = args.length == 1 ? null : new IntParser(args[1]);
        if (value != null && !value.isNumberInRange(1, 99)) {
            onFail(p, alias);
            return;
        }
        item.setMaxStackSize(value==null?null:value.getValue()).build();
        if (value != null) {
            onSuccess(p, "%value%", String.valueOf(value.getValue()));
        } else {
            sendFeedback(p, "feedback-reset");
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return CompleteUtility.complete(args[1], "1", "32", "64", "99");
    }
}