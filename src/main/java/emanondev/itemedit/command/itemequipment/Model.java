package emanondev.itemedit.command.itemequipment;

import emanondev.itemedit.command.ItemEquipmentCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class Model extends SubCmd {
    public Model(ItemEquipmentCommand command) {
        super("model", command, true, true);
    }

    //if nutrition <amount>
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length == 1) {
            item.setEquipmentModel(null).build();
            updateView(p);
            sendFeedback(p, "feedback-reset");
            return;
        }
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        String[] rawKey = args[1].toLowerCase(Locale.ENGLISH).split(":");
        NamespacedKey key = rawKey.length == 1 ? new NamespacedKey(NamespacedKey.MINECRAFT, rawKey[0]) :
                new NamespacedKey(rawKey[0], rawKey[1]);
        item.setEquipmentModel(key).build();
        updateView(p);
        onSuccess(p, "%key%", key.toString());
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of(); //TODO
    }
}
