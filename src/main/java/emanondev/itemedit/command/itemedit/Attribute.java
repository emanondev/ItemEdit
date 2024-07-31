package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilLegacy;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Attribute extends SubCmd {
    private static final String[] attributeSub = new String[]{"add", "remove"};

    public Attribute(ItemEditCommand cmd) {
        super("attribute", cmd, true, true);
    }

    public void reload() {
        super.reload();
    }

    // add <attribute> amount [operation] [equip]
    // remove [attribute/slot]
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "add":
                attributeAdd(p, item, args);
                return;
            case "remove":
                attributeRemove(p, item, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    // add <attribute> amount [operation] [equip]
    private void attributeAdd(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 4 || args.length > 6)
                throw new IllegalArgumentException("Wrong param number");

            org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
            if (attr == null) {
                onWrongAlias("wrong-attribute", p, Aliases.ATTRIBUTE);
                p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("add.params", null, p),
                        getLanguageStringList("add.description", null, p)));
                return;
            }
            double amount = Double.parseDouble(args[3]);
            Operation op;
            if (args.length > 4)
                op = Aliases.OPERATIONS.convertAlias(args[4]);
            else
                op = Operation.ADD_NUMBER;

            if (op == null) {
                onWrongAlias("wrong-operation", p, Aliases.OPERATIONS);
                p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("add.params", null, p),
                        getLanguageStringList("add.description", null, p)));
                return;
            }


            String equip = null;

            if (args.length > 5) {
                if (Util.isVersionAfter(1, 21)) {
                    {
                        equip = Aliases.EQUIPMENT_SLOTGROUPS.convertAlias(args[5]).toString();
                        if (equip == null) {
                            onWrongAlias("wrong-equipment", p, Aliases.EQUIPMENT_SLOTGROUPS);
                            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("add.params", null, p),
                                    getLanguageStringList("add.description", null, p)));
                            return;
                        }
                    }
                } else {
                    equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[5]).toString();
                    if (equip == null) {
                        onWrongAlias("wrong-equipment", p, Aliases.EQUIPMENT_SLOTS);
                        p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("add.params", null, p),
                                getLanguageStringList("add.description", null, p)));
                        return;
                    }
                }
            }

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addAttributeModifier(attr,UtilLegacy.createAttributeModifier(attr, amount, op, equip));
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("add.params", null, p),
                    getLanguageStringList("add.description", null, p)));
        }
    }

    // remove [attribute/slot]
    private void attributeRemove(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
            EquipmentSlot equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[2]);
            if (attr == null && equip == null) {
                onWrongAlias("wrong-attribute", p, Aliases.ATTRIBUTE);
                onWrongAlias("wrong-equipment", p, Aliases.EQUIPMENT_SLOTS);
                p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("remove.params", null, p),
                        getLanguageStringList("remove.description", null, p)));
                return;
            }

            ItemMeta itemMeta = item.getItemMeta();
            if (attr != null)
                itemMeta.removeAttributeModifier(attr);
            if (equip != null)
                itemMeta.removeAttributeModifier(equip);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    // attribute add/rem attr amount op slot
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], attributeSub);
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length == 3)
                return Util.complete(args[2], Aliases.ATTRIBUTE);
            if (args.length == 5)
                return Util.complete(args[4], Aliases.OPERATIONS);
            if (args.length == 6) {
                if (Util.isVersionAfter(1, 21))
                    return Util.complete(args[5], Aliases.EQUIPMENT_SLOTGROUPS);
                return Util.complete(args[5], Aliases.EQUIPMENT_SLOTS);
            }
        } else if (args[1].equalsIgnoreCase("remove") && args.length == 3) {
            List<String> l = Util.complete(args[2], Aliases.ATTRIBUTE);
            l.addAll(Util.complete(args[2], Aliases.EQUIPMENT_SLOTS));
            return l;
        }
        return Collections.emptyList();
    }

}
