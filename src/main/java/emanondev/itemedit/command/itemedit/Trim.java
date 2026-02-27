package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Trim extends SubCmd {

    public Trim(ItemEditCommand cmd) {
        super("armortrim", cmd, true, true);

    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(ItemUtils.getMeta(item) instanceof ArmorMeta armorMeta)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_armor");
            return;
        }
        if (args.length == 2 && args[1].equalsIgnoreCase("clear")) {
            armorMeta.setTrim(null);
            item.setItemMeta(armorMeta);
            updateView(p);
            return;
        }
        if (args.length != 3) {
            onFail(sender, alias);
            return;
        }
        TrimMaterial mat = Aliases.TRIM_MATERIAL.convertAlias(args[1]);
        if (mat == null) {
            onWrongAlias(p, Aliases.TRIM_MATERIAL);
            onFail(p, alias);
            return;
        }
        TrimPattern patt = Aliases.TRIM_PATTERN.convertAlias(args[2]);
        if (patt == null) {
            onWrongAlias(p, Aliases.TRIM_PATTERN);
            onFail(p, alias);
            return;
        }
        armorMeta.setTrim(new ArmorTrim(mat, patt));
        item.setItemMeta(armorMeta);
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> {
                List<String> list = new ArrayList<>(CompleteUtility.complete(args[1], Aliases.TRIM_MATERIAL));
                if ("clear".startsWith(args[1].toLowerCase(Locale.ENGLISH))) {
                    list.add("CLEAR");
                }
                yield list;
            }
            case 3 -> args[1].equalsIgnoreCase("clear")
                    ? List.of() : CompleteUtility.complete(args[2], Aliases.TRIM_PATTERN);
            default -> List.of();
        };
    }
}