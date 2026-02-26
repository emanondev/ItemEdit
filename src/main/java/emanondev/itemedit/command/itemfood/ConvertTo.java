package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class ConvertTo extends SubCmd {
    public ConvertTo(ItemFoodCommand itemFoodCommand) {
        super("convertto", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length > 3) {
            onFail(player, alias);
            return;
        }
        ItemStack target = null;
        if (args.length >= 2) {
            String name = args[1];
            target = ItemEdit.get().getServerStorage().getItem(name);
            if (target == null) {
                try {
                    Material mat = Material.valueOf(name.toUpperCase(Locale.ENGLISH));
                    if (!ItemUtils.isItem(mat)) {
                        throw new IllegalArgumentException();
                    }
                    target = new ItemStack(mat);
                } catch (IllegalArgumentException e2) {
                    onFail(player, alias);
                    return;
                }
            }
        }
        int amount = 1;
        if (args.length == 3) {
            amount = Integer.parseInt(args[2]);
        }
        if (target != null) {
            if (target.getType().isAir()) {
                target = null;
            } else {
                target.setAmount(amount);
            }
        }
        setItemInHand(player, new ItemBuilder(getItemInHand(player)).setConvertsTo(target).build());
        onSuccess(player);
        updateView(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> list2 = CompleteUtility.complete(args[1], Material.class);
            list2.addAll(CompleteUtility.complete(args[1], ItemEdit.get().getServerStorage().getIds()));
            return list2;
        }
        return List.of();
    }
}
