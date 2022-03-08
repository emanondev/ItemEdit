package emanondev.itemedit.command.itemedit;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.BaseComponent;

public class PotionEffectEditor extends SubCmd {

    private static final String[] subCommands = new String[]{"add", "remove", "reset"};
    private BaseComponent[] helpAdd;
    private BaseComponent[] helpRemove;

    public PotionEffectEditor(ItemEditCommand cmd) {
        super("potioneffect", cmd, true, true);

        load();

    }

    private void load() {
        this.helpAdd = this.craftFailFeedback(getConfString("add.params"),
                String.join("\n", getConfStringList("add.description")));
        this.helpRemove = this.craftFailFeedback(getConfString("remove.params"),
                String.join("\n", getConfStringList("remove.description")));
    }

    public void reload() {
        super.reload();
        load();
    }

    @Override
    public void onCmd(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (!(item.getItemMeta() instanceof PotionMeta) && !(item.getItemMeta() instanceof SuspiciousStewMeta)) {
            Util.sendMessage(p, this.getConfString("wrong-type"));
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
            onFail(p);
        }

    }

    private void potioneffectRemove(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3)
                throw new IllegalArgumentException("Wrong param number");
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2].toUpperCase());
            if (effect == null)
                throw new IllegalArgumentException();
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.removeCustomEffect(effect);
                item.setItemMeta(meta);
            } else {
                SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
                meta.removeCustomEffect(effect);
                item.setItemMeta(meta);
            }
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpRemove);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
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

            int level = 0;
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (effect == null)
                throw new IllegalArgumentException();
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
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                meta.addCustomEffect(new PotionEffect(effect, duration, level), true);
                item.setItemMeta(meta);
            } else {
                SuspiciousStewMeta meta = (SuspiciousStewMeta) item.getItemMeta();
                meta.addCustomEffect(new PotionEffect(effect, duration, level), true);
                item.setItemMeta(meta);
            }
            p.updateInventory();
        } catch (Exception e) {
            p.spigot().sendMessage(helpAdd);
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
            p.updateInventory();
        } catch (Exception e) {
        }
    }

}
