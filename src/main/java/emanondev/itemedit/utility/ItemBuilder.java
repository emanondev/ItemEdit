package emanondev.itemedit.utility;

import emanondev.itemedit.Keys;
import emanondev.itemedit.ParsedItem;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemBuilder {

    private @NotNull ItemStack stack;
    private @NotNull ItemMeta meta;

    public ItemBuilder(@NotNull Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder(@NotNull ItemStack itemStack) {
        this.stack = itemStack;
        this.meta = Objects.requireNonNull(itemStack.getItemMeta());
    }

    public ItemStack build() {
        stack.setItemMeta(meta);
        return stack;
    }

    //GENERIC

    public ItemStack getConvertsTo() {
        if (!VersionUtils.isVersionAfter(1, 21)) {
            throw new UnsupportedOperationException();
        }
        return meta.getUseRemainder();
    }

    public ItemBuilder setConvertsTo(ItemStack itemStack) {
        if (!VersionUtils.isVersionAfter(1, 21)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 2)) {
            FoodComponent food = meta.getFood();
            try {
                food.getClass().getMethod("setUsingConvertsTo", ItemStack.class).invoke(food, itemStack);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            meta.setFood(food);
            return this;
        }
        meta.setUseRemainder(itemStack);
        return this;
    }

    //CONSUMABLE COMPONENT

    public ItemBuilder addConsumeEffect(ConsumableEffect effect) {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        List<ConsumableEffect> effects = new ArrayList<>(consumableComponent.getEffects());
        effects.add(effect);
        consumableComponent.setEffects(effects);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ConsumableComponent.Animation getConsumeAnimation() {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        if (!meta.hasConsumable()) {
            return null;
        }
        return meta.getConsumable().getAnimation();
    }

    @Deprecated
    public String getConsumeAnimationName() {
        if (!VersionUtils.isVersionInRange(1, 20, 5, 1, 21, 3)) {
            throw new UnsupportedOperationException();
        }
        stack.setItemMeta(meta);
        ParsedItem parsedItem = new ParsedItem(stack);
        return parsedItem.readString("eat", Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "animation");
    }

    @Deprecated
    public ItemBuilder setConsumeAnimationName(String animationName) {
        if (!VersionUtils.isVersionInRange(1, 20, 5, 1, 21, 3)) {
            throw new UnsupportedOperationException();
        }
        stack.setItemMeta(meta);
        ParsedItem parsedItem = new ParsedItem(stack);
        parsedItem.set(animationName, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "animation");
        stack = parsedItem.toItemStack();
        meta = Objects.requireNonNull(stack.getItemMeta());
        return this;
    }

    public float getConsumeSeconds() {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            return parsedItem.readFloat(1.6F, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "consume_seconds");
        }
        if (!meta.hasConsumable()) {
            return 1.6F;
        }
        return meta.getConsumable().getConsumeSeconds();
    }

    public List<ConsumableEffect> getConsumeEffects() {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        if (!meta.hasConsumable()) {
            return Collections.emptyList();
        }
        return meta.getConsumable().getEffects();

    }

    public Sound getConsumeSound() {
        if (!VersionUtils.isVersionAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            String val = parsedItem.readString((String) null, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "sound");
            if (val == null) {
                return Sound.ENTITY_GENERIC_EAT;
            }
            try {
                Sound value = Registry.SOUNDS.get(new NamespacedKey(val.split(":")[0], val.split(":")[1]));
                return value == null ? Sound.ENTITY_GENERIC_EAT : value;
            } catch (Exception e) {
                return Sound.ENTITY_GENERIC_EAT;
            }
        }
        if (!meta.hasConsumable()) {
            return null;
        }
        return meta.getConsumable().getSound();
    }

    public boolean hasConsumeParticles() {
        if (!VersionUtils.isVersionAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            return parsedItem.readBoolean(true, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "has_consume_particles");
        }
        if (!meta.hasConsumable()) {
            return true;
        }
        return meta.getConsumable().hasConsumeParticles();
    }

    public ItemBuilder setConsumeAnimation(ConsumableComponent.Animation animation) {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        consumableComponent.setAnimation(animation);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ItemBuilder setConsumeParticles(boolean consumeParticles) {
        if (!VersionUtils.isVersionAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            parsedItem.set(consumeParticles, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "has_consume_particles");
            stack = parsedItem.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        consumableComponent.setConsumeParticles(consumeParticles);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ItemBuilder setConsumeSeconds(float consumeSeconds) {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            parsedItem.loadEmptyMap(Keys.Component.CROSS_VERSION_CONSUMABLE.toString());
            parsedItem.set(consumeSeconds, Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "consume_seconds");
            stack = parsedItem.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        consumableComponent.setConsumeSeconds(consumeSeconds);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ItemBuilder setConsumeEffects(List<ConsumableEffect> effects) {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        consumableComponent.setEffects(effects);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ItemBuilder setConsumeSound(Sound sound) {
        if (!VersionUtils.isVersionAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsed = new ParsedItem(stack);
            parsed.set(((Keyed) sound).getKey(), Keys.Component.CROSS_VERSION_CONSUMABLE.toString(), "sound");
            stack = parsed.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        ConsumableComponent consumableComponent = meta.getConsumable();
        consumableComponent.setSound(sound);
        meta.setConsumable(consumableComponent);
        return this;
    }

    public ItemBuilder setConsumableComponent(ConsumableComponent consumable) {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        meta.setConsumable(consumable);
        return this;
    }

    public ItemBuilder writeConsumableComponent() {
        return setConsumableComponent(meta.getConsumable());
    }

    public ItemBuilder clearConsumableComponent() {
        return setConsumableComponent(null);
    }

    public ConsumableComponent getConsumableComponent() {
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        return meta.getConsumable();
    }

    //FOOD COMPONENT

    public boolean canAlwaysEat() {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsed = new ParsedItem(stack);
            return parsed.readBoolean(false, Keys.Component.FOOD.toString(), "can_always_eat");
        }
        if (!meta.hasFood()) {
            return false;
        }
        FoodComponent food = meta.getFood();
        return food.canAlwaysEat();
    }

    public int getNutrition() {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            Map<String, Object> food = ParsedItem.getMap(parsedItem.getMap(), "food");
            if (food == null || !food.containsKey("nutrition")) {
                return 0;
            }
            return ParsedItem.readInt(food, "nutrition", 0);
        }

        if (!meta.hasFood()) {
            return 0;
        }
        FoodComponent food = meta.getFood();
        return food.getNutrition();
    }

    public float getSaturation() {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            Map<String, Object> food = ParsedItem.getMap(parsedItem.getMap(), "food");
            if (food == null || !food.containsKey("saturation")) {
                return 0F;
            }
            return ParsedItem.readFloat(food, "saturation", 0f);
        }

        if (!meta.hasFood()) {
            return 0F;
        }
        FoodComponent food = meta.getFood();
        return food.getSaturation();
    }

    public ItemBuilder setCanAlwaysEat(boolean canAlwaysEat) {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            parsedItem.loadEmptyMap(Keys.Component.CROSS_VERSION_CONSUMABLE.toString());
            parsedItem.set(canAlwaysEat, Keys.Component.FOOD.toString(), "nutrition");
            parsedItem.load(0F, Keys.Component.FOOD.toString(), "saturation");
            stack = parsedItem.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        FoodComponent food = meta.getFood();
        food.setCanAlwaysEat(canAlwaysEat);
        meta.setFood(food);
        if (!meta.hasConsumable()) {
            meta.setConsumable(meta.getConsumable());
        }
        return this;
    }

    public ItemBuilder setNutrition(int nutrition) {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            parsedItem.loadEmptyMap(Keys.Component.CROSS_VERSION_CONSUMABLE.toString());
            parsedItem.set(nutrition, Keys.Component.FOOD.toString(), "nutrition");
            parsedItem.load(0F, Keys.Component.FOOD.toString(), "saturation");
            stack = parsedItem.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        FoodComponent food = meta.getFood();
        food.setNutrition(nutrition);
        meta.setFood(food);
        if (!meta.hasConsumable()) {
            meta.setConsumable(meta.getConsumable());
        }
        return this;
    }

    public ItemBuilder setSaturation(float saturation) {
        if (!VersionUtils.isVersionAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isVersionAfter(1, 21, 4)) {
            stack.setItemMeta(meta);
            ParsedItem parsedItem = new ParsedItem(stack);
            parsedItem.loadEmptyMap(Keys.Component.CROSS_VERSION_CONSUMABLE.toString());
            parsedItem.set(0, Keys.Component.FOOD.toString(), "nutrition");
            parsedItem.load(saturation, Keys.Component.FOOD.toString(), "saturation");
            stack = parsedItem.toItemStack();
            meta = Objects.requireNonNull(stack.getItemMeta());
            return this;
        }
        FoodComponent food = meta.getFood();
        food.setSaturation(saturation);
        meta.setFood(food);
        if (!meta.hasConsumable()) {
            meta.setConsumable(meta.getConsumable());
        }
        return this;
    }

    public ItemBuilder setFoodComponent(FoodComponent food) {
        meta.setFood(food);
        return this;
    }

    public ItemBuilder writeFoodComponent() {
        return setFoodComponent(meta.getFood());
    }

    public ItemBuilder clearFoodComponent() {
        return setFoodComponent(null);
    }

    public FoodComponent getFoodComponent() {
        return meta.getFood();
    }
}
