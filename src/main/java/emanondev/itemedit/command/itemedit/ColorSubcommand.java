package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.ColorGui;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ColorSubcommand extends SubCmd {
    private final String tippedArrowPerm;
    private final String potionPerm;
    private final String leatherPerm;
    private final String starsPerm;

    public ColorSubcommand(@NotNull ItemEditCommand cmd) {
        super("color", cmd, true, true);
        tippedArrowPerm = getPermission() + ".tipped_arrow";
        potionPerm = getPermission() + ".potion";
        leatherPerm = getPermission() + ".leather";
        starsPerm = getPermission() + ".firework_star";
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        String perm = calculatePermission(item);
        if (perm == null) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_rgb_colorable");
            return;
        }

        if (!sender.hasPermission(perm)) {
            this.getCommand().sendPermissionLackMessage(perm, sender);
            return;
        }

        if (!VersionUtils.isUpTo(1, 10) && args.length == 1 && !Objects.equals(perm, starsPerm)) {
            p.openInventory(new ColorGui(p).getInventory());
            return;
        }
        if (args.length != 4) {
            onFail(p, alias);
            return;
        }

        Color color;
        try {
            color = Color.fromRGB(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        } catch (NumberFormatException e) {
            onFail(p, alias);
            return;
        } catch (Exception e) {//todo check numbers make sense 0 to 255
            onFail(p, alias);
            return;
        }

        if (!Objects.equals(perm, starsPerm)) {
            item.setColor(color).build();
            updateView(p);
            onSuccess(p);
            return;
        }

        //then it's firework
        //TODO this only sets color for first element
        FireworkEffect oldEffect = item.getFireworkEffect();
        FireworkEffect.Builder newEffect = FireworkEffect.builder().flicker(oldEffect != null && oldEffect.hasFlicker())
                .trail(oldEffect != null && oldEffect.hasTrail()).withColor(color);
        if (oldEffect != null && oldEffect.getFadeColors() != null) {
            newEffect.withFade(oldEffect.getFadeColors());
        }
        item.setFireworkEffect(newEffect.build()).build();
        onSuccess(p);
        updateView(p);
    }

    private String calculatePermission(ItemBuilder item) {
        if (item.isMetaClass(LeatherArmorMeta.class)) {
            return leatherPerm;
        } else if (item.isMetaClass(FireworkEffectMeta.class)) {
            return starsPerm;
        } else if (!VersionUtils.isUpTo(1, 10) && item.isMetaClass(PotionMeta.class)) {
            if (item.getType() == Material.TIPPED_ARROW) {
                return tippedArrowPerm;
            } else if (item.getType().name().contains("POTION")) {
                return potionPerm;
            } else {
                //TODO log issue
                new IllegalStateException("unhandled kind " + item.getType().name()).printStackTrace();
                return potionPerm;
            }
        }
        return null;
    }

    // itemedit bookauthor <name>
    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
