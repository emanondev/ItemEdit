package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collections;
import java.util.List;

public class Color extends SubCmd {
    private final String tippedArrowPerm;
    private final String potionPerm;
    private final String leatherPerm;
    private final String starsPerm;

    public Color(ItemEditCommand cmd) {
        super("color", cmd, true, true);
        tippedArrowPerm = getPermission() + ".tipped_arrow";
        potionPerm = getPermission() + ".potion";
        leatherPerm = getPermission() + ".leather";
        starsPerm = getPermission() + ".firework_star";
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        ItemMeta meta = ItemUtils.getMeta(item);
        if ((meta instanceof PotionMeta)) {

            if (item.getType() == Material.TIPPED_ARROW && !sender.hasPermission(tippedArrowPerm)) {
                this.getCommand().sendPermissionLackMessage(tippedArrowPerm, sender);
                return;
            }

            if (item.getType().name().contains("POTION") && !sender.hasPermission(potionPerm)) {
                this.getCommand().sendPermissionLackMessage(potionPerm, sender);
                return;
            }
            PotionMeta potionMeta = (PotionMeta) meta;
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                org.bukkit.Color color = org.bukkit.Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                potionMeta.setColor(color);
                item.setItemMeta(potionMeta);
                updateView(p);
            } catch (Exception e) {
                onFail(p, alias);
            }
            return;
        }
        if ((meta instanceof LeatherArmorMeta)) {
            if (!sender.hasPermission(leatherPerm)) {
                this.getCommand().sendPermissionLackMessage(leatherPerm, sender);
                return;
            }

            LeatherArmorMeta leatherMeta = (LeatherArmorMeta) meta;
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                org.bukkit.Color color = org.bukkit.Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                leatherMeta.setColor(color);
                item.setItemMeta(leatherMeta);
                updateView(p);
            } catch (Exception e) {
                onFail(p, alias);
            }
            return;
        }
        if (meta instanceof FireworkEffectMeta) {
            if (!sender.hasPermission(starsPerm)) {
                this.getCommand().sendPermissionLackMessage(starsPerm, sender);
                return;
            }

            FireworkEffectMeta starMeta = (FireworkEffectMeta) meta;
            try {
                if (args.length != 4)
                    throw new IllegalArgumentException("Wrong param number");

                org.bukkit.Color color = org.bukkit.Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]));
                FireworkEffect oldEffect = starMeta.getEffect(); // may be null?
                FireworkEffect.Builder newEffect = FireworkEffect.builder().flicker(oldEffect != null && oldEffect.hasFlicker())
                        .trail(oldEffect != null && oldEffect.hasTrail()).withColor(color);
                if (oldEffect != null && oldEffect.getFadeColors() != null) // may be null?
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
