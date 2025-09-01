package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.*;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.consumableeffects.*;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class Food extends SubCmd {


    private static final String[] foodSub =
            VersionUtils.isVersionAfter(1, 21, 2) ?
                    new String[]{"clear", "canalwayseat", "eatticks", "nutrition", "saturation", "addeffect",
                            "removeeffect", "cleareffects", "info", "convertto", "consumeparticles", "animation", "sound"} :
                    (VersionUtils.isVersionAfter(1, 21) ?
                            new String[]{"clear", "canalwayseat", "eatticks", "nutrition", "saturation", "addeffect",
                                    "removeeffect", "cleareffects", "info", "convertto",} :
                            new String[]{"clear", "canalwayseat", "eatticks", "nutrition", "saturation", "addeffect",
                                    "removeeffect", "cleareffects", "info"});

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
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "clear":
                foodClear(p, item, alias, args);
                return;
            case "canalwayseat":
                foodCanAlwaysEat(p, item, alias, args);
                return;
            case "eatticks":
                foodEatTicks(p, item, alias, args);
                return;
            case "nutrition":
                foodNutrition(p, item, alias, args);
                return;
            case "saturation":
                foodSaturation(p, item, alias, args);
                return;
            case "addeffect":
                foodAddEffect(p, item, alias, args);
                return;
            case "removeeffect":
                foodRemoveEffect(p, item, alias, args);
                return;
            case "cleareffects":
                foodClearEffects(p, item, alias, args);
                return;
            case "consumeparticles":
                if (!VersionUtils.isVersionAfter(1, 21, 2)) {
                    onFail(p, alias);
                    return;
                }
                foodConsumeParticles(p, item, alias, args);
                return;
            case "animation":
                if (!VersionUtils.isVersionAfter(1, 21, 2)) {
                    onFail(p, alias);
                    return;
                }
                foodAnimation(p, item, alias, args);
                return;
            case "sound":
                if (!VersionUtils.isVersionAfter(1, 21, 2)) {
                    onFail(p, alias);
                    return;
                }
                foodSound(p, item, alias, args);
                return;
            case "info":
                foodInfo(p, item, alias, args);
                return;
            case "convertto":
                if (!VersionUtils.isVersionAfter(1, 21)) {
                    onFail(p, alias);
                    return;
                }
                foodConvertTo(p, item, alias, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    private void foodSound(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "sound");
                return;
            }
            Sound value = Aliases.SOUND.convertAlias(args[2]);
            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumable = meta.getConsumable();
            consumable.setSound(value);
            meta.setConsumable(consumable);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "sound");
        }
    }

    private void foodAnimation(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "animation");
                return;
            }
            ConsumableComponent.Animation value = Aliases.ANIMATION.convertAlias(args[2]);
            if (value == null) {
                onWrongAlias("wrong-animation", p, Aliases.ANIMATION);
                sendFailFeedbackForSub(p, alias, "animation");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumable = meta.getConsumable();
            consumable.setAnimation(value);
            meta.setConsumable(consumable);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "animation");
        }
    }

    private void foodConsumeParticles(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "consumeparticles");
                return;
            }

            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumable = meta.getConsumable();
            Boolean value = args.length == 2 ? Boolean.valueOf(!consumable.hasConsumeParticles()) : Aliases.BOOLEAN.convertAlias(args[2]);
            if (value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "consumeparticles");
                return;
            }
            consumable.setConsumeParticles(value);
            meta.setConsumable(consumable);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "consumeparticles");
        }
    }


    public void onFail(@NotNull CommandSender target, @NotNull String alias) {
        Util.sendMessage(target, new ComponentBuilder(getLanguageString("help-header", "", target)).create());
        for (String sub : foodSub) {
            Util.sendMessage(target, new ComponentBuilder(
                    ChatColor.DARK_GREEN + "/" + alias + " " + this.getName() + ChatColor.GREEN + " " + sub + " "
                            + getLanguageString(sub + ".params", "", target))
                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            "/" + alias + " " + this.getName() + " " + sub + " "))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new Text(String.join("\n",
                                    getLanguageStringList(sub + ".description", null, target)))))
                    .create());
        }
    }

    private void foodClear(Player p, ItemStack item, String alias, String[] args) {
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = ItemUtils.getMeta(item);
        if (!meta.hasFood() && !meta.hasConsumable()) {
            return;
        }
        meta.setFood(null);
        meta.setConsumable(null);
        item.setItemMeta(meta);
        updateView(p);
        //TODO feedback?
    }

    //ie food canalwayseat [true/false]
    private void foodCanAlwaysEat(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                sendFailFeedbackForSub(p, alias, "canalwayseat");
                return;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            FoodComponent food = meta.getFood();
            Boolean value = args.length == 2 ? Boolean.valueOf(!food.canAlwaysEat()) : Aliases.BOOLEAN.convertAlias(args[2]);
            if (value == null) {
                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "canalwayseat");
                return;
            }
            food.setCanAlwaysEat(value);
            meta.setFood(food);
            if (!meta.hasConsumable()) {
                meta.setConsumable(meta.getConsumable());
            }
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "canalwayseat");
        }
    }


    //ie food eatticks <amount>
    private void foodEatTicks(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "eatticks");
                return;
            }
            int val = Integer.parseInt(args[2]);
            if (val < 0) {
                val = 0;
            }
            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumable = meta.getConsumable();
            consumable.setConsumeSeconds(val / 20F);
            meta.setConsumable(consumable);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "eatticks");
        }
    }

    //ie food nutrition <amount>
    private void foodNutrition(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "nutrition");
                return;
            }
            int val = Integer.parseInt(args[2]);
            ItemMeta meta = ItemUtils.getMeta(item);
            FoodComponent food = meta.getFood();
            food.setNutrition(val);
            meta.setFood(food);
            if (!meta.hasConsumable()) {
                meta.setConsumable(meta.getConsumable());
            }
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "nutrition");
        }
    }

    //ie food saturation <amount>
    private void foodSaturation(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "saturation");
                return;
            }
            float val = Float.parseFloat(args[2]);
            ItemMeta meta = ItemUtils.getMeta(item);
            FoodComponent food = meta.getFood();
            food.setSaturation(val);
            meta.setFood(food);
            if (!meta.hasConsumable()) {
                meta.setConsumable(meta.getConsumable());
            }
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "saturation");
        }
    }


    //ie food addeffect apply <type> <duration> [amplifier=1] [particles] [ambient] [icon] [chances=100%]
    //ie food addeffect applymany <chances=100%> [<type> <duration> [amplifier=1] [particles] [ambient] [icon]]xN
    //ie food addeffect remove <type>
    //ie food addeffect clear
    //ie food addeffect sound <sound>
    //ie food addeffect teleport [diameter=10]
    private void foodAddEffect(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 3) {
                sendFailFeedbackForSub(p, alias, "addeffect");
                return;
            }
            ConsumableEffect effect;
            String effectType = Aliases.CONSUMABLE_EFFECT.convertAlias(args[2]);
            if (effectType == null) {
                onWrongAlias("wrong-effecttype", p, Aliases.BOOLEAN);
                sendFailFeedbackForSub(p, alias, "addeffect");
                return;
            }
            switch (effectType) {
                case "apply": {
                    try {
                        if (args.length < 4) {
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        PotionEffectType potionEffectType = Aliases.POTION_EFFECT.convertAlias(args[2]);
                        if (potionEffectType == null) {
                            onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        int duration = UtilLegacy.readPotionEffectDurationSecondsToTicks(args[3]);

                        int level = 0;
                        if (args.length >= 5) {
                            level = Integer.parseInt(args[4]) - 1;
                            if ((level < 0) || (level > 127)) {
                                throw new IllegalArgumentException();
                            }
                        }

                        Boolean particles = true;
                        if (args.length >= 6) {
                            particles = Aliases.BOOLEAN.convertAlias(args[5]);
                            if (particles == null) {
                                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                return;
                            }
                        }
                        Boolean ambient = false;
                        if (args.length >= 7) {
                            ambient = Aliases.BOOLEAN.convertAlias(args[6]);
                            if (ambient == null) {
                                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                return;
                            }
                        }
                        Boolean icon = true;
                        if (args.length >= 8) {
                            icon = Aliases.BOOLEAN.convertAlias(args[7]);
                            if (icon == null) {
                                onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                return;
                            }
                        }
                        float chance = 1;
                        if (args.length == 9) {
                            chance = Float.parseFloat(args[8]) / 100;
                            if (chance <= 0 || chance > 1) {
                                throw new IllegalArgumentException();
                            }
                        }
                        if (!p.hasPermission(this.getPermission() + ".bypass_limits")) {
                            level = Math.min(level, 3);
                        }
                        effect = new ApplyEffects(Collections.singletonList(
                                new PotionEffect(potionEffectType, duration, level - 1, ambient, particles, icon)),
                                chance);

                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                case "applymany": {
                    try {
                        if (args.length < 6) {
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        float chance = Float.parseFloat(args[3]) / 100;
                        if (chance <= 0 || chance > 1) {
                            throw new IllegalArgumentException();
                        }
                        List<PotionEffect> potionEffects = new ArrayList<>();
                        PotionEffect potionEffect = null;
                        for (int i = 4; i < args.length; i++) {
                            switch ((i - 4) % 6) {
                                case 0: {
                                    if (potionEffect != null) {
                                        potionEffects.add(potionEffect);
                                    }
                                    PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[i]);
                                    if (type == null) {
                                        onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                        return;
                                    }
                                    potionEffect = new PotionEffect(type, 0, 0);
                                    break;
                                }
                                case 1: {
                                    int duration = UtilLegacy.readPotionEffectDurationSecondsToTicks(args[i]);
                                    potionEffect = new PotionEffect(potionEffect.getType(), duration, 0);
                                    break;
                                }
                                case 2: {
                                    int level = Integer.parseInt(args[i]);
                                    if (!p.hasPermission(this.getPermission() + ".bypass_limits")) {
                                        level = Math.min(level, 3);
                                    }
                                    potionEffect = new PotionEffect(potionEffect.getType(),
                                            potionEffect.getDuration(),
                                            level - 1);
                                    break;
                                }
                                case 3: {
                                    Boolean ambient = Aliases.BOOLEAN.convertAlias(args[i]);
                                    if (ambient == null) {
                                        onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                        return;
                                    }
                                    potionEffect = new PotionEffect(potionEffect.getType(),
                                            potionEffect.getDuration(),
                                            potionEffect.getAmplifier(),
                                            ambient);
                                    break;
                                }
                                case 4: {
                                    Boolean particles = Aliases.BOOLEAN.convertAlias(args[i]);
                                    if (particles == null) {
                                        onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                        return;
                                    }
                                    potionEffect = new PotionEffect(potionEffect.getType(),
                                            potionEffect.getDuration(),
                                            potionEffect.getAmplifier(),
                                            potionEffect.isAmbient(),
                                            particles);
                                    break;
                                }
                                case 5: {
                                    Boolean icon = Aliases.BOOLEAN.convertAlias(args[i]);
                                    if (icon == null) {
                                        onWrongAlias("wrong-boolean", p, Aliases.BOOLEAN);
                                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                        return;
                                    }
                                    potionEffect = new PotionEffect(potionEffect.getType(),
                                            potionEffect.getDuration(),
                                            potionEffect.getAmplifier(),
                                            potionEffect.isAmbient(),
                                            potionEffect.hasParticles(),
                                            icon);
                                    break;
                                }
                            }
                            potionEffects.add(potionEffect);
                        }
                        effect = new ApplyEffects(potionEffects, chance);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                case "remove": {
                    try {
                        if (args.length < 4) {
                            sendFailFeedbackForSubSub(p, alias, "remove", effectType);
                            return;
                        }
                        List<PotionEffectType> types = new ArrayList<>();
                        for (int i = 3; i < args.length; i++) {
                            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[i]);
                            if (type == null) {
                                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                                sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                                return;
                            }
                            types.add(type);
                        }
                        effect = new RemoveEffects(types);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                case "clear": {
                    try {
                        if (args.length != 3) {
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        effect = new ClearEffects();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                case "sound": {
                    try {
                        if (args.length != 4) {
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        Sound sound = Aliases.SOUND.convertAlias(args[3]);
                        if (sound == null) {
                            onWrongAlias("wrong-sound", p, Aliases.SOUND);
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        effect = new PlaySound(sound);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                case "teleport": {
                    try {
                        if (args.length != 4) {
                            sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                            return;
                        }
                        float diameter = Float.parseFloat(args[3]);
                        effect = new TeleportRandomly(diameter);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        sendFailFeedbackForSubSub(p, alias, "addeffect", effectType);
                        return;
                    }
                    break;
                }
                default: {
                    //TODO
                    sendFailFeedbackForSub(p, alias, "addeffect");
                    return;
                }
            }

            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumableComponent = meta.getConsumable();
            List<ConsumableEffect> effects = new ArrayList<>(consumableComponent.getEffects());
            effects.add(effect);
            consumableComponent.setEffects(effects);
            meta.setConsumable(consumableComponent);
            item.setItemMeta(meta);
            p.getInventory().setItemInMainHand(item);
            updateView(p);
        } catch (Exception e) {
            e.printStackTrace();
            sendFailFeedbackForSub(p, alias, "addeffect");
        }
    }

    //ie food removeeffect [number]
    private void foodRemoveEffect(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                sendFailFeedbackForSub(p, alias, "removeeffect");
                return;
            }
            int number;
            number = Integer.parseInt(args[2])-1;
            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumableComponent = meta.getConsumable();
            List<ConsumableEffect> effects = new ArrayList<>(consumableComponent.getEffects());
            effects.remove(number); //TODO check bounds
            consumableComponent.setEffects(effects);
            meta.setConsumable(consumableComponent);
            item.setItemMeta(meta);
            p.getInventory().setItemInMainHand(item);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "removeeffect");
        }
    }

    //ie food cleareffects
    private void foodClearEffects(Player p, ItemStack item, String alias, String[] args) {
        try {
            ItemMeta meta = ItemUtils.getMeta(item);
            ConsumableComponent consumable = meta.getConsumable();
            consumable.setEffects(new ArrayList<>());
            meta.setConsumable(consumable);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "cleareffects");
        }
    }

    //ie food info
    private void foodInfo(Player p, ItemStack item, String alias, String[] args) {
        try {
            ParsedItem parsedItem = new ParsedItem(item);
            if (!item.hasItemMeta() || !(parsedItem.getMap().containsKey(Keys.Component.CROSS_VERSION_CONSUMABLE.toString())
                    || parsedItem.getMap().containsKey("use_remainer"))) {
                sendLanguageString("info.not_food", "", p);
                return;
            }
            ItemStack remainer = VersionUtils.isVersionAfter(1, 21) ? getUseRemainder(item) : null;
            ArrayList<String> list = new ArrayList<>(this.getLanguageStringList("info.message", Collections.emptyList(), p,
                    "%eatseconds%", UtilsString.formatNumber(getEastSeconds(item), 2, false),
                    "%eatticks%", UtilsString.formatNumber(getEastSeconds(item) * 20, 0, false),
                    "%saturation%", UtilsString.formatNumber(getSaturation(item), 2, false),
                    "%nutrition%", String.valueOf(getNutrition(item)),
                    "%canalwayseat%", Aliases.BOOLEAN.getDefaultName(canAlwaysEat(item)),
                    "%convertto%", VersionUtils.isVersionAfter(1, 21) ? (remainer == null ? Aliases.BOOLEAN.getDefaultName(false) :
                            remainer.getType().name() + (remainer.hasItemMeta() ? " [...]" : "")
                                    + (remainer.getAmount() == 1 ? "" : (" x" + remainer.getAmount())))
                            : "&c1.21+",
                    "%consumeparticles%", Aliases.BOOLEAN.getDefaultName(consumeParticles(item)),
                    "%animation%", Aliases.ANIMATION_OLD.getDefaultName(animation(item)),
                    "%sound%", Aliases.SOUND.getDefaultName(sound(item))));

            int appliedEffects = getFoodEffects(item).size();
            if (appliedEffects > 0) {
                list.addAll(this.getLanguageStringList("info.apply_effect_prefix", Collections.emptyList(), p,
                        "%effects%", String.valueOf(getFoodEffects(item).size())));
                int index = 1;
                for (FoodPojo foodEffect : getFoodEffects(item)) {
                    PotionEffect effect = foodEffect.getPotionEffect();
                    list.addAll(this.getLanguageStringList("info.apply_effect", Collections.emptyList(), p,
                            "%index%", String.valueOf(index),
                            "%type%", Aliases.POTION_EFFECT.getDefaultName(effect.getType()),
                            "%level%", String.valueOf(effect.getAmplifier() + 1),
                            "%duration_s%", effect.getDuration() == -1 ? "∞" : UtilsString.formatNumber(effect.getDuration() / 20D, 2, true),
                            "%hasparticle%", Aliases.BOOLEAN.getDefaultName(effect.hasParticles()),
                            "%isambient%", Aliases.BOOLEAN.getDefaultName(effect.isAmbient()),
                            "%hasicon%", Aliases.BOOLEAN.getDefaultName(!VersionUtils.isVersionAfter(1, 13) || effect.hasIcon()),
                            "%duration_ticks%", effect.getDuration() == -1 ? "∞" : String.valueOf(effect.getDuration()),
                            "%chance_perc%", UtilsString.formatNumber(foodEffect.getProbability() * 100, 2, true)
                    ));
                    index++;
                }
            }
            Util.sendMessage(p, String.join("\n", list));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ie food convertto <material/type> [amount]
    private void foodConvertTo(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length > 4) {
                sendFailFeedbackForSub(p, alias, "convertto");
                return;
            }
            ItemStack target = null;
            if (args.length >= 3) {
                String name = args[2];
                target = ItemEdit.get().getServerStorage().getItem(name);
                if (target == null) {
                    try {
                        Material mat = Material.valueOf(name.toUpperCase(Locale.ENGLISH));
                        if (!ItemUtils.isItem(mat)) {
                            throw new IllegalArgumentException();
                        }
                        target = new ItemStack(mat);
                    } catch (IllegalArgumentException e2) {
                        sendFailFeedbackForSub(p, alias, "convertto");
                        return;
                    }
                }
            }
            int amount = 1;
            if (args.length == 4) {
                amount = Integer.parseInt(args[3]);
            }
            if (target != null && target.getType().isAir()) {
                target = null;
            }
            if (target != null) {
                target.setAmount(amount);
            }

            ItemMeta meta = ItemUtils.getMeta(item);
            meta.setUseRemainder(target);
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            sendFailFeedbackForSub(p, alias, "convertto");
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
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        switch (args.length) {
            case 2:
                List<String> list = CompleteUtility.complete(args[1], foodSub);
                if (!VersionUtils.isVersionAfter(1, 21)) {
                    list.remove("convertto");
                }
                return list;
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "canalwayseat":
                        return CompleteUtility.complete(args[2], Aliases.BOOLEAN);
                    case "consumeparticles":
                        if (VersionUtils.isVersionAfter(1, 21, 2)) {
                            return CompleteUtility.complete(args[2], Aliases.BOOLEAN);
                        }
                        return Collections.emptyList();
                    case "animation":
                        if (VersionUtils.isVersionAfter(1, 21, 2)) {
                            return CompleteUtility.complete(args[2], Aliases.ANIMATION_OLD);
                        }
                        return Collections.emptyList();
                    case "sound":
                        if (VersionUtils.isVersionAfter(1, 21, 2)) {
                            return CompleteUtility.complete(args[2], Aliases.SOUND);
                        }
                        return Collections.emptyList();
                    case "eatticks":
                        return CompleteUtility.complete(args[2], "1", "20", "40");
                    case "saturation":
                        return CompleteUtility.complete(args[2], "0", "1", "1.5", "2", "10");
                    case "nutrition":
                        return CompleteUtility.complete(args[2], "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                                "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20");
                    case "addeffect":
                        return CompleteUtility.complete(args[2], Aliases.POTION_EFFECT);
                    case "removeeffect": {
                        ItemStack item = getItemInHand((Player) sender);
                        if (item == null || !item.hasItemMeta()) {
                            return Collections.emptyList();
                        }
                        HashSet<PotionEffectType> types = new HashSet<>();
                        for (FoodPojo food : getFoodEffects(item)) {
                            types.add(food.getPotionEffect().getType());
                        }
                        List<String> list2 = new ArrayList<>();//Util.complete(args[2], Aliases.POTION_EFFECT);
                        for (PotionEffectType type : types) {
                            list2.add(Aliases.POTION_EFFECT.getDefaultName(type));
                        }
                        return CompleteUtility.complete(args[2], list2);
                    }
                    case "convertto":
                        if (VersionUtils.isVersionAfter(1, 21)) {
                            List<String> list2 = CompleteUtility.complete(args[2], Material.class);
                            list2.addAll(CompleteUtility.complete(args[2], ItemEdit.get().getServerStorage().getIds()));
                            return list2;
                        }
                }
                return Collections.emptyList();
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "addeffect":
                        return CompleteUtility.complete(args[3], "infinite", "instant", "1", "90", "180", "480");
                    case "removeeffect":
                        return CompleteUtility.complete(args[3], "1", "2", "3", "4", "5");
                    case "convertto":
                        if (VersionUtils.isVersionAfter(1, 21)) {
                            return CompleteUtility.complete(args[3], "1", "10", "64");
                        }
                }
                return Collections.emptyList();
            case 5:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect")) {
                    return CompleteUtility.complete(args[4], "1", "2", "3", "4", "5");
                }
                return Collections.emptyList();
            case 6:
            case 7:
            case 8:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect")) {
                    return CompleteUtility.complete(args[args.length - 1], Aliases.BOOLEAN);
                }
                return Collections.emptyList();
            case 9:
                if (args[1].toLowerCase(Locale.ENGLISH).equals("addeffect")) {
                    return CompleteUtility.complete(args[8], "0.01", "10.0", "50.0", "100.0");
                }
                return Collections.emptyList();
        }
        return Collections.emptyList();
    }

}
