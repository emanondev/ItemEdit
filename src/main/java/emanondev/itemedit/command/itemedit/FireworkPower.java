package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FireworkPower extends SubCmd {

    public FireworkPower(ItemEditCommand cmd) {
        super("fireworkpower", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof FireworkMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        FireworkMeta itemMeta = (FireworkMeta) ItemUtils.getMeta(item);

        try {
            if (args.length != 2)
                throw new IllegalArgumentException();
            int power = Integer.parseInt(args[1]);
            if (power < 0 || power > 5)
                throw new IllegalArgumentException();
            itemMeta.setPower(power);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    // itemedit fireworkpower <power>
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < 6; i++)
                list.add(String.valueOf(i));
            return list;
        }
        return Collections.emptyList();
    }

}
