package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.IntStream;

public class FireworkPower extends SubCmd {

    public FireworkPower(ItemEditCommand cmd) {
        super("fireworkpower", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof FireworkMeta itemMeta)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_firework");
            return;
        }

        if (args.length != 2) {
            onFail(p, alias);
            return;
        }
        try {
            int power = Integer.parseInt(args[1]);
            if (power < 0 || power > 5) {
                onFail(p, alias);
                return;
            }
            itemMeta.setPower(power);
            item.setItemMeta(itemMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }
    }

    // itemedit fireworkpower <power>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return args.length == 2 ? IntStream.range(0, 6)
                .mapToObj(String::valueOf).toList() : List.of();
    }

}
