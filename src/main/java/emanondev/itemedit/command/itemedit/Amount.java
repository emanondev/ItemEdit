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

    public Amount(ItemEditCommand cmd) {
        super("amount", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");
            int amount = Integer.parseInt(args[1]);
            if (amount < 0) //remove this amount
                item.setAmount(Math.max(0, item.getAmount() + amount));
            else if ((amount > 127) || (amount < 1))
                throw new IllegalArgumentException("Wrong amount number");
            else
                item.setAmount(amount);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Arrays.asList("1", "10", "64", "100", "127"));
        return Collections.emptyList();
    }
}