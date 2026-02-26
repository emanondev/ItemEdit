package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Amount extends SubCmd {

    public Amount(ItemEditCommand cmd) {
        super("amount", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length != 2) {
            onFail(p, alias);
        }
        try {
            int amount = Integer.parseInt(args[1]);
            if (amount < 0) {//remove this amount
                item.setAmount(Math.max(0, item.getAmount() + amount));
                sendFeedback(p, "feedback-decrease",
                        "%amount%", String.valueOf(Math.abs(amount)));
            } else if ((amount > 127) || (amount < 1)) {
                onFail(p, alias);
                return;
            } else {
                item.setAmount(amount);
                onSuccess(p, "%amount%", String.valueOf(Math.abs(amount)));
            }
            updateView(p);
        } catch (NumberFormatException e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], List.of("1", "10", "64", "100", "127"));
        }
        return List.of();
    }
}