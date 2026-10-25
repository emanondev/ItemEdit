package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class Type extends SubCmd {

    public Type(ItemEditCommand cmd) {
        super("type", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInMainHand(p);
        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        ItemType itemType = getItemType(args[1]);
        if (itemType == null || ItemType.AIR.equals(itemType)) {
            onFail(p, alias);
            return;
        }
        ItemStack newItem = item.withType(itemType.createItemStack().getType());
        p.getInventory().setItemInMainHand(newItem);
        updateView(p);
        onSuccess(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.completeItemTypes(args[1], itemType -> !ItemType.AIR.equals(itemType));
        }
        return List.of();
    }


    public ItemType getItemType(@NotNull String input) {
        String normalizedInput = input.toLowerCase(Locale.ENGLISH);
        if (!normalizedInput.contains(":")) {
            normalizedInput = NamespacedKey.MINECRAFT + ":" + normalizedInput;
        }
        NamespacedKey key = NamespacedKey.fromString(normalizedInput);
        return key == null ? null : Registry.ITEM.get(key);
    }

}
