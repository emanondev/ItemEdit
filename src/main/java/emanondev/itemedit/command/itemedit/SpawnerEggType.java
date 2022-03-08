package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;

public class SpawnerEggType extends SubCmd {

    public SpawnerEggType(ItemEditCommand cmd) {
        super("spawnereggtype", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof SpawnEggMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }

        SpawnEggMeta itemMeta = (SpawnEggMeta) item.getItemMeta();

        try {
            if (args.length != 2)
                throw new IllegalArgumentException();
            EntityType type = Aliases.EGG_TYPE.convertAlias(args[1]);
            if (type == null)
                throw new IllegalArgumentException();
            itemMeta.setSpawnedType(type);
            item.setItemMeta(itemMeta);
            p.updateInventory();
        } catch (Exception e) {
            onFail(p);
        }
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.EGG_TYPE);
        return Collections.emptyList();
    }

}
