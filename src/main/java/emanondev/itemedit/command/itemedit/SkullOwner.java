package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkullOwner extends SubCmd {

    public SkullOwner(ItemEditCommand cmd) {
        super("skullowner", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (!item.isMetaClass(SkullMeta.class)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_player_head");
            return;
        }

        if (args.length == 1) {
            item.setSkullOwner(null).build();
            updateView(p);
            sendFeedback(p, "feedback-reset");
            return;
        }
        StringBuilder name = new StringBuilder(args[1]);
        for (int i = 2; i < args.length; i++) {
            name.append(" ").append(args[i]);
        }
        name = new StringBuilder(ChatColor.translateAlternateColorCodes('&', name.toString()));
        item.setSkullOwner(name.toString()).build();
        onSuccess(p);
        updateView(p);
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.completePlayers(args[1]);
        }
        return List.of();
    }

}
