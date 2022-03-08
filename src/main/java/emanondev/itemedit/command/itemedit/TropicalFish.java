package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.BaseComponent;

public class TropicalFish extends SubCmd {

    private static final String[] subCommands = new String[]{"pattern", "patterncolor", "bodycolor"};
    private BaseComponent[] helpPattern;
    private BaseComponent[] helpPatternColor;
    private BaseComponent[] helpBodyColor;

    public TropicalFish(ItemEditCommand cmd) {
        super("tropicalfish",
                cmd, true, true);

        load();

    }

    private void load() {
        this.helpPattern = this
                .craftFailFeedback(getConfString("pattern.params"),
                        String.join("\n",
                                getConfStringList("pattern.description")));
        this.helpPatternColor = this
                .craftFailFeedback(getConfString("patterncolor.params"),
                        String.join("\n",
                                getConfStringList("patterncolor.description")));
        this.helpBodyColor = this
                .craftFailFeedback(getConfString("bodycolor.params"),
                        String.join("\n",
                                getConfStringList("bodycolor.description")));
    }

    public void reload() {
        super.reload();
        load();
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof TropicalFishBucketMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
            return;
        }

        try {
            if (args.length < 2)
                throw new IllegalArgumentException("Wrong param number");

            switch (args[1].toLowerCase()) {
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
            onFail(p);
        }

    }

    private void bodyColor(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null)
                throw new IllegalArgumentException();
            meta.setPatternColor(color);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpBodyColor);
        }
    }

    private void patternColor(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            DyeColor color = Aliases.COLOR.convertAlias(args[2]);
            if (color == null)
                throw new IllegalArgumentException();
            meta.setPatternColor(color);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpPatternColor);
        }
    }

    private void pattern(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");

            TropicalFishBucketMeta meta = (TropicalFishBucketMeta) item.getItemMeta();

            Pattern pattern = Aliases.TROPICALPATTERN.convertAlias(args[2]);
            if (pattern == null)
                throw new IllegalArgumentException();
            meta.setPattern(pattern);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpPattern);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
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