package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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
        ItemStack item = this.getItemInHand(p);
        if (!(ItemUtils.getMeta(item) instanceof TropicalFishBucketMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            if (args.length < 2) {
                throw new IllegalArgumentException("Wrong param number");
            }

            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "pattern":
                    pattern(p, item, alias, args);
                    return;
                case "patterncolor":
                    patternColor(p, item, alias, args);
                    return;
                case "bodycolor":
                    bodyColor(p, item, alias, args);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    private void bodyColor(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException("Wrong param number");
            }

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p, alias, "bodycolor");
                return;
            }
            meta.setBodyColor(color);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "bodycolor");
        }
    }

    private void patternColor(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException("Wrong param number");
            }

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p, alias, "patterncolor");
                return;
            }
            meta.setPatternColor(color);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "patterncolor");
        }
    }

    private void pattern(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                throw new IllegalArgumentException("Wrong param number");
            }

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            Pattern pattern = Aliases.TROPICALPATTERN.convertAlias(args[2]);
            if (pattern == null) {
                onWrongAlias("wrong-pattern", p, Aliases.TROPICALPATTERN);
                sendFailFeedbackForSub(p, alias, "pattern");
                return;
            }
            meta.setPattern(pattern);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "pattern");
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2) {
            return CompleteUtility.complete(args[1], subCommands);
        }
        if (args.length == 3)
            if (args[1].equalsIgnoreCase("patterncolor") || args[1].equalsIgnoreCase("bodycolor")) {
                return CompleteUtility.complete(args[2], Aliases.COLOR);
            } else if (args[1].equalsIgnoreCase("pattern")) {
                return CompleteUtility.complete(args[2], Aliases.TROPICALPATTERN);
            }
        return Collections.emptyList();
    }
}