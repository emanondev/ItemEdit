package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilLegacy;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemBuilder;
import emanondev.itemedit.utility.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PotionEffectEditor extends SubCmd {
    private static final String[] subCommands = new String[]{"add", "remove", "reset"};

    public PotionEffectEditor(ItemEditCommand cmd) {
        super("potioneffect", cmd, true, true);

    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemBuilder item = new ItemBuilder(getItemInHand(p));
        if (!item.isMetaClass(PotionMeta.class)
                && (VersionUtils.isUpTo(1, 14) || !item.isMetaClass(SuspiciousStewMeta.class))) {
            getPlugin().getTranslator().send(p, "generic.error.wrong-material_potion_effect_applicable");
            if (p.hasPermission("itemedit.admin")) {
                String msg = this.translate("itemtag-tip", sender);
                if (msg != null && !msg.isEmpty()) {
                    Util.sendMessage(p, new ComponentBuilder(msg).event(
                                    Util.craftHoverEvent(
                                            this.translateList("itemtag-tip-hover", p)))
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/plugin/itemtag")).create()
                    );
                }
            }
            return;
        }

        if (args.length < 2) {
            onFail(p, alias);
            return;
        }
        try {
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "reset" -> potioneffectReset(p, item, alias, args);
                case "add" -> potioneffectAdd(p, item, alias, args);
                case "remove" -> potioneffectRemove(p, item, alias, args);
                default -> onFail(p, alias);
            }
        } catch (Exception e) {
            Util.logCommandError(getCommand(),args, p);
            onSubFail(p, alias, args[1].toLowerCase(Locale.ENGLISH));
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return switch (args.length) {
            case 2 -> CompleteUtility.complete(args[1], subCommands);
            case 3 -> {
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")) {
                    yield CompleteUtility.complete(args[2], Aliases.POTION_EFFECT);
                }
                yield List.of();
            }
            case 4 -> {
                if (args[1].equalsIgnoreCase("add")) {
                    yield CompleteUtility.complete(args[3], "infinite", "instant", "∞", "90", "180", "480");
                }
                yield List.of();
            }
            case 5 -> {
                if (args[1].equalsIgnoreCase("add")) {
                    yield CompleteUtility.complete(args[4], "1", "2", "3");
                }
                yield List.of();
            }
            case 6, 7 -> {
                if (args[1].equalsIgnoreCase("add")) {
                    yield CompleteUtility.complete(args[args.length - 1], Aliases.BOOLEAN);
                }
                yield List.of();
            }
            case 8 -> {
                if (VersionUtils.isAfter(1, 13) && args[1].equalsIgnoreCase("add")) {
                    yield CompleteUtility.complete(args[args.length - 1], Aliases.BOOLEAN);
                }
                yield List.of();
            }
            default -> List.of();
        };
    }

    private void potioneffectRemove(Player p, ItemBuilder item, String alias, String[] args) {
        if (args.length != 3) {
            onSubFail(p, alias, "remove");
        }

        PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2].toUpperCase());
        if (effect == null) {
            onWrongAlias(p, Aliases.POTION_EFFECT);
            onSubFail(p, alias, "remove");
            return;
        }

        item.removeCustomEffect(effect).build();
        onSubSuccess(p, "remove");
        updateView(p);
    }

    private void potioneffectAdd(Player p, ItemBuilder item, String alias, String[] args) {
        if (args.length != 4 && args.length != 5 && args.length != 6 && args.length != 7 && args.length != 8) {
            onSubFail(p, alias, "add");
        }

        int level = 0;
        PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
        if (type == null) {
            onWrongAlias(p, Aliases.POTION_EFFECT);
            onSubFail(p, alias, "add");
            return;
        }
        int duration = UtilLegacy.readPotionEffectDurationSecondsToTicks(args[3]);
        if (args.length >= 5) {
            level = Integer.parseInt(args[4]) - 1;
            if ((level < 0) || (level > 127)) {
                throw new IllegalArgumentException();
            }
        }
        boolean particles = true;
        if (args.length >= 6) {
            particles = Aliases.BOOLEAN.convertAlias(args[5]);
        }
        boolean ambient = false;
        if (args.length >= 7) {
            ambient = Aliases.BOOLEAN.convertAlias(args[6]);
        }
        boolean icon = true;
        if (VersionUtils.isAfter(1, 13) && args.length == 8) {
            icon = Aliases.BOOLEAN.convertAlias(args[7]);
        }
        if (!p.hasPermission(this.getPermission() + ".bypass_limits")) {
            level = Math.min(level, 1);
        }
        PotionEffect effect = VersionUtils.isAfter(1, 13) ?
                new PotionEffect(type, duration, level, ambient, particles, icon) :
                new PotionEffect(type, duration, level, ambient, particles);

        item.addCustomEffect(effect).build();
        onSubSuccess(p, "add");
        updateView(p);
    }

    private void potioneffectReset(Player p, ItemBuilder item, String alias, String[] args) {
        item.clearCustomEffects().build();
        onSubSuccess(p, "clear");
        updateView(p);
    }
}
