package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomModelData extends SubCmd {

    public CustomModelData(ItemEditCommand cmd) {//1.14+
        super("custommodeldata", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (args.length != 1 && args.length != 2) {
            onFail(p, alias);
            return;
        }
        try {
            Integer amount = args.length == 1 ? null : Integer.parseInt(args[1]);
            if (amount != null && amount < 0) {
                onFail(p, alias);
                return;
            }
            item.setCustomModelData(amount).build();
            if (amount != null) {
                onSuccess(p);
            } else {
                sendFeedback(p, "feedback-reset");
            }
            updateView(p);
        } catch (NumberFormatException e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], List.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        }
        return List.of();
    }

}