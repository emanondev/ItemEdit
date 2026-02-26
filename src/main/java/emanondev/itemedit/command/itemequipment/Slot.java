package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Slot extends SubCmd {
    public Slot(ItemEquipmentCommand command) {
        super("slot", command, true, true);
    }

    //command id <value>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        EquipmentSlot slot = Aliases.EQUIPMENT_SLOTS.convertAlias(args[1]);
        if (slot == null) {
            onWrongAlias(p, Aliases.EQUIPMENT_SLOTS);
            onFail(p, alias);
            return;
        }
        new ItemBuilder(getItemInHand(p)).setEquippableSlot(slot).build();
        onSuccess(p, "%value%", args[1]);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length != 2) {
            return List.of();
        }
        return CompleteUtility.complete(args[1], Aliases.EQUIPMENT_SLOTS);
    }
}
