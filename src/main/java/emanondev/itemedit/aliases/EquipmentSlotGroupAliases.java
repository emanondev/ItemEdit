package emanondev.itemedit.aliases;

import org.bukkit.inventory.EquipmentSlotGroup;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("UnstableApiUsage")
public class EquipmentSlotGroupAliases extends AliasSet<EquipmentSlotGroup> {
    public EquipmentSlotGroupAliases() {
        super("equip_group");
    }

    @Override
    public String getName(EquipmentSlotGroup value) {
        return value.toString();
    }

    @Override
    public Collection<EquipmentSlotGroup> getValues() {
        return Arrays.asList(EquipmentSlotGroup.ANY, EquipmentSlotGroup.ARMOR, EquipmentSlotGroup.CHEST,
                EquipmentSlotGroup.FEET, EquipmentSlotGroup.HAND, EquipmentSlotGroup.HEAD,
                EquipmentSlotGroup.LEGS, EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND);
    }
}
