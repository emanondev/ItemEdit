package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class SpawnerEggType extends SubCmd {

    public SpawnerEggType(ItemEditCommand cmd) {
        super("spawnereggtype", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        ItemMeta rawMeta = ItemUtils.getMeta(item);
        if (!(rawMeta instanceof SpawnEggMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        SpawnEggMeta meta = (SpawnEggMeta) rawMeta;

        try {
            if (args.length != 2) {
                throw new IllegalArgumentException();
            }
            EntityType type = Aliases.EGG_TYPE.convertAlias(args[1]);
            if (type == null) {
                onWrongAlias("wrong-entity", p, Aliases.EGG_TYPE);
                onFail(p, alias);
                return;
            }
            meta.setSpawnedType(type);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], Aliases.EGG_TYPE);
        }
        return Collections.emptyList();
    }

}
