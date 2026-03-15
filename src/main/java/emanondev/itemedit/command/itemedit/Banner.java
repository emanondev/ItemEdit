package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.BannerEditor;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Banner extends SubCmd {

    private static final String[] subCommands = new String[]{"add", "set", "remove", "color"};

    public Banner(@NotNull ItemEditCommand cmd) {
        super("banner", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(this.getItemInHand(p));
        if (!(item.isMetaClass(BannerMeta.class))) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_banner");
            return;
        }
        if (args.length == 1) {
            p.openInventory(new BannerEditor(p, item.build()).getInventory());
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
    private void colorPattern(@NotNull Player p, @NotNull ItemBuilder item, @NotNull String alias, String[] args) {
        try {
            //TODO check indexes
            int index = Integer.parseInt(args[2]) - 1;
            PatternType type = item.getBannerPatterns().get(index).getPattern();
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (color == null) {
                onWrongAlias(p, Aliases.COLOR);
                onSubFail(p, alias, "color");
                return;
            }
            item.setBannerPattern(index, new Pattern(color, type)).build();
            onSubSuccess(p, "color");
            updateView(p);
        } catch (Exception e) {
            Util.logCommandError(getCommand(),args, p);
            onSubFail(p, alias, "color");
        }

    }

    private void removePattern(@NotNull Player p, @NotNull ItemBuilder item, @NotNull String alias, String[] args) {
        try {
            //TODO check indexes
            int index = Integer.parseInt(args[2]) - 1;
            List<Pattern> list = new ArrayList<>(item.getBannerPatterns());
            list.remove(index);
            item.setBannerPatterns(list).build();
            onSubSuccess(p, "remove");
            updateView(p);
        } catch (Exception e) {
            Util.logCommandError(getCommand(),args, p);
            onSubFail(p, alias, "remove");
        }
    }

    private void setPattern(@NotNull Player p, @NotNull ItemBuilder item, @NotNull String alias, String[] args) {
        try {
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) {
                    onWrongAlias(p, Aliases.PATTERN_TYPE);
                }
                if (color == null) {
                    onWrongAlias(p, Aliases.COLOR);
                }
                onSubFail(p, alias, "set");
                return;
            }
            //TODO check indexes
            int index = Integer.parseInt(args[4]) - 1;
            item.setBannerPattern(index, new Pattern(color, type)).build();
            onSubSuccess(p, "set");
            updateView(p);
        } catch (NumberFormatException n) {
            onSubFail(p, alias, "set");
        } catch (Exception e) {
            Util.logCommandError(getCommand(),args, p);
            onSubFail(p, alias, "set");
        }
    }

    private void addPattern(@NotNull Player p, @NotNull ItemBuilder item, @NotNull String alias, String[] args) {
        try {
            if (args.length != 3 && args.length != 4) {
                onSubFail(p, alias, "add");
                return;
            }
            PatternType type = Aliases.PATTERN_TYPE.convertAlias(args[2]);
            DyeColor color = Aliases.COLOR.convertAlias(args[3]);
            if (type == null || color == null) {
                if (type == null) {
                    onWrongAlias(p, Aliases.PATTERN_TYPE);
                }
                if (color == null) {
                    onWrongAlias(p, Aliases.COLOR);
                }
                onSubFail(p, alias, "add");
                return;
            }
            item.addBannerPattern(new Pattern(color, type)).build();
            onSubSuccess(p, "add");
            updateView(p);
        } catch (Exception e) {
            Util.logCommandError(getCommand(),args, p);
            onSubFail(p, alias, "add");
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], subCommands);
            case 3 -> switch (args[1].toLowerCase()) {
                case "add", "set" -> CompleteUtility.complete(args[2], Aliases.PATTERN_TYPE);
                default -> List.of();
            };
            case 4 -> switch (args[1].toLowerCase()) {
                case "color", "add", "set" -> CompleteUtility.complete(args[3], Aliases.COLOR);
                default -> List.of();
            };
            default -> List.of();
        };
    }

}
