package emanondev.itemedit.command.itemedit;

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
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_tropical_fish_bucket");
            return;
        }

        if (args.length < 2) {
            onFail(sender, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "pattern" -> pattern(p, item, alias, args);
            case "patterncolor" -> patternColor(p, item, alias, args);
            case "bodycolor" -> bodyColor(p, item, alias, args);
            default -> onFail(p, alias);
        }
    }

    private void bodyColor(Player p, ItemStack item, String alias, String[] args) {
        if (args.length != 3) {
            sendFailFeedbackForSub(p, alias, "bodycolor");
            return;
        }

        try {
            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias(p, Aliases.COLOR);
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
        if (args.length != 3) {
            sendFailFeedbackForSub(p, alias, "patterncolor");
            return;
        }

        try {
            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null) {
                onWrongAlias(p, Aliases.COLOR);
                sendFailFeedbackForSub(p, alias, "patterncolor");
                return;
            }
            meta.setPatternColor(color);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            e.printStackTrace();
            sendFailFeedbackForSub(p, alias, "patterncolor");
        }
    }

    private void pattern(Player p, ItemStack item, String alias, String[] args) {
        if (args.length != 3) {
            sendFailFeedbackForSub(p, alias, "pattern");
            return;
        }

        try {
            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) ItemUtils.getMeta(item);

            Pattern pattern = Aliases.TROPICALPATTERN.convertAlias(args[2]);
            if (pattern == null) {
                onWrongAlias(p, Aliases.TROPICALPATTERN);
                sendFailFeedbackForSub(p, alias, "pattern");
                return;
            }
            meta.setPattern(pattern);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            e.printStackTrace();
            sendFailFeedbackForSub(p, alias, "pattern");
        }
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