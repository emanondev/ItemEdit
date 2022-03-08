package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.BaseComponent;

public class Attribute extends SubCmd {
    private BaseComponent[] helpAdd;
    private BaseComponent[] helpRemove;
    private static final String[] attributeSub = new String[]{"add", "remove"};

    public Attribute(ItemEditCommand cmd) {
        super("attribute", cmd, true, true);
        load();
    }

    private void load() {
        this.helpAdd = this.craftFailFeedback(getConfString("add.params"),
                String.join("\n", getConfStringList("add.description")));
        this.helpRemove = this.craftFailFeedback(getConfString("remove.params"),
                String.join("\n", getConfStringList("remove.description")));

    }

    public void reload() {
        super.reload();
        load();
    }

    // add <attribute> amount [operation] [equip]
    // remove [attribute/slot]
    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p);
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add":
                attributeAdd(p, item, args);
                return;
            case "remove":
                attributeRemove(p, item, args);
                return;
            default:
                onFail(p);
        }
    }

    // add <attribute> amount [operation] [equip]
    private void attributeAdd(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 4 || args.length > 6)
                throw new IllegalArgumentException("Wrong param number");

            org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
            double amount = Double.parseDouble(args[3]);
            Operation op;
            if (args.length > 4)
                op = Aliases.OPERATIONS.convertAlias(args[4]);
            else
                op = Operation.ADD_NUMBER;
            if (op == null)
                throw new IllegalArgumentException("can't find operation '" + args[4] + "'");

            EquipmentSlot equip;
            if (args.length > 5) {
                equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[5]);
                if (equip == null)
                    throw new IllegalArgumentException("can't find slot '" + args[5] + "'");
            } else
                equip = null;

            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.addAttributeModifier(attr,
                    new AttributeModifier(UUID.randomUUID(), attr.toString(), amount, op, equip));
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpAdd);
        }
    }

    // remove [attribute/slot]
    private void attributeRemove(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            org.bukkit.attribute.Attribute attr = Aliases.ATTRIBUTE.convertAlias(args[2]);
            EquipmentSlot equip = Aliases.EQUIPMENT_SLOTS.convertAlias(args[2]);
            if (attr == null && equip == null)
                throw new IllegalArgumentException();

            ItemMeta itemMeta = item.getItemMeta();
            if (attr != null)
                itemMeta.removeAttributeModifier(attr);
            if (equip != null)
                itemMeta.removeAttributeModifier(equip);
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpRemove);
        }
    }

    // attribute add/rem attr amount op slot
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], attributeSub);
        if (args[1].equalsIgnoreCase("add")) {
            if (args.length == 3)
                return Util.complete(args[2], Aliases.ATTRIBUTE);
            if (args.length == 5)
                return Util.complete(args[4], Aliases.OPERATIONS);
            if (args.length == 6)
                return Util.complete(args[5], Aliases.EQUIPMENT_SLOTS);
        } else if (args[1].equalsIgnoreCase("remove") && args.length == 3) {
            List<String> l = Util.complete(args[2], Aliases.ATTRIBUTE);
            l.addAll(Util.complete(args[2], Aliases.EQUIPMENT_SLOTS));
            return l;
        }
        return Collections.emptyList();
    }

}
