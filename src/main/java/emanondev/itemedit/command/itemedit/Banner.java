package emanondev.itemedit.command.itemedit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.BannerEditor;
import net.md_5.bungee.api.chat.BaseComponent;

public class Banner extends SubCmd {
    private BaseComponent[] helpSet;
    private BaseComponent[] helpColor;
    private BaseComponent[] helpAdd;
    private BaseComponent[] helpColorBanner;
    private BaseComponent[] helpRemove;

    public Banner(ItemEditCommand cmd) {
        super("banner", cmd, true, true);
        load();
    }

    private void load() {
        this.helpSet = this.craftFailFeedback(getConfString("set.params"),
                String.join("\n", getConfStringList("set.description")));
        this.helpAdd = this.craftFailFeedback(getConfString("add.params"),
                String.join("\n", getConfStringList("add.description")));
        this.helpColor = this.craftFailFeedback(getConfString("color.params"),
                String.join("\n", getConfStringList("color.description")));
        this.helpColorBanner = this.craftFailFeedback(getConfString("colorbanner.params"),
                String.join("\n", getConfStringList("colorbanner.description")));
        this.helpRemove = this.craftFailFeedback(getConfString("remove.params"),
                String.join("\n", getConfStringList("remove.description")));
    }

    public void reload() {
        super.reload();
        load();
    }

    private static final String[] subCommands = new String[]{"add", "set", "colorbanner", "remove", "color"};

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof BannerMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }
        if (args.length == 1) {
            p.openInventory(new BannerEditor(p, item).getInventory());
            return;
        }

        switch (args[1].toLowerCase()) {
            case "add":
                addPattern(p, item, args);
                return;
            case "set":
                setPattern(p, item, args);
                return;
            case "colorbanner":
                color(p, item, args);
                return;
            case "remove":
                removePattern(p, item, args);
                return;
            case "color":
                colorPattern(p, item, args);
                return;
            default:
                onFail(p);
        }
    }

    // itemedit banner color id color
    private void colorPattern(Player p, ItemStack item, String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            int id = Integer.parseInt(args[2]) - 1;
            PatternType type = meta.getPattern(id).getPattern();
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpColor);
        }

    }

    private void removePattern(Player p, ItemStack item, String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            int id = Integer.parseInt(args[2]) - 1;
            List<Pattern> list = new ArrayList<>(meta.getPatterns());
            list.remove(id);
            meta.setPatterns(list);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpRemove);
        }
    }

    @SuppressWarnings("deprecation")
    private void color(Player p, ItemStack item, String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            meta.setBaseColor(color);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpColorBanner);
        }
    }

    private void setPattern(Player p, ItemStack item, String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            int id = Integer.parseInt(args[4]) - 1;

            meta.setPattern(id, new Pattern(color, type));
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpSet);
        }
    }

    private void addPattern(Player p, ItemStack item, String[] args) {
        try {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);

            meta.addPattern(new Pattern(color, type));
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpAdd);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return Util.complete(args[1], subCommands);
        }
        if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set")))
            return Util.complete(args[2], Aliases.PATTERN_TYPE);
        if (args.length == 4 && (args[1].equalsIgnoreCase("color") || args[1].equalsIgnoreCase("add")
                || args[1].equalsIgnoreCase("set")))
            return Util.complete(args[3], Aliases.COLOR);
        if (args.length == 3 && args[1].equalsIgnoreCase("colorbanner"))
            return Util.complete(args[3], Aliases.COLOR);
        return Collections.emptyList();
    }

}
