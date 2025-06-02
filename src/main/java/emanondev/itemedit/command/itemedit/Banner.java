package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.BannerEditor;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Banner extends SubCmd {

    private static final String[] subCommands = new String[]{"add", "set", "remove", "color"};

    public Banner(final ItemEditCommand cmd) {
        super("banner", cmd, true, true);
    }

    @Override
    public void onCommand(final CommandSender sender, final String alias, final String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof BannerMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }
        if (args.length == 1) {
            p.openInventory(new BannerEditor(p, item).getInventory());
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "add" -> addPattern(p, item, alias, args);
            case "set" -> setPattern(p, item, alias, args);
            case "remove" -> removePattern(p, item, alias, args);
            case "color" -> colorPattern(p, item, alias, args);
            default -> onFail(p, alias);
        }
    }

    // itemedit banner color id color
    private void colorPattern(final Player p, final ItemStack item, final String alias, final String[] args) {
        try {
            BannerMeta meta = (BannerMeta) ItemUtils.getMeta(item);
            int id = Integer.parseInt(args[2]) - 1;
            PatternType type = meta.getPattern(id).getPattern();
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p, alias, "color");
                return;
            }
            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "color");
        }

    }

    private void removePattern(final Player p, final ItemStack item, final String alias, final String[] args) {
        try {
            BannerMeta meta = (BannerMeta) ItemUtils.getMeta(item);
            int id = Integer.parseInt(args[2]) - 1;
            List<Pattern> list = new ArrayList<>(meta.getPatterns());
            list.remove(id);
            meta.setPatterns(list);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "remove");
        }
    }

    private void setPattern(final Player p, final ItemStack item, final String alias, final String[] args) {
        try {
            BannerMeta meta = (BannerMeta) ItemUtils.getMeta(item);
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) {
                    onWrongAlias("wrong-pattern", p, Aliases.PATTERN_TYPE);
                }
                if (color == null) {
                    onWrongAlias("wrong-color", p, Aliases.COLOR);
                }
                sendFailFeedbackForSub(p, alias, "set");
                return;
            }
            int id = Integer.parseInt(args[4]) - 1;
            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "set");
        }
    }

    private void addPattern(final Player p, final ItemStack item, final String alias, final String[] args) {
        try {
            BannerMeta meta = (BannerMeta) ItemUtils.getMeta(item);
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) {
                    onWrongAlias("wrong-pattern", p, Aliases.PATTERN_TYPE);
                }
                if (color == null) {
                    onWrongAlias("wrong-color", p, Aliases.COLOR);
                }
                sendFailFeedbackForSub(p, alias, "add");
                return;
            }
            meta.addPattern(new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "add");
        }
    }

    @Override
    public List<String> onComplete(final CommandSender sender, final String[] args) {
        return switch (args.length){
            case 2 -> CompleteUtility.complete(args[1], subCommands);
            case 3 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add", "set" -> CompleteUtility.complete(args[2], Aliases.PATTERN_TYPE);
                default -> List.of();
            };
            case 4 -> switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "add", "set", "color" -> CompleteUtility.complete(args[3], Aliases.COLOR);
                default -> List.of();
            };
            default -> List.of();
        };
    }

}
