package emanondev.itemedit.command.itemedit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class Hide extends SubCmd {

    public Hide(ItemEditCommand cmd) {
        super("hide", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if ((args.length != 3) && (args.length != 2))
                throw new IllegalArgumentException("Wrong param number");

            ItemMeta itemMeta = item.getItemMeta();
            ItemFlag flag = Aliases.FLAG_TYPE.convertAlias(args[1]);
            if (flag == null) {
                onWrongAlias("wrong-flag", p, Aliases.FLAG_TYPE);
                onFail(p, alias);
                return;
            }
            boolean add = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : !itemMeta.hasItemFlag(flag);
            handleFlagChange(add, flag, item, itemMeta);

            if (add) {
                itemMeta.addItemFlags(flag);
            } else {
                itemMeta.removeItemFlags(flag);
            }

            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    private void handleFlagChange(boolean put, ItemFlag flag, ItemStack item, ItemMeta meta) {
        if (!Util.hasPaperAPI() ||
                !Util.isVersionAfter(1, 20, 5) ||
                !ItemEdit.get().getConfig().loadBoolean("itemedit.paper_hide_fix", true)) {
            return;
        }
        if (flag != ItemFlag.HIDE_ATTRIBUTES) {
            return;
        }
        if (put) {
            if (meta.getAttributeModifiers() != null) {
                return;
            }
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                item.getType().getDefaultAttributeModifiers(slot).forEach(meta::addAttributeModifier);
            }
            return;
        }

        Multimap<Attribute, AttributeModifier> mods = meta.getAttributeModifiers();
        if (mods == null) {
            return;
        }

        HashMultimap<Attribute, AttributeModifier> mods2 = HashMultimap.create();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mods2.putAll(item.getType().getDefaultAttributeModifiers(slot));
        }

        if (mods.equals(mods2)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                meta.removeAttributeModifier(slot);
            }
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.FLAG_TYPE);
        if (args.length == 3)
            return Util.complete(args[2], Aliases.BOOLEAN);
        return Collections.emptyList();
    }

}