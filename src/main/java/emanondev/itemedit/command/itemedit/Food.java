package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilLegacy;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Food extends SubCmd {


    private static final String[] foodSub = new String[]{"clear", "canalwayseat",
            "eatticks", "nutrition", "saturation", "addeffect", "removeeffect", "cleareffects", "info", "convertto"};

    public Food(ItemEditCommand cmd) {
        super("food", cmd, true, true);
    }

    /*
    food clear
    food canalwayseat [true/false]
    food eatticks <amount>
    food nutrition <amount>
    food saturation <amount>
    food addeffect <type> <duration> [level=1] [particles] [ambient] [icon] [chances=1]
    food removeeffect <type> [level]
    food cleareffects
    food info
    food convertto <type/serveritem id>
     */
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "clear":
                foodClear(p, item, args);
                return;
            case "canalwayseat":
                foodCanAlwaysEat(p, item, args);
                return;
            case "eatticks":
                foodEatTicks(p, item, args);
                return;
            case "nutrition":
                foodNutrition(p, item, args);
                return;
            case "saturation":
                foodSaturation(p, item, args);
                return;
            case "addeffect":
                foodAddEffect(p, item, args);
                return;
            case "removeeffect":
                foodRemoveEffect(p, item, args);
                return;
            case "cleareffects":
                foodClearEffects(p, item, args);
                return;
            case "info":
                foodInfo(p, item, args);
                return;
            case "convertto":
                if (!Util.isVersionAfter(1, 21))
                    onFail(p, alias);
                foodConvertTo(p, item, args);
                return;
            default:
                onFail(p, alias);
        }
    }


    public void onFail(CommandSender target, String alias) {
        Util.sendMessage(target, new ComponentBuilder(getLanguageString("help-header", "", target)).create());
        for (String sub : foodSub)
            Util.sendMessage(target, new ComponentBuilder(
                    ChatColor.DARK_GREEN + "/" + alias + " " + this.getName() + ChatColor.GREEN + " " + sub + " "
                            + getLanguageString(sub + ".params", "", target))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/" + alias + " " + this.getName() + " " + sub + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder(String.join("\n",
                                    getLanguageStringList(sub + ".description", null, target))).create()))
                    .create());
    }

    private void foodClear(Player p, ItemStack item, String[] args) {
        if (!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasFood())
            return;
        meta.setFood(null);
        item.setItemMeta(meta);
        updateView(p);
        //TODO feedback?
    }

    //ie food canalwayseat [true/false]
    private void foodCanAlwaysEat(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                p.spigot().sendMessage(this.craftFailFeedback("canalwayseat " + getLanguageString("canalwayseat.params", null, p),
                        getLanguageStringList("canalwayseat.description", null, p)));
                return;
            }
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            if (args.length == 2)
                food.setCanAlwaysEat(!food.canAlwaysEat());
            else
                food.setCanAlwaysEat(Aliases.BOOLEAN.convertAlias(args[2]));
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("canalwayseat " + getLanguageString("canalwayseat.params", null, p),
                    getLanguageStringList("canalwayseat.description", null, p)));
        }
    }


    //ie food eatticks <amount>
    private void foodEatTicks(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3) {
                p.spigot().sendMessage(this.craftFailFeedback("eatticks " + getLanguageString("eatticks.params", null, p),
                        getLanguageStringList("eatticks.description", null, p)));
                return;
            }
            int val = Integer.parseInt(args[2]);
            if (val < 0)
                val = 0;
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.setEatSeconds((float) val / 20);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("eatticks " + getLanguageString("eatticks.params", null, p),
                    getLanguageStringList("eatticks.description", null, p)));
        }
    }

    //ie food nutrition <amount>
    private void foodNutrition(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3) {
                p.spigot().sendMessage(this.craftFailFeedback("nutrition " + getLanguageString("nutrition.params", null, p),
                        getLanguageStringList("nutrition.description", null, p)));
                return;
            }
            int val = Integer.parseInt(args[2]);
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.setNutrition(val);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("nutrition " + getLanguageString("nutrition.params", null, p),
                    getLanguageStringList("nutrition.description", null, p)));
        }
    }

    //ie food saturation <amount>
    private void foodSaturation(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3) {
                p.spigot().sendMessage(this.craftFailFeedback("saturation " + getLanguageString("saturation.params", null, p),
                        getLanguageStringList("saturation.description", null, p)));
                return;
            }
            float val = Float.parseFloat(args[2]);
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.setSaturation(val);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("saturation " + getLanguageString("saturation.params", null, p),
                    getLanguageStringList("saturation.description", null, p)));
        }
    }


    //ie food addeffect <type> <duration> [amplifier=1] [particles] [ambient] [icon] [chances=100%]
    private void foodAddEffect(Player p, ItemStack item, String[] args) {
        try {
            if (args.length < 4 || args.length > 9) {
                p.spigot().sendMessage(this.craftFailFeedback("addeffect " + getLanguageString("addeffect.params", null, p),
                        getLanguageStringList("addeffect.description", null, p)));
                return;
            }
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback("addeffect " + getLanguageString("addeffect.params", null, p),
                        getLanguageStringList("addeffect.description", null, p)));
                return;
            }
            int duration = UtilLegacy.readPotionEffectDurationSecondsToTicks(args[3]);

            int level = 0;
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
            if (args.length >= 8) {
                icon = Aliases.BOOLEAN.convertAlias(args[7]);
            }
            float chance = 1;
            if (args.length == 9) {
                chance = Float.parseFloat(args[8])/100;
                if (chance <= 0 || chance > 1)
                    throw new IllegalArgumentException();
            }
            if (!p.hasPermission(this.getPermission() + ".bypass_limits"))
                level = Math.min(level, 1);
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.addEffect(
                    Util.isVersionAfter(1, 13) ?
                            new PotionEffect(effect, duration, level, ambient, particles, icon) :
                            new PotionEffect(effect, duration, level, ambient, particles)
                    , chance);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("addeffect " + getLanguageString("addeffect.params", null, p),
                    getLanguageStringList("addeffect.description", null, p)));
        }
    }

    //ie food removeeffect <type> [amplifier]
    private void foodRemoveEffect(Player p, ItemStack item, String[] args) {
        try {
            if (args.length != 3 && args.length != 4) {
                p.spigot().sendMessage(this.craftFailFeedback("removeeffect " + getLanguageString("removeeffect.params", null, p),
                        getLanguageStringList("removeeffect.description", null, p)));
                return;
            }
            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (type == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                Util.sendMessage(p, this.craftFailFeedback("removeeffect " + getLanguageString("removeeffect.params", null, p),
                        getLanguageStringList("removeeffect.description", null, p)));
                return;
            }
            Integer level;
            if (args.length == 4) {
                level = Integer.parseInt(args[3]) - 1;
                if ((level < 0) || (level > 127))
                    throw new IllegalArgumentException();
            } else {
                level = null;
            }

            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            ArrayList<FoodComponent.FoodEffect> list = new ArrayList<>(food.getEffects());

            list.removeIf(effect -> effect.getEffect().getType().equals(type) &&
                    (level == null || effect.getEffect().getAmplifier() == level));

            food.setEffects(list);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("removeeffect " + getLanguageString("removeeffect.params", null, p),
                    getLanguageStringList("removeeffect.description", null, p)));
        }
    }

    //ie food cleareffects
    private void foodClearEffects(Player p, ItemStack item, String[] args) {
        try {
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.setEffects(Collections.emptyList());
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback("cleareffects " + getLanguageString("cleareffects.params", null, p),
                    getLanguageStringList("cleareffects.description", null, p)));
        }
    }

    //ie food info
    private void foodInfo(Player p, ItemStack item, String[] args) {
        try {
            ItemMeta meta = item.getItemMeta();
            if (!item.hasItemMeta() || !meta.hasFood()) {
                sendLanguageString("info.not_food", "", p);
                return;
            }
            FoodComponent food = meta.getFood();

            ArrayList<String> list = new ArrayList<>(this.getLanguageStringList("info.message", Collections.emptyList(), p,
                    "%saturation%", UtilsString.formatNumber(food.getSaturation(), 2, false),
                    "%nutrition%", String.valueOf(food.getNutrition()),
                    "%eatticks%", String.valueOf((int) (food.getEatSeconds() * 20)),
                    "%eatseconds%", UtilsString.formatNumber(food.getEatSeconds(), 2, true),
                    "%canalwayseat%", Aliases.BOOLEAN.getDefaultName(food.canAlwaysEat()),
                    "%convertto%",
                    Util.isVersionAfter(1, 21) ? (food.getUsingConvertsTo() == null ? Aliases.BOOLEAN.getDefaultName(false) :
                            food.getUsingConvertsTo().getType().name() + (food.getUsingConvertsTo().hasItemMeta() ? " [...]" : "")
                                    + (food.getUsingConvertsTo().getAmount() == 1 ? "" : (" x" + food.getUsingConvertsTo().getAmount())))
                            : "&c1.21+ only",
                    "%effects%", String.valueOf(food.getEffects().size())));
            int index = 1;
            for (FoodComponent.FoodEffect foodEffect : food.getEffects()) {
                PotionEffect effect = foodEffect.getEffect();
                list.addAll(this.getLanguageStringList("info.effect", Collections.emptyList(), p,
                        "%index%", String.valueOf(index),
                        "%type%", Aliases.POTION_EFFECT.getDefaultName(effect.getType()),
                        "%level%", String.valueOf(effect.getAmplifier() + 1),
                        "%duration_s%", effect.getDuration() == -1 ? "∞" : UtilsString.formatNumber(effect.getDuration() / 20D, 2, true),
                        "%hasparticle%", Aliases.BOOLEAN.getDefaultName(effect.hasParticles()),
                        "%isAmbient%", Aliases.BOOLEAN.getDefaultName(effect.isAmbient()),
                        "%hasicon%", Aliases.BOOLEAN.getDefaultName(!Util.isVersionAfter(1, 13) || effect.hasIcon()),
                        "%duration_ticks%", effect.getDuration() == -1 ? "∞" : String.valueOf(effect.getDuration()),
                        "%chance_perc%", UtilsString.formatNumber(foodEffect.getProbability() * 100, 2, true)
                ));
                index++;
            }
            Util.sendMessage(p, String.join("\n", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ie food convertto <material/type> [amount]
    private void foodConvertTo(Player p, ItemStack item, String[] args) {
        try {
            if (args.length > 4) {
                p.spigot().sendMessage(this.craftFailFeedback("convertto " + getLanguageString("convertto.params", null, p),
                        getLanguageStringList("convertto.description", null, p)));
                return;
            }
            ItemStack target = null;
            if (args.length >= 3) {
                String name = args[2];
                target = ItemEdit.get().getServerStorage().getItem(name);
                if (target == null) {
                    try {
                        Material mat = Material.valueOf(name.toUpperCase(Locale.ENGLISH));
                        if (!mat.isItem())
                            throw new IllegalArgumentException();
                        target = new ItemStack(mat);
                    } catch (IllegalArgumentException e2) {
                        p.spigot().sendMessage(this.craftFailFeedback("convertto " + getLanguageString("convertto.invalid-type", null, p),
                                getLanguageStringList("convertto.description", null, p)));
                        return;
                    }
                }
            }
            int amount = 1;
            if (args.length == 4)
                amount = Integer.parseInt(args[3]);

            if (target.getType().isAir())
                target = null;
            if (target != null)
                target.setAmount(amount);
            ItemMeta meta = item.getItemMeta();
            FoodComponent food = meta.getFood();
            food.setUsingConvertsTo(target);
            meta.setFood(food);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            p.spigot().sendMessage(this.craftFailFeedback(getLanguageString("convertto.params", null, p),
                    getLanguageStringList("convertto.description", null, p)));
        }
    }

    /*
        food clear
        food canalwayseat [true/false]
        food eatticks [amount]
        food nutrition [amount]
        food saturation [amount]
        food addeffect <type> <duration> [amplifier=1] [particles] [ambient] [icon] [chances=1]
        food removeeffect <type> [amplifier]
        food cleareffects
        food info
        food convertto <type/serveritem id> [amount]
         */
    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return Collections.emptyList();
        switch (args.length) {
            case 2:
                List<String> list = Util.complete(args[1], foodSub);
                if (!Util.isVersionAfter(1,21))
                    list.remove("convertto");
                return list;
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "canalwayseat":
                        return Util.complete(args[2], Aliases.BOOLEAN);
                    case "eatticks":
                        return Util.complete(args[2], "1", "20", "40");
                    case "saturation":
                        return Util.complete(args[2], "0", "1", "1.5", "2", "10");
                    case "nutrition":
                        return Util.complete(args[2], "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
                    case "addeffect":
                        return Util.complete(args[2], Aliases.POTION_EFFECT);
                    case "removeeffect": {
                        ItemStack item = getItemInHand((Player) sender);
                        HashSet<PotionEffectType> types = new HashSet<>();
                        for (FoodComponent.FoodEffect food:item.getItemMeta().getFood().getEffects())
                            types.add(food.getEffect().getType());
                        List<String> list2 = new ArrayList<>();//Util.complete(args[2], Aliases.POTION_EFFECT);
                        for (PotionEffectType type:types)
                            list2.add(Aliases.POTION_EFFECT.getDefaultName(type));
                        return Util.complete(args[2],list2);
                    }
                    case "convertto":
                        if (Util.isVersionAfter(1, 21)) {
                            List<String> list2 = Util.complete(args[2], Material.class);
                            list2.addAll(Util.complete(args[2], ItemEdit.get().getServerStorage().getIds()));
                            return list2;
                        }
                }
                return Collections.emptyList();
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "addeffect":
                        return Util.complete(args[3], "infinite", "0", "60", "180");
                    case "removeeffect":
                        return Util.complete(args[3], "1", "2", "3", "4", "5");
                    case "convertto":
                        if (Util.isVersionAfter(1, 21))
                            return Util.complete(args[3], "1", "10", "64");
                }
                return Collections.emptyList();
            case 5:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect"))
                    return Util.complete(args[4], "1", "2", "3", "4", "5");
                return Collections.emptyList();
            case 6:
            case 7:
            case 8:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect"))
                    return Util.complete(args[args.length - 1], Aliases.BOOLEAN);
                return Collections.emptyList();
            case 9:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect"))
                    return Util.complete(args[8], "0.01", "10.0", "50.0", "100.0");
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }
}
