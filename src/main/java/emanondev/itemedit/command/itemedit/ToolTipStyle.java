package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class ToolTipStyle extends SubCmd {

    public ToolTipStyle(ItemEditCommand cmd) {
        super("tooltipstyle", cmd, true, true);
    }

    //ie tooltipstyle <style/clear>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length != 2) {
            onFail(sender, alias);
            return;
        }

        String value = args[1].toLowerCase(Locale.ENGLISH);
        if (value.equals("clear")) {

            item.setTooltipStyle(null).build();
            sendFeedback(p, "feedback-reset");
            return;
        }
        String pre;
        String post;
        if (!value.contains(":")) {
            pre = NamespacedKey.MINECRAFT;
            post = value;
        } else {
            pre = value.split(":")[0];
            post = value.split(":")[1];
        }
        item.setTooltipStyle(new NamespacedKey(pre, post)).build();
        onSuccess(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], "clear");
        }
        return List.of();
    }
}