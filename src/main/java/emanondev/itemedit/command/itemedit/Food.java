package emanondev.itemedit.command.itemedit;

import emanondev.itemedit.*;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.ItemEditCommand;
import emanondev.itemedit.command.SubCmd;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Food extends SubCmd {


    private static final String[] foodSub = new String[]{"clear", "canalwayseat",
            "eatticks", "nutrition", "saturation", "addeffect", "removeeffect", "cleareffects", "info", "convertto",
            "consumeparticles", "animation", "sound"};

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
                foodConsumeParticles(p, item, alias, args);
                return;
            case "animation":
                foodAnimation(p, item, alias, args);
                return;
            case "sound":
                foodSound(p, item, alias, args);
                return;
            case "info":
                foodInfo(p, item, alias, args);
                return;
            case "convertto":
                if (!Util.isVersionAfter(1, 21))
                    onFail(p, alias);
                foodConvertTo(p, item, alias, args);
                return;
            default:
                onFail(p, alias);
        }
    }

    private void foodSound(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                onSubFail(p, alias, "sound");
                return;
            }
            Sound value = Aliases.SOUND.convertAlias(args[2]);
            ParsedItem parsed = new ParsedItem(item);
            parsed.set(value.getKey(), getComponentPath(), "sound");
            p.getInventory().setItemInMainHand(parsed.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "sound");
        }
    }

    private void foodAnimation(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                onSubFail(p, alias, "animation");
                return;
            }
            String value = Aliases.ANIMATION.convertAlias(args[2]);
            if (value == null) {
                onWrongAlias("wrong-animation", p, Aliases.ANIMATION);
                onSubFail(p, alias, "animation");
                return;
            }
            ParsedItem parsed = new ParsedItem(item);
            parsed.set(value, getComponentPath(), "animation");
            p.getInventory().setItemInMainHand(parsed.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "animation");
        }
    }

    private void foodConsumeParticles(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                onSubFail(p, alias, "consumeparticles");
                return;
            }
            Boolean value = args.length == 2 ? !consumeParticles(item) : Aliases.BOOLEAN.convertAlias(args[2]);
            ParsedItem parsed = new ParsedItem(item);
            parsed.set(value, getComponentPath(), "has_consume_particles");
            p.getInventory().setItemInMainHand(parsed.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "consumeparticles");
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
                            new Text(String.join("\n",
                                    getLanguageStringList(sub + ".description", null, target)))))
                    .create());
    }

    private void onSubFail(CommandSender target, String alias, String subCommand) {
        target.spigot().sendMessage(this.craftFailFeedback(alias, subCommand + " " + getLanguageString(subCommand + ".params", null, target),
                getLanguageStringList(subCommand + ".description", null, target)));
    }

    private void foodClear(Player p, ItemStack item, String alias, String[] args) {
        if (!item.hasItemMeta())
            return;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasFood())
            return;
        ParsedItem parsed = new ParsedItem(item);
        parsed.remove(getComponentPath());
        parsed.remove(Keys.Component.FOOD.toString());
        p.getInventory().setItemInMainHand(parsed.toItemStack());
        updateView(p);
        //TODO feedback?
    }

    //ie food canalwayseat [true/false]
    private void foodCanAlwaysEat(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 2 && args.length != 3) {
                onSubFail(p, alias, "canalwayseat");
                return;
            }
            Boolean value = args.length == 2 ? !canAlwaysEat(item) : Aliases.BOOLEAN.convertAlias(args[2]);
            ParsedItem parsed = new ParsedItem(item);
            parsed.loadEmptyMap(Keys.Component.CONSUMABLE.toString());
            parsed.set(value, Keys.Component.FOOD.toString(), "can_always_eat");
            parsed.load(0, Keys.Component.FOOD.toString(), "nutrition");
            parsed.load(0, Keys.Component.FOOD.toString(), "saturation");
            p.getInventory().setItemInMainHand(parsed.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "canalwayseat");
        }
    }

    private ItemStack setFoodEffects(ItemStack stack, List<FoodPojo> value) {
        ParsedItem parsed = new ParsedItem(stack);
        Map<String, Object> componentMap = value == null ? ParsedItem.getMap(parsed.getMap(), getComponentPath()) :
                ParsedItem.loadMap(parsed.getMap(), getComponentPath());
        if (componentMap == null)
            return stack;
        if (Util.isVersionUpTo(1, 21, 1)) {
            if (value == null)
                componentMap.remove("effects");
            List<Map<String, Object>> effects = ParsedItem.loadListOfMap(componentMap, "effects");
            value.forEach((foodPojo -> {
                LinkedHashMap<String, Object> potionMap = new LinkedHashMap<>();
                ParsedItem.setValue(potionMap, "duration", 30);
                ParsedItem.setValue(potionMap, "amplifier", 0);
                ParsedItem.setValue(potionMap, "ambient", false);
                ParsedItem.setValue(potionMap, "show_particles", true);
                ParsedItem.setValue(potionMap, "show_icon", true);
                LinkedHashMap<String, Object> effectMap = new LinkedHashMap<>();
                effectMap.put("effect", potionMap);
                ParsedItem.setValue(effectMap, "probability", foodPojo.getProbability());
                effects.add(effectMap);
            }));
            ItemStack result = parsed.toItemStack();
            result.setAmount(stack.getAmount());
            return result;
        }
        List<Map<String, Object>> consumeEffects = ParsedItem.getListOfMap(componentMap, "on_consume_effects");
        //[{effects:[{ambient:1b,amplifier:1b,duration:1,id:"minecraft:wither",show_icon:0b,show_particles:0b}],probability:0.08f,type:"minecraft:apply_effects"}]
        consumeEffects.removeIf(map -> ParsedItem.readNamespacedKey(map, "type")
                .equals(new NamespacedKey(NamespacedKey.MINECRAFT, "apply_effects")));
        value.forEach((food -> {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            ParsedItem.setValue(map, "type", new NamespacedKey(NamespacedKey.MINECRAFT, "apply_effects"));
            ParsedItem.setValue(map, "probability", food.getProbability());
            PotionEffect pot = food.getPotionEffect();
            LinkedHashMap<String, Object> subMap = new LinkedHashMap<>();
            ParsedItem.setValue(subMap, "id", pot.getType());
            ParsedItem.setValue(subMap, "duration", pot.getDuration());
            ParsedItem.setValue(subMap, "amplifier", pot.getAmplifier());
            ParsedItem.setValue(subMap, "ambient", pot.isAmbient());
            ParsedItem.setValue(subMap, "show_particles", pot.hasParticles());
            ParsedItem.setValue(subMap, "show_icon", pot.hasIcon());
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            list.add(subMap);
            map.put("effects", list);
            consumeEffects.add(map);
        }));
        if (consumeEffects.isEmpty())
            componentMap.remove("on_consume_effects");
        ItemStack result = parsed.toItemStack();
        result.setAmount(stack.getAmount());
        return result;
    }

    private List<FoodPojo> getFoodEffects(ItemStack stack) {
        ParsedItem parsed = new ParsedItem(stack);
        Map<String, Object> componentMap = ParsedItem.getMap(parsed.getMap(), getComponentPath());
        if (componentMap == null)
            return new ArrayList<>();
        if (Util.isVersionUpTo(1, 21, 1)) {
            List<Map<String, Object>> effects = ParsedItem.getListOfMap(componentMap, "effects");
            if (effects == null)
                return new ArrayList<>();
            //{effect:{id:wither,duration:1,amplifier:2,ambient:1b,show_particles:0b,show_icon:0b},probability:0.01f}
            List<FoodPojo> list = new ArrayList<>();
            effects.forEach((map) -> {
                Map<String, Object> subMap = ParsedItem.getMap(map, "effect");
                list.add(new FoodPojo(new PotionEffect(Registry.EFFECT.get(ParsedItem.readNamespacedKey(subMap, "id")),
                        ParsedItem.readInt(subMap, "duration", 30),
                        ParsedItem.readInt(subMap, "amplifier", 0),
                        ParsedItem.readBoolean(subMap, "ambient", false),
                        ParsedItem.readBoolean(subMap, "show_particles", true),
                        ParsedItem.readBoolean(subMap, "show_icon", true)
                ),
                        ParsedItem.readFloat(map, "probability", 1f)));
            });
            return list;
        }
        //{on_consume_effects:[{type:apply_effects,effects:[{id:wither,duration:1,amplifier:1,ambient:1b,show_particles:0b,show_icon:0b}]}],nutrition:1,saturation:1}
        List<Map<String, Object>> consumeEffects = ParsedItem.getListOfMap(componentMap, "on_consume_effects");
        if (consumeEffects.isEmpty())
            return new ArrayList<>();
        List<FoodPojo> list = new ArrayList<>();
        consumeEffects.forEach((map) -> {
            if (!ParsedItem.readNamespacedKey(map, "type").equals(new NamespacedKey(NamespacedKey.MINECRAFT, "apply_effects")))
                return;
            float probability = ParsedItem.readFloat(map, "probability", 1f);
            ParsedItem.loadListOfMap(map, "effects").forEach(effect -> {
                list.add(new FoodPojo(new PotionEffect(Registry.EFFECT.get(ParsedItem.readNamespacedKey(effect, "id")),
                        ParsedItem.readInt(effect, "duration", 30),
                        ParsedItem.readInt(effect, "amplifier", 0),
                        ParsedItem.readBoolean(effect, "ambient", false),
                        ParsedItem.readBoolean(effect, "show_particles", true),
                        ParsedItem.readBoolean(effect, "show_icon", true)
                ),
                        probability));
            });
        });
        return list;
    }

    private boolean canAlwaysEat(ItemStack stack) {
        ParsedItem parsed = new ParsedItem(stack);
        return parsed.readBoolean(false, Keys.Component.FOOD.toString(), "can_always_eat");
    }


    //ie food eatticks <amount>
    private void foodEatTicks(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                onSubFail(p, alias, "eatticks");
                return;
            }
            int val = Integer.parseInt(args[2]);
            if (val < 0)
                val = 0;

            ParsedItem parsedItem = new ParsedItem(item);
            parsedItem.loadEmptyMap(getComponentPath());
            parsedItem.set((float) val / 20, getComponentPath(), "consume_seconds");
            p.getInventory().setItemInMainHand(parsedItem.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "eatticks");
        }
    }

    private String getComponentPath() {
        if (Util.isVersionUpTo(1, 21, 1)) {
            return Keys.Component.FOOD.toString();
        }
        return Keys.Component.CONSUMABLE.toString();
    }

    private float getEastSeconds(ItemStack stack) {
        ParsedItem parsed = new ParsedItem(stack);
        return parsed.readFloat(1.6F, getComponentPath(), "consume_seconds");
    }


    //ie food nutrition <amount>
    private void foodNutrition(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                onSubFail(p, alias, "nutrition");
                return;
            }
            int val = Integer.parseInt(args[2]);
            ParsedItem parsedItem = new ParsedItem(item);
            parsedItem.loadEmptyMap(getComponentPath());
            parsedItem.set(val, Keys.Component.FOOD.toString(), "nutrition");
            parsedItem.load(0, Keys.Component.FOOD.toString(), "saturation");
            p.getInventory().setItemInMainHand(parsedItem.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "nutrition");
        }
    }

    //ie food saturation <amount>
    private void foodSaturation(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3) {
                onSubFail(p, alias, "saturation");
                return;
            }
            float val = Float.parseFloat(args[2]);
            ParsedItem parsedItem = new ParsedItem(item);
            parsedItem.loadEmptyMap(getComponentPath());
            parsedItem.set(0, Keys.Component.FOOD.toString(), "nutrition");
            parsedItem.load(val, Keys.Component.FOOD.toString(), "saturation");
            p.getInventory().setItemInMainHand(parsedItem.toItemStack());
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "saturation");
        }
    }


    //ie food addeffect <type> <duration> [amplifier=1] [particles] [ambient] [icon] [chances=100%]
    private void foodAddEffect(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length < 4 || args.length > 9) {
                onSubFail(p, alias, "addeffect");
                return;
            }
            PotionEffectType effect = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (effect == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                onSubFail(p, alias, "addeffect");
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
                chance = Float.parseFloat(args[8]) / 100;
                if (chance <= 0 || chance > 1)
                    throw new IllegalArgumentException();
            }
            if (!p.hasPermission(this.getPermission() + ".bypass_limits"))
                level = Math.min(level, 1);
            List<FoodPojo> values = getFoodEffects(item);
            values.add(new FoodPojo(
                    new PotionEffect(effect, duration, level, ambient, particles, icon)
                    , chance));
            p.getInventory().setItemInMainHand(setFoodEffects(item, values));
            updateView(p);
        } catch (Exception e) {
            e.printStackTrace();
            onSubFail(p, alias, "addeffect");
        }
    }

    //ie food removeeffect <type> [amplifier]
    private void foodRemoveEffect(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length != 3 && args.length != 4) {
                onSubFail(p, alias, "removeeffect");
                return;
            }
            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (type == null) {
                onWrongAlias("wrong-effect", p, Aliases.POTION_EFFECT);
                onSubFail(p, alias, "removeeffect");
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
            List<FoodPojo> values = getFoodEffects(item);
            values.removeIf(effect -> effect.getPotionEffect().getType().equals(type) &&
                    (level == null || effect.getPotionEffect().getAmplifier() == level));
            p.getInventory().setItemInMainHand(setFoodEffects(item, values));
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "removeeffect");
        }
    }

    //ie food cleareffects
    private void foodClearEffects(Player p, ItemStack item, String alias, String[] args) {
        try {
            p.getInventory().setItemInMainHand(setFoodEffects(item, null));
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "cleareffects");
        }
    }

    //ie food info
    private void foodInfo(Player p, ItemStack item, String alias, String[] args) {
        try {
            ItemMeta meta = item.getItemMeta();
            ParsedItem parsedItem = new ParsedItem(item);
            if (!item.hasItemMeta() || !(parsedItem.getMap().containsKey(getComponentPath()) || parsedItem.getMap().containsKey("use_remainer"))) {
                sendLanguageString("info.not_food", "", p);
                return;
            }
            //FoodComponent food = meta.getFood();
            //TODO eattime placeholder
            ArrayList<String> list = new ArrayList<>(this.getLanguageStringList("info.message", Collections.emptyList(), p,
                    "%eatseconds%", UtilsString.formatNumber(getEastSeconds(item), 2, false),
                    "%eatticks%", UtilsString.formatNumber(getEastSeconds(item) * 20, 0, false),
                    "%saturation%", UtilsString.formatNumber(getSaturation(item), 2, false),
                    "%nutrition%", String.valueOf(getNutrition(item)),
                    "%canalwayseat%", Aliases.BOOLEAN.getDefaultName(canAlwaysEat(item)),
                    "%convertto%", Util.isVersionAfter(1, 21) ? (meta.getUseRemainder() == null ? Aliases.BOOLEAN.getDefaultName(false) :
                            meta.getUseRemainder().getType().name() + (meta.getUseRemainder().hasItemMeta() ? " [...]" : "")
                                    + (meta.getUseRemainder().getAmount() == 1 ? "" : (" x" + meta.getUseRemainder().getAmount())))
                            : "&c1.21+ only",
                    "%effects%", String.valueOf(getFoodEffects(item).size()),
                    "%consumeparticles%", Aliases.BOOLEAN.getDefaultName(consumeParticles(item)),
                    "%animation%", Aliases.ANIMATION.getDefaultName(animation(item)),
                    "%sound%", Aliases.SOUND.getDefaultName(sound(item)),
                    "%effects%", String.valueOf(getFoodEffects(item).size())));
            int index = 1;
            for (FoodPojo foodEffect : getFoodEffects(item)) {
                PotionEffect effect = foodEffect.getPotionEffect();
                list.addAll(this.getLanguageStringList("info.effect", Collections.emptyList(), p,
                        "%index%", String.valueOf(index),
                        "%type%", Aliases.POTION_EFFECT.getDefaultName(effect.getType()),
                        "%level%", String.valueOf(effect.getAmplifier() + 1),
                        "%duration_s%", effect.getDuration() == -1 ? "∞" : UtilsString.formatNumber(effect.getDuration() / 20D, 2, true),
                        "%hasparticle%", Aliases.BOOLEAN.getDefaultName(effect.hasParticles()),
                        "%isambient%", Aliases.BOOLEAN.getDefaultName(effect.isAmbient()),
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

    private String animation(ItemStack item) {
        ParsedItem parsedItem = new ParsedItem(item);
        return parsedItem.readString("eat", getComponentPath(), "animation");
    }

    private Sound sound(ItemStack item) {
        ParsedItem parsedItem = new ParsedItem(item);
        String val = parsedItem.readString((String) null, getComponentPath(), "sound");
        if (val == null)
            return Sound.ENTITY_GENERIC_EAT;
        try {
            Sound value = Registry.SOUNDS.get(new NamespacedKey(val.split(":")[0], val.split(":")[1]));
            return value == null ? Sound.ENTITY_GENERIC_EAT : value;
        } catch (Exception e) {
            return Sound.ENTITY_GENERIC_EAT;
        }
    }

    private boolean consumeParticles(ItemStack item) {
        ParsedItem parsedItem = new ParsedItem(item);
        return parsedItem.readBoolean(true, getComponentPath(), "has_consume_particles");
    }

    private float getSaturation(ItemStack item) {
        ParsedItem parsedItem = new ParsedItem(item);
        Map<String, Object> food = ParsedItem.getMap(parsedItem.getMap(), "food");
        if (food == null || !food.containsKey("saturation"))
            return 0;
        return ParsedItem.readFloat(food, "saturation", 0f);
    }

    private int getNutrition(ItemStack item) {
        ParsedItem parsedItem = new ParsedItem(item);
        Map<String, Object> food = ParsedItem.getMap(parsedItem.getMap(), "food");
        if (food == null || !food.containsKey("nutrition"))
            return 0;
        return ParsedItem.readInt(food, "nutrition", 0);
    }

    //ie food convertto <material/type> [amount]
    private void foodConvertTo(Player p, ItemStack item, String alias, String[] args) {
        try {
            if (args.length > 4) {
                onSubFail(p, alias, "convertto");
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
                        onSubFail(p, alias, "convertto");
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
            meta.setUseRemainder(target);//TODO
            item.setItemMeta(meta);
            updateView(p);
        } catch (Exception e) {
            onSubFail(p, alias, "convertto");
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
                if (!Util.isVersionAfter(1, 21))
                    list.remove("convertto");
                return list;
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "canalwayseat":
                    case "consumeparticles":
                        return Util.complete(args[2], Aliases.BOOLEAN);
                    case "animation":
                        return Util.complete(args[2], Aliases.ANIMATION);
                    case "sound":
                        return Util.complete(args[2], Aliases.SOUND);
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
                        if (item == null || !item.hasItemMeta())
                            return Collections.emptyList();
                        HashSet<PotionEffectType> types = new HashSet<>();
                        for (FoodPojo food : getFoodEffects(item))
                            types.add(food.getPotionEffect().getType());
                        List<String> list2 = new ArrayList<>();//Util.complete(args[2], Aliases.POTION_EFFECT);
                        for (PotionEffectType type : types)
                            list2.add(Aliases.POTION_EFFECT.getDefaultName(type));
                        return Util.complete(args[2], list2);
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
                        return Util.complete(args[3], "infinite", "0", "90", "180", "480");
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


    private class FoodPojo {
        private PotionEffect potionEffect;
        private float probability;

        public FoodPojo(PotionEffect potionEffect, float probability) {
            this.potionEffect = potionEffect;
            this.probability = probability;
        }

        public PotionEffect getPotionEffect() {
            return potionEffect;
        }

        public void setPotionEffect(PotionEffect potionEffect) {
            this.potionEffect = potionEffect;
        }

        public float getProbability() {
            return probability;
        }

        public void setProbability(float probability) {
            this.probability = probability;
        }
    }
}
