package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CameraOverlay extends SubCmd {
    public CameraOverlay(ItemEquipmentCommand command) {
        super("cameraoverlay", command, true, true);
    }

    //if nutrition <amount>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length != 1 && args.length != 2) {
            onFail(p, alias);
            return;
        }

        String rawkey = args.length == 1 ? null : args[1];
        NamespacedKey key = rawkey == null ? null : NamespacedKey.fromString(rawkey);
        if (args.length == 2 && key == null) {
            sendLanguageString("invalid-namespacedkey", p,
                    "%value%", rawkey);
            return;
        }
        item.setEquipmentCameraOverlay(key).build();
        if (key != null) {
            onSuccess(p, "%value%", key.toString());
        } else {
            sendFeedback(p, "feedback-reset");
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? CompleteUtility.complete(args[1], "minecraft:misc/pumpkinblur") : List.of();
    }
}
