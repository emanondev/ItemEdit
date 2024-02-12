package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Unbreakable extends SubCmd {

    public Unbreakable(ItemEditCommand cmd) {
        super("unbreakable", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            try {
                if (args.length > 2)
                    throw new IllegalArgumentException("Wrong param number");
                ItemMeta meta = item.getItemMeta();
                meta.setUnbreakable(args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !meta.isUnbreakable());
                item.setItemMeta(meta);
            } catch (Throwable t) { //backward compability for 1.8 and 1.9
                if (args.length > 2)
                    throw new IllegalArgumentException("Wrong param number");
                Map<String, Object> map = new LinkedHashMap<>(item.getItemMeta().serialize());
                boolean value = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1]) : !map.containsKey("Unbreakable");

                if (value)
                    map.put("Unbreakable", true);
                else
                    map.remove("Unbreakable");
                map.put("==", "ItemMeta");
                ItemMeta meta = (ItemMeta) ConfigurationSerialization.deserializeObject(map);
                item.setItemMeta(meta);
            }
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], Aliases.BOOLEAN);
        return Collections.emptyList();
    }
}
