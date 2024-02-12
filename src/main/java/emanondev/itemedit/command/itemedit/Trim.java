package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Trim extends SubCmd {

    public Trim(ItemEditCommand cmd) {
        super("armortrim", cmd, true, true);

    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        ItemMeta meta = item.getItemMeta();
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
                Util.sendMessage(p, this
                        .craftFailFeedback(getLanguageString("params", null, p),
                                getLanguageStringList("description", null, p)));
                return;
            }
            TrimPattern patt = Aliases.TRIM_PATTERN.convertAlias(args[2]);
            if (patt == null) {
                onWrongAlias("wrong-pattern", p, Aliases.TRIM_PATTERN);
                Util.sendMessage(p, this
                        .craftFailFeedback(getLanguageString("params", null, p),
                                getLanguageStringList("description", null, p)));
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
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            List<String> list = Util.complete(args[1], Aliases.TRIM_MATERIAL);
            if ("clear".startsWith(args[1].toLowerCase(Locale.ENGLISH)))
                list.add("CLEAR");
            return list;
        }
        if (args.length == 3 && !args[1].equalsIgnoreCase("clear"))
            return Util.complete(args[2], Aliases.TRIM_PATTERN);
        return Collections.emptyList();
    }
}