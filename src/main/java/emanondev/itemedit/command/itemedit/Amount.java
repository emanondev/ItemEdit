package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Amount extends SubCmd {

    public Amount(final ItemEditCommand cmd) {
        super("amount", cmd, true, true);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong param number");
            }
            int amount = Integer.parseInt(args[1]);
            if (amount < 0) { //remove this amount
                item.setAmount(Math.max(0, item.getAmount() + amount));
            }else if ((amount > 127) || (amount < 1)) {
                throw new IllegalArgumentException("Wrong amount number");
            }else {
                item.setAmount(amount);
            }
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        return switch (args.length){
            case 2 -> CompleteUtility.complete(args[1], Arrays.asList("1", "10", "64", "100", "127"));
            default -> List.of();
        };
    }
}