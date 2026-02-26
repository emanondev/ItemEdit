package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Clear extends SubCmd {
    public Clear(ItemEquipmentCommand command) {
        super("clear", command, true, true);
    }

    //command id <value>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            onFail(player, alias);
            return;
        }
        new ItemBuilder(getItemInHand(player)).clearEquippable().build();
        onSuccess(player);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
