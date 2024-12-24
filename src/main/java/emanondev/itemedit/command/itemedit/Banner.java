package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.BannerEditor;
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

    public Banner(ItemEditCommand cmd) {
        super("banner", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
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
            case "add":
                addPattern(p, item, alias, args);
                return;
            case "set":
                setPattern(p, item, alias, args);
                return;
            case "remove":
                removePattern(p, item, alias, args);
                return;
            case "color":
                colorPattern(p, item, alias, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    // itemedit banner color id color
    private void colorPattern(Player p, ItemStack item, String alias,String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            int id = Integer.parseInt(args[2]) - 1;
            PatternType type = meta.getPattern(id).getPattern();
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (color == null) {
                onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p,alias,"color");
                return;
            }
            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p,alias,"color");
        }

    }

    private void removePattern(Player p, ItemStack item, String alias,String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            int id = Integer.parseInt(args[2]) - 1;
            List<Pattern> list = new ArrayList<>(meta.getPatterns());
            list.remove(id);
            meta.setPatterns(list);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p,alias,"remove");
        }
    }

    private void setPattern(Player p, ItemStack item, String alias,String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) onWrongAlias("wrong-pattern", p, Aliases.PATTERN_TYPE);
                if (color == null) onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p,alias,"set");
                return;
            }
            int id = Integer.parseInt(args[4]) - 1;
            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p,alias,"set");
        }
    }

    private void addPattern(Player p, ItemStack item, String alias,String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) onWrongAlias("wrong-pattern", p, Aliases.PATTERN_TYPE);
                if (color == null) onWrongAlias("wrong-color", p, Aliases.COLOR);
                sendFailFeedbackForSub(p,alias,"add");
                return;
            }
            meta.addPattern(new Pattern(color, type));
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p,alias,"add");
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Util.complete(args[1], subCommands);
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("set")))
            return Util.complete(args[2], Aliases.PATTERN_TYPE);
        if (args.length == 4 && (args[1].equalsIgnoreCase("color")
                || args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("set")))
            return Util.complete(args[3], Aliases.COLOR);
        return Collections.emptyList();
    }

}
