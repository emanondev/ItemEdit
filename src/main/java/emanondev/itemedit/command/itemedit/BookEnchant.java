package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookEnchant extends SubCmd {
    public BookEnchant(ItemEditCommand cmd) {
        super("bookenchant", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (item.getType() == Material.BOOK)
            item.setType(Material.ENCHANTED_BOOK);
        if (!(item.getItemMeta() instanceof EnchantmentStorageMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }
        try {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) ItemUtils.getMeta(item);
            if (args.length != 2 && args.length != 3)
                throw new IllegalArgumentException("Wrong argument Number");
            int lv = 1;
            Enchantment ench = Aliases.ENCHANT.convertAlias(args[1]);
            if (ench == null) {
                onWrongAlias("wrong-enchant", p, Aliases.ENCHANT);
                onFail(p, alias);
                return;
            }
            if (args.length == 3)
                lv = Integer.parseInt(args[2]);
            if (lv == 0)
                meta.removeStoredEnchant(ench);
            else {
                if (!p.hasPermission(this.getPermission() + ".bypass_max_level"))
                    lv = Math.min(ench.getMaxLevel(), lv);
                meta.addStoredEnchant(ench, lv, true);
            }
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], Aliases.ENCHANT);
        Enchantment ench = Aliases.ENCHANT.convertAlias(args[2]);
        if (ench == null)
            return Collections.emptyList();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i <= ench.getMaxLevel(); i++)
            list.add(String.valueOf(i));
        return list;
    }

}
