package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class Attribute extends SubCmd {
    private static final String[] attributeSub = new String[]{"add", "remove"};

    public Attribute(@NotNull ItemEditCommand cmd) {
        super("attribute", cmd, true, true);
    }

    // add <attribute> amount [operation] [equip]
    // remove [attribute/slot]
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "add" -> attributeAdd(p, item, alias, args);
            case "remove" -> attributeRemove(p, item, alias, args);
            default -> onFail(p, alias);
        }
    }

    // attribute add/rem attr amount op slot
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], attributeSub);
        }
        return switch (args[1].toLowerCase()) {
            case "add" -> switch (args.length) {
                case 3 -> CompleteUtility.complete(args[2], Aliases.ATTRIBUTE);
                case 5 -> CompleteUtility.complete(args[4], Aliases.OPERATIONS);
                case 6 -> VersionUtils.isAfter(1, 21)
                        ? CompleteUtility.complete(args[5], Aliases.EQUIPMENT_SLOTGROUPS)
                        : CompleteUtility.complete(args[5], Aliases.EQUIPMENT_SLOTS);
                default -> List.of();
            };
            case "remove" -> args.length == 3 ? Stream.concat(
                    CompleteUtility.complete(args[2], Aliases.ATTRIBUTE).stream(),
                    CompleteUtility.complete(args[2], Aliases.EQUIPMENT_SLOTS).stream()
            ).toList() : List.of();
            default -> List.of();
        };
    }

    // add <attribute> amount [operation] [equip]
    private void attributeAdd(Player p, ItemStack item, String alias, String[] args) {
        if (args.length < 4 || args.length > 6) {
            onSubFail(p, alias, "add");
            return;
        }
        org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
        if (attr == null) {
            onWrongAlias(p, Aliases.ATTRIBUTE);
            onSubFail(p, alias, "add");
            return;
        }
        double amount;
        try {
            amount = Double.parseDouble(args[3]);
        } catch (NumberFormatException e) {
            onSubFail(p, alias, "add");
            return;
        }
        Operation op;
        if (args.length > 4) {
            op = Aliases.OPERATIONS.convertAlias(args[4]);
        } else {
            op = Operation.ADD_NUMBER;
        }

        if (op == null) {
            onWrongAlias(p, Aliases.OPERATIONS);
            onSubFail(p, alias, "add");
            return;
        }


        String equip = null;

        if (args.length > 5) {
            if (VersionUtils.isAfter(1, 21)) {
                equip = Aliases.EQUIPMENT_SLOTGROUPS.convertAlias(args[5]).toString();
                if (equip == null) {
                    onWrongAlias(p, Aliases.EQUIPMENT_SLOTGROUPS);
                    onSubFail(p, alias, "add");
                    return;
                }
            } else {
                equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[5]).toString();
                if (equip == null) {
                    onWrongAlias(p, Aliases.EQUIPMENT_SLOTS);
                    onSubFail(p, alias, "add");
                    return;
                }
            }
        }

        ItemMeta itemMeta = ItemUtils.getMeta(item);

        //TODO here eventually add defaults and merge logic


        itemMeta.addAttributeModifier(attr, ItemUtils.createAttributeModifier(amount, op, equip));
        item.setItemMeta(itemMeta);
        onSubSuccess(p, "add");
        updateView(p);
    }

    // remove [attribute/slot]
    private void attributeRemove(Player p, ItemStack item, String alias, String[] args) {
        if (args.length != 3) {
            onSubFail(p, alias, "remove");
        }

        org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
        EquipmentSlot equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[2]);
        if (attr == null && equip == null) {
            onWrongAlias(p, Aliases.ATTRIBUTE);
            onWrongAlias(p, Aliases.EQUIPMENT_SLOTS);
            onSubFail(p, alias, "remove");
            return;
        }

        ItemMeta itemMeta = ItemUtils.getMeta(item);
        //TODO here


        if (attr != null) {
            itemMeta.removeAttributeModifier(attr);
            sendFeedback(p, getId() + ".feedback-attribute");
        }
        if (equip != null) {
            itemMeta.removeAttributeModifier(equip);
            sendFeedback(p, getId() + ".feedback-equipment");
        }
        item.setItemMeta(itemMeta);
        updateView(p);
    }

}
