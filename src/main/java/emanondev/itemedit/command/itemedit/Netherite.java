package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Netherite extends SubCmd {

    public Netherite(ItemEditCommand cmd) {
        super("netherite", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = this.getItemInHand(player);
        ArmorType type = getArmorType(item.getType());
        if (type == null) {
            Util.sendMessage(player, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(item.getType().getMaxDurability() - type.getDurability());
        }

        if (!hasAttribute(meta, Attribute.ARMOR)) {
            addAttribute(meta, Attribute.ARMOR, type.getArmorValue(), type.getSlot());
        }
        if (!hasAttribute(meta, Attribute.ARMOR_TOUGHNESS)) {
            addAttribute(meta, Attribute.ARMOR_TOUGHNESS, 3, type.getSlot());
        }
        if (!hasAttribute(meta, Attribute.KNOCKBACK_RESISTANCE)) {
            addAttribute(meta, Attribute.KNOCKBACK_RESISTANCE, 0.1, type.getSlot());
        }
        meta.setFireResistant(true);

        item.setItemMeta(meta);
    }

    private boolean hasAttribute(ItemMeta meta, Attribute attribute) {
        if (meta == null) return false;
        Collection<AttributeModifier> modifiers = meta.getAttributeModifiers(attribute);
        if (modifiers == null) return false;
        for (AttributeModifier mod : modifiers) {
            if (mod != null) {
                return true;
            }
        }
        return false;
    }

    private void addAttribute(ItemMeta meta, org.bukkit.attribute.Attribute attribute, double value, EquipmentSlot slot) {
        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), value, AttributeModifier.Operation.ADD_NUMBER, slot));
    }

    private ArmorType getArmorType(Material material) {
        switch (material) {
            case DIAMOND_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case NETHERITE_HELMET:
            case LEATHER_HELMET:
            case CHAINMAIL_HELMET:
                return new ArmorType(407, 3, EquipmentSlot.HEAD);
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case NETHERITE_CHESTPLATE:
            case LEATHER_CHESTPLATE:
            case CHAINMAIL_CHESTPLATE:
                return new ArmorType(592, 8, EquipmentSlot.CHEST);
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case NETHERITE_LEGGINGS:
            case LEATHER_LEGGINGS:
            case CHAINMAIL_LEGGINGS:
                return new ArmorType(555, 6, EquipmentSlot.LEGS);
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case NETHERITE_BOOTS:
            case LEATHER_BOOTS:
            case CHAINMAIL_BOOTS:
                return new ArmorType(481, 3, EquipmentSlot.FEET);
            default:
                return null;
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private static class ArmorType {
        private final int durability;
        private final int armorValue;
        private final EquipmentSlot slot;

        public ArmorType(int durability, int armorValue, EquipmentSlot slot) {
            this.durability = durability;
            this.armorValue = armorValue;
            this.slot = slot;
        }

        public int getDurability() {
            return durability;
        }

        public int getArmorValue() {
            return armorValue;
        }

        public EquipmentSlot getSlot() {
            return slot;
        }
    }
}
