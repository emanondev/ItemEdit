package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
        ItemMeta meta = ItemUtils.getMeta(item);
        if (!(meta instanceof ArmorMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }
        try {
            if (args.length == 2 && args[1].equalsIgnoreCase("clear")) {
                ArmorMeta armorMeta = (ArmorMeta) meta;
                armorMeta.setTrim(null);
                item.setItemMeta(armorMeta);
                updateView(p);
                return;
            }
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");
            TrimMaterial mat = Aliases.TRIM_MATERIAL.convertAlias(args[1]);
            if (mat == null) {
                onWrongAlias("wrong-material", p, Aliases.TRIM_MATERIAL);
                onFail(p, alias);
                return;
            }
            TrimPattern patt = Aliases.TRIM_PATTERN.convertAlias(args[2]);
            if (patt == null) {
                onWrongAlias("wrong-pattern", p, Aliases.TRIM_PATTERN);
                onFail(p, alias);
                return;
            }
            ArmorMeta armorMeta = (ArmorMeta) meta;
            armorMeta.setTrim(new ArmorTrim(mat, patt));
            item.setItemMeta(armorMeta);
            updateView(p);
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> list = CompleteUtility.complete(args[1], Aliases.TRIM_MATERIAL);
            if ("clear".startsWith(args[1].toLowerCase(Locale.ENGLISH)))
                list.add("CLEAR");
            return list;
        }
        if (args.length == 3 && !args[1].equalsIgnoreCase("clear"))
            return CompleteUtility.complete(args[2], Aliases.TRIM_PATTERN);
        return Collections.emptyList();
    }
}