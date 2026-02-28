package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class Enchant extends SubCmd {
    public Enchant(ItemEditCommand cmd) {
        super("enchant", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (args.length != 2 && args.length != 3) {
            onFail(p, alias);
            return;
        }
        int lv = 1;
        Enchantment ench = Aliases.ENCHANT.convertAlias(args[1]);
        if (ench == null) {
            onWrongAlias(p, Aliases.ENCHANT);
            onFail(p, alias);
            return;
        }
        try {
            if (args.length == 3) {
                lv = Integer.parseInt(args[2]);
            }
            if (lv == 0) {
                item.removeEnchantment(ench).build();
                sendFeedback(p, "feedback-removed");
            } else {
                if (!p.hasPermission(this.getPermission() + ".bypass_max_level")) {
                    lv = Math.min(ench.getMaxLevel(), lv);
                }
                item.setEnchantment(ench, lv).build();
                onSuccess(p);
            }
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], Aliases.ENCHANT);
            case 3 -> {
                Enchantment ench = Aliases.ENCHANT.convertAlias(args[2]);
                yield ench == null ? List.of() : IntStream.rangeClosed(0, ench.getMaxLevel())
                        .mapToObj(String::valueOf).toList();
            }
            default -> List.of();
        };
    }

}
