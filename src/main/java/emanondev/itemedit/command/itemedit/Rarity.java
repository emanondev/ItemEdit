package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Rarity extends SubCmd {

    public Rarity(ItemEditCommand cmd) {
        super("rarity", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (args.length > 2) {
            onFail(sender, alias);
            return;
        }
        ItemRarity rarity = args.length == 1 ? null : Aliases.RARITY.convertAlias(args[1]);
        if (rarity == null && args.length != 1) {
            onWrongAlias(p, Aliases.RARITY);
            onFail(p, alias);
            return;
        }
        item.setRarity(rarity).build();
        if (rarity == null) {
            sendFeedback(p, "feedback-reset");
        } else {
            onSuccess(p);
        }
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.RARITY);
        }
        return List.of();
    }

}
