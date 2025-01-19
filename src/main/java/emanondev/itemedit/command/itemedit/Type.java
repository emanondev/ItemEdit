package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class Type extends SubCmd {

    public Type(ItemEditCommand cmd) {
        super("type", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length != 2)
                throw new IllegalArgumentException("Wrong param number");

            Material mat = Material.valueOf(args[1].toUpperCase());
            if (mat == Material.AIR)
                throw new IllegalArgumentException();
            item.setType(mat);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            if (VersionUtils.isVersionUpTo(1, 12, 99))
                return Util.complete(args[1], Material.class);
            return Util.complete(args[1], Material.class, Material::isItem);
        }
        return Collections.emptyList();
    }

}
