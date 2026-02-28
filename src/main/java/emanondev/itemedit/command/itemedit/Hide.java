package emanondev.itemedit.command.itemedit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Hide extends SubCmd {

    public Hide(ItemEditCommand cmd) {
        super("hide", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if ((args.length != 3) && (args.length != 2)) {
            onFail(p, alias);
            return;
        }
        try {
            ItemFlag flag = Aliases.FLAG_TYPE.convertAlias(args[1]);
            if (flag == null) {
                onWrongAlias(p, Aliases.FLAG_TYPE);
                onFail(p, alias);
                return;
            }
            Boolean add = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2]) : (Boolean) !item.hasItemFlag(flag);
            if (add == null) {
                onWrongAlias(p, Aliases.BOOLEAN);
                onFail(p, alias);
                return;
            }
            handleFlagChange(add, flag, item);

            item.setItemFlag(flag, add).build();
            onSuccess(p, "%value%", Aliases.BOOLEAN.convertValue(add));
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    private void handleFlagChange(boolean put, ItemFlag flag, ItemBuilder item) {
        if (!VersionUtils.hasPaperAPI() ||
                !VersionUtils.isAfter(1, 20, 5) ||
                !ItemEdit.get().getConfig().loadBoolean("itemedit.paper_hide_fix", true)) {
            return;
        }
        if (flag != ItemFlag.HIDE_ATTRIBUTES) {
            return;
        }
        if (put) {
            if (item.getAttributeModifiers() != null) {
                return;
            }
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                item.getType().getDefaultAttributeModifiers(slot).forEach(item::addAttributeModifier);
            }
            return;
        }

        Multimap<Attribute, AttributeModifier> mods = item.getAttributeModifiers();
        if (mods == null) {
            return;
        }

        HashMultimap<Attribute, AttributeModifier> mods2 = HashMultimap.create();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mods2.putAll(item.getType().getDefaultAttributeModifiers(slot));
        }

        if (mods.equals(mods2)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                item.removeAttributeModifier(slot).build();
            }
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], Aliases.FLAG_TYPE);
            case 3 -> CompleteUtility.complete(args[2], Aliases.BOOLEAN);
            default -> List.of();
        };
    }

}