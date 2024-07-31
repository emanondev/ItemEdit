package emanondev.itemedit.compability;

import org.bukkit.Registry;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class V1_20_6 {

    public static AttributeModifier createAttribute(org.bukkit.attribute.Attribute attr, double amount, AttributeModifier.Operation op, @Nullable String slot) {
        EquipmentSlotGroup group;
        if (slot == null)
            group = EquipmentSlotGroup.ANY;
        else {
            group = EquipmentSlotGroup.getByName(slot.toUpperCase(Locale.ENGLISH));
            if (group == null)
                group = EquipmentSlot.valueOf(slot.toUpperCase(Locale.ENGLISH)).getGroup();
        }
        return new AttributeModifier(UUID.randomUUID(), attr.getKey().toString(), amount, op, group);
    }

    public static PatternType[] getPatternTypes() {
        List<PatternType> result = new ArrayList<>();
        Registry.BANNER_PATTERN.forEach(result::add);
        return result.toArray(new PatternType[0]);
    }
}
