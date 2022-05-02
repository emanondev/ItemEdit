package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class PotionEffectEditorOld extends SubCmd {
    private static final String[] subCommands = new String[]{"add", "remove", "reset"};

    public PotionEffectEditorOld(ItemEditCommand cmd) {
        super("potioneffect",
                cmd, true, true);

    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof PotionMeta)) {
            Util.sendMessage(p, this.getLanguageString("wrong-type", null, sender));
            return;
        }

        try {
            if (args.length < 2)
                throw new IllegalArgumentException("Wrong param number");

            switch (args[1].toLowerCase()) {
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

            PotionMeta meta = (PotionMeta) item.getItemMeta();

            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2].toUpperCase());
            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                        getLanguageStringList("remove.description", null, p)));
                return;
            }

            meta.removeCustomEffect(effect);
            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            Util.sendMessage(p, this.craftFailFeedback(getLanguageString("remove.params", null, p),
                    getLanguageStringList("remove.description", null, p)));
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], subCommands);
        if (args.length == 3 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove")))
            return Util.complete(args[2], Aliases.POTION_EFFECT);
        return Collections.emptyList();
    }

    private void potioneffectAdd(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 4 && args.length != 5)
                throw new IllegalArgumentException("Wrong param number");

            PotionMeta meta = (PotionMeta) item.getItemMeta();
            int level = 0;
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback(getLanguageString("add.params", null, p),
                        getLanguageStringList("add.description", null, p)));
                return;
            }
            int duration = Integer.parseInt(args[3]) * 20;
            if (duration < 0)
                throw new IllegalArgumentException();
            if (args.length == 5) {
                level = Integer.parseInt(args[4]) - 1;
                if ((level < 0) || (level > 127))
                    throw new IllegalArgumentException();
            }
            if (!p.hasPermission(this.getPermission() + ".bypass_limits"))
                level = Math.min(level, 1);

            meta.addCustomEffect(new PotionEffect(effect, duration, level), true);

            item.setItemMeta(meta);
            p.updateInventory();
        } catch (Exception e) {
            Util.sendMessage(p, this
                    .craftFailFeedback(getLanguageString("add.params", null, p),

                            getLanguageStringList("add.description", null, p)));
        }
    }

    private void potioneffectClear(Player p, ItemStack item, String[] args) {
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.clearCustomEffects();
        item.setItemMeta(meta);
        p.updateInventory();
    }
}
