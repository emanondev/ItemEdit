package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.FireworkEditor;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Firework extends SubCmd {

    public Firework(ItemEditCommand cmd) {
        super("firework", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (!item.isMetaClass(FireworkMeta.class)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_firework");
            return;
        }
        p.openInventory(new FireworkEditor(p, item.build()).getInventory());
    }

    // itemedit firework
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }

}
