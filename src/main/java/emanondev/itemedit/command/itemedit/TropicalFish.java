package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class TropicalFish extends SubCmd {

    private static final String[] subCommands = new String[]{"pattern", "patterncolor", "bodycolor"};

    public TropicalFish(ItemEditCommand cmd) {
        super("tropicalfish", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (!item.isMetaClass(TropicalFishBucketMeta.class)) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_tropical_fish_bucket");
            return;
        }

        if (args.length < 2) {
            onFail(sender, alias);
            return;
        }

        try {
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "pattern" -> pattern(p, item, alias, args);
                case "patterncolor" -> patternColor(p, item, alias, args);
                case "bodycolor" -> bodyColor(p, item, alias, args);
                default -> onFail(p, alias);
            }
        } catch (Exception e) {
            Util.logCommandError(getCommand(), args, p);
            onSubFail(p, alias, args[1].toLowerCase(Locale.ENGLISH));
        }

    }


    private void bodyColor(Player p, ItemBuilder item, String alias, String[] args) {
        if (args.length != 3) {
            onSubFail(p, alias, "bodycolor");
            return;
        }

        DyeColor color = Aliases.COLOR.convertAlias(args[2]);
        if (color == null) {
            onWrongAlias(p, Aliases.COLOR);
            onSubFail(p, alias, "bodycolor");
            return;
        }
        item.setTropicalFishBodyColor(color).build();
        onSubSuccess(p, "bodycolor");
        updateView(p);
    }

    private void patternColor(Player p, ItemBuilder item, String alias, String[] args) {
        if (args.length != 3) {
            onSubFail(p, alias, "patterncolor");
            return;
        }

        DyeColor color = Aliases.COLOR.convertAlias(args[2]);
        if (color == null) {
            onWrongAlias(p, Aliases.COLOR);
            onSubFail(p, alias, "patterncolor");
            return;
        }
        item.setTropicalFishPatternColor(color).build();
        onSubSuccess(p, "patterncolor");
        updateView(p);
    }

    private void pattern(Player p, ItemBuilder item, String alias, String[] args) {
        if (args.length != 3) {
            onSubFail(p, alias, "pattern");
            return;
        }

        Pattern pattern = Aliases.TROPICALPATTERN.convertAlias(args[2]);
        if (pattern == null) {
            onWrongAlias(p, Aliases.TROPICALPATTERN);
            onSubFail(p, alias, "pattern");
            return;
        }
        item.setTropicalFishPattern(pattern).build();
        onSubSuccess(p, "pattern");
        updateView(p);
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], subCommands);
            case 3 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "patterncolor", "bodycolor" -> CompleteUtility.complete(args[2], Aliases.COLOR);
                case "pattern" -> CompleteUtility.complete(args[2], Aliases.TROPICALPATTERN);
                default -> List.of();
            };
            default -> List.of();
        };
    }
}