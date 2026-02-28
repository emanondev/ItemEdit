package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RepairCost extends SubCmd {

    public RepairCost(ItemEditCommand cmd) {
        super("repaircost", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length > 2) {
            onFail(sender, alias);
            return;
        }
        if (!sender.hasPermission(this.getPermission() + ".without_durability") && item.getType().getMaxDurability() <= 1) {
            this.getCommand().sendPermissionLackMessage(this.getPermission() + ".without_durability", sender);
            return;
        }
        int amount = Integer.parseInt(args[1]);
        item.setRepairCost(amount).build();
        onSuccess(sender);
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Arrays.asList("0", "1", "3", "7", "30", "40"));
        }
        return List.of();
    }

}
