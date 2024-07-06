package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilLegacy;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PotionEffectEditor extends SubCmd {

    private static final String[] subCommands = new String[]{"add", "remove", "reset"};

    public PotionEffectEditor(ItemEditCommand cmd) {
        super("potioneffect", cmd, true, true);
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof PotionMeta) && !(item.getItemMeta() instanceof SuspiciousStewMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            if (p.hasPermission("itemedit.admin")) {
                String msg = this.getLanguageString("itemtag-tip", null, sender);
                if (msg != null && !msg.isEmpty()) {
                    Util.sendMessage(p, new ComponentBuilder(msg).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(String.join("\n",
                                            this.getLanguageStringList("itemtag-tip-hover", null, p))).create()))
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/89634/")).create()
                    );
                }
            }
            return;
        }

        try {
            if (args.length < 2)
                throw new IllegalArgumentException("Wrong param number");

            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "reset":
                    potioneffectClear(p, item, args);
                    return;
                case "add":
                    potioneffectAdd(p, item, args);
                    return;
                case "remove":
                    potioneffectRemove(p, item, args);
                    return;
                default:
                    throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    private void potioneffectRemove(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2].toUpperCase());

            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                        getLanguageStringList("remove.description", null, p)));
                return;
            }
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.removeCustomEffect(effect);
                item.setItemMeta(meta);
            } else {
                SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
                meta.removeCustomEffect(effect);
                item.setItemMeta(meta);
            }
            updateView(p);
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Util.complete(args[1], subCommands);
            case 3:
                if (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))
                    return Util.complete(args[2], Aliases.POTION_EFFECT);
                return Collections.emptyList();
            case 4:
                if (args[1].equalsIgnoreCase("add"))
                    return Util.complete(args[3], "infinite", "0", "90", "180", "480");
                return Collections.emptyList();
            case 5:
                if (args[1].equalsIgnoreCase("add"))
                    return Util.complete(args[4], "1", "2", "3");
                return Collections.emptyList();
            case 6:
            case 7:
            case 8:
                if (args[1].equalsIgnoreCase("add"))
                    return Util.complete(args[args.length - 1], Aliases.BOOLEAN);
                return Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    private void potioneffectAdd(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 4 && args.length != 5 && args.length != 6 && args.length != 7 && args.length != 8)
                throw new IllegalArgumentException("Wrong param number");

            int level = 0;
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback(getLanguageString("add.params", null, p),
                        getLanguageStringList("add.description", null, p)));
                return;
            }
            int duration = UtilLegacy.readPotionEffectDurationSecondsToTicks(args[3]);

            if (args.length >= 5) {
                level = Integer.parseInt(args[4]) - 1;
                if ((level < 0) || (level > 127))
                    throw new IllegalArgumentException();
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
            if (args.length == 8) {
                icon = Aliases.BOOLEAN.convertAlias(args[7]);
            }

            if (!p.hasPermission(this.getPermission() + ".bypass_limits"))
                level = Math.min(level, 1);
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.addCustomEffect(new PotionEffect(effect, duration, level, ambient, particles, icon), true);
                item.setItemMeta(meta);
            } else {
                SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
                meta.addCustomEffect(new PotionEffect(effect, duration, level, ambient, particles, icon), true);
                item.setItemMeta(meta);
            }
            updateView(p);
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("add.params", null, p),
                    getLanguageStringList("add.description", null, p)));
        }
    }

    private void potioneffectClear(Player p, ItemStack item, String[] args) {
        try {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.clearCustomEffects();
                item.setItemMeta(meta);
            } else {
                SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
                meta.clearCustomEffects();
                item.setItemMeta(meta);
            }
            updateView(p);
        } catch (Exception ignored) {
        }
    }

}
