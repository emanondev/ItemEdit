package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ItemModel extends SubCmd {

    public ItemModel(ItemEditCommand cmd) {//1.21.2+
        super("itemmodel", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length != 1 && args.length != 2) {
            onFail(p, alias);
            return;
        }

        if (args.length == 1) {
            item.setItemModel(null).build();
            updateView(p);
            sendFeedback(p, "feedback-reset");
            return;
        }
        String[] rawKey = args[1].toLowerCase(Locale.ENGLISH).split(":");
        NamespacedKey key = rawKey.length == 1 ? new NamespacedKey(NamespacedKey.MINECRAFT, rawKey[0]) :
                new NamespacedKey(rawKey[0], rawKey[1]);
        item.setItemModel(key).build();
        onSuccess(p);
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Registry.ITEM.stream().collect(Collectors.toList()),
                    args[1].contains(":") ? (type) -> type.getKey().toString() : (type) -> type.getKey().getKey());
        }
        return List.of();
    }

}