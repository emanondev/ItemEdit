package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.IntParser;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Damage extends SubCmd {

    public Damage(ItemEditCommand cmd) {
        super("damage", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInMainHand(p));
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        IntParser amount = new IntParser(args[1]);
        if (!amount.isNumberMin(0)) {
            onFail(p, alias);
            return;
        }
        item.setDamage(amount.getValue()).build();
        updateView(p);
        if (amount.getValue() == 0) {
            sendFeedback(p, "feedback-reset");
        } else {
            onSuccess(p);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2 || !(sender instanceof Player player)) {
            return List.of();
        }
        ItemStack item = getItemInMainHand(player);
        if (ItemUtils.isAirOrNull(item)) {
            return List.of();
        }
        int max = item.getType().getMaxDurability();
        if (max <= 1) {
            return List.of();
        }
        return CompleteUtility.complete(args[1],
                "0", String.valueOf(max), String.valueOf(max / 2),
                String.valueOf(max / 4), String.valueOf((max * 3) / 4)
        );
    }

}
