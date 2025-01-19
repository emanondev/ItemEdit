package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Collections;
import java.util.List;

public class ColorOld extends SubCmd {
    private final String leatherPerm;
    private final String starsPerm;

    public ColorOld(ItemEditCommand cmd) {
        super("color", cmd, true, true);
        leatherPerm = getPermission() + ".leather";
        starsPerm = getPermission() + ".firework_star";
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if ((item.getItemMeta() instanceof LeatherArmorMeta)) {
            if (!sender.hasPermission(leatherPerm)) {
                this.getCommand().sendPermissionLackMessage(leatherPerm, sender);
                return;
            }
            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) ItemUtils.getMeta(item);
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                Color color = Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                leatherMeta.setColor(color);
                item.setItemMeta(leatherMeta);
                updateView(p);
            } catch (Exception e) {
                onFail(p, alias);
            }
            return;
        }
        if (item.getItemMeta() instanceof FireworkEffectMeta) {
            if (!sender.hasPermission(starsPerm)) {
                this.getCommand().sendPermissionLackMessage(starsPerm, sender);
                return;
            }

            FireworkEffectMeta starMeta = (FireworkEffectMeta) item.getItemMeta();
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                org.bukkit.Color color = org.bukkit.Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                FireworkEffect oldEffect = starMeta.getEffect();
                FireworkEffect.Builder newEffect = FireworkEffect.builder().flicker(oldEffect != null && oldEffect.hasFlicker())
                        .trail(oldEffect != null && oldEffect.hasTrail()).withColor(color);
                if (oldEffect != null && oldEffect.getFadeColors() != null)
                    newEffect.withFade(oldEffect.getFadeColors());
                starMeta.setEffect(newEffect.build());
                item.setItemMeta(starMeta);
                updateView(p);
            } catch (Exception e) {
                onFail(p, alias);
            }
            return;
        }
        Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));

    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}