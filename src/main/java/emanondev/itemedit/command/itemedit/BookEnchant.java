package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class BookEnchant extends SubCmd {
    public BookEnchant(ItemEditCommand cmd) {
        super("bookenchant", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p); //TODO use itemBuilder
        if (item.getType() == Material.BOOK) {
            item.setType(Material.ENCHANTED_BOOK);
        }
        if (!(item.getItemMeta() instanceof EnchantmentStorageMeta)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_enchantment_storage");
            return;
        }
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) ItemUtils.getMeta(item);
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
        } catch (NumberFormatException e) {
            onFail(p, alias);
            return;
        }
        if (lv == 0) {
            meta.removeStoredEnchant(ench);
            sendFeedback(p, "feedback-removed", "%enchant%", args[1]);
        } else {
            if (!p.hasPermission(this.getPermission() + ".bypass_max_level")) {
                lv = Math.min(ench.getMaxLevel(), lv);
                //TODO feedback?
            }
            meta.addStoredEnchant(ench, lv, true);
            onSuccess(p, "%enchant%", args[1], "%lv%", String.valueOf(lv));
        }
        item.setItemMeta(meta);
        updateView(p);
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
