package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Collections;
import java.util.List;

public class SpawnerEggType extends SubCmd {

    public SpawnerEggType(ItemEditCommand cmd) {
        super("spawnereggtype", cmd, true, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof SpawnEggMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        SpawnEggMeta itemMeta = (SpawnEggMeta) item.getItemMeta();

        try {
            if (args.length != 2)
                throw new IllegalArgumentException();
            EntityType type = Aliases.EGG_TYPE.convertAlias(args[1]);
            if (type == null) {
                onWrongAlias("wrong-entity", p, Aliases.EGG_TYPE);
                onFail(p, alias);
                return;
            }
            itemMeta.setSpawnedType(type);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.EGG_TYPE);
        return Collections.emptyList();
    }

}
