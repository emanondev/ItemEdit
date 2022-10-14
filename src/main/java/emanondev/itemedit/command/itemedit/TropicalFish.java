package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TropicalFish extends SubCmd {

    private static final String[] subCommands = new String[]{"pattern", "patterncolor", "bodycolor"};

    public TropicalFish(ItemEditCommand cmd) {
        super("tropicalfish",
                cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof TropicalFishBucketMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            if (args.length < 2)
                throw new IllegalArgumentException("Wrong param number");

            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "pattern":
                    pattern(p, item, args);
                    return;
                case "patterncolor":
                    patternColor(p, item, args);
                    return;
                case "bodycolor":
                    bodyColor(p, item, args);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    private void bodyColor(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                Util.sendMessage(p, this
                        .craftFailFeedback(getLanguageString("bodycolor.params", null, p),
                                getLanguageStringList("bodycolor.description", null, p)));
                return;
            }
            meta.setBodyColor(color);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            Util.sendMessage(p, this
                    .craftFailFeedback(getLanguageString("bodycolor.params", null, p),
                            getLanguageStringList("bodycolor.description", null, p)));
        }
    }

    private void patternColor(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                Util.sendMessage(p, this
                        .craftFailFeedback(getLanguageString("patterncolor.params", null, p),
                                getLanguageStringList("patterncolor.description", null, p)));
                return;
            }
            meta.setPatternColor(color);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            Util.sendMessage(p, this
                    .craftFailFeedback(getLanguageString("patterncolor.params", null, p),
                            getLanguageStringList("patterncolor.description", null, p)));
        }
    }

    private void pattern(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            Pattern pattern = Aliases.TROPICALPATTERN.convertAlias(args[2]);
            if (pattern == null) {
                onWrongAlias("wrong-pattern", p, Aliases.TROPICALPATTERN);
                Util.sendMessage(p, this.craftFailFeedback(getLanguageString("pattern.params", null, p),
                        getLanguageStringList("pattern.description", null, p)));
                return;
            }
            meta.setPattern(pattern);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("pattern.params", null, p),
                    getLanguageStringList("pattern.description", null, p)));
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], subCommands);
        if (args.length == 3)
            if (args[1].equalsIgnoreCase("patterncolor") || args[1].equalsIgnoreCase("bodycolor"))
                return Util.complete(args[2], Aliases.COLOR);
            else if (args[1].equalsIgnoreCase("pattern"))
                return Util.complete(args[2], Aliases.TROPICALPATTERN);
        return Collections.emptyList();
    }
}