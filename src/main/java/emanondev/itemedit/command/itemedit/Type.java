package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Type extends SubCmd {

    public Type(final ItemEditCommand cmd) {
        super("type", cmd, true, true);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong param number");
            }

            Material mat = Material.valueOf(args[1].toUpperCase());
            if (mat == Material.AIR) {
                throw new IllegalArgumentException();
            }
            item.setType(mat);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        if (args.length == 2) {
            if (VersionUtils.isVersionUpTo(1, 12, 99)) {
                return CompleteUtility.complete(args[1], Material.class);
            }
            return CompleteUtility.complete(args[1], Material.class, Material::isItem);
        }
        return Collections.emptyList();
    }

}
