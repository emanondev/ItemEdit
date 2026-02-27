package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
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

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        try {
            short amount = Short.parseShort(args[1]);
            amount = (short) Math.max(0, Math.min(amount, item.getType().getMaxDurability()));
            item.setDurability(amount);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2 || !(sender instanceof Player player)) {
            return List.of();
        }
        ItemStack item = getItemInHand(player);
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
