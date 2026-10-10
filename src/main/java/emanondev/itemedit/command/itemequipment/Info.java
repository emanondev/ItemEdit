package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Equippable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Info extends SubCmd {
    public Info(ItemEquipmentCommand command) {
        super("info", command, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = getItemInMainHand(player);
        Equippable equipment = item.getData(DataComponentTypes.EQUIPPABLE);
        if (equipment == null) {
            Util.sendMessage2(player, translate("not_set", player));
            return;
        }

        String allowedEntities = equipment.allowedEntities() == null ? "all"
                : equipment.allowedEntities().values().stream().map(Object::toString).collect(Collectors.joining(", "));
        Util.sendMessage2(player, translateList("message", player,
                "%slot%", equipment.slot().toString(),
                "%canbesheared%", String.valueOf(equipment.canBeSheared()),
                "%damageonhurt%", String.valueOf(equipment.damageOnHurt()),
                "%dispensable%", String.valueOf(equipment.dispensable()),
                "%equiponinteract%", String.valueOf(equipment.equipOnInteract()),
                "%swappable%", String.valueOf(equipment.swappable()),
                "%equipsound%", Objects.toString(equipment.equipSound(), "-"),
                "%shearsound%", Objects.toString(equipment.shearSound(), "-"),
                "%allowedentities%", allowedEntities,
                "%cameraoverlay%", Objects.toString(equipment.cameraOverlay(), "-"),
                "%assetid%", Objects.toString(equipment.assetId(), "-")));
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
