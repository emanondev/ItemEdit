package emanondev.itemedit.utility;

import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
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

    public ItemStack getConvertsTo() {
        if (!VersionUtils.isAfter(1, 21)) {
            throw new UnsupportedOperationException();
        }
        return meta.getUseRemainder();
    }

    public ItemBuilder setConvertsTo(ItemStack itemStack) {
        if (!VersionUtils.isAfter(1, 21)) {
            throw new UnsupportedOperationException();
        }
        if (!VersionUtils.isAfter(1, 21, 2)) {
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

    public ItemBuilder addConsumeEffect(@NotNull ConsumeEffect effect) {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().addEffect(effect).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemUseAnimation getConsumeAnimation() {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).animation();
    }

    public float getConsumeSeconds() {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).consumeSeconds();
    }

    public List<ConsumeEffect> getConsumeEffects() {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).consumeEffects();

    }

    public Key getConsumeSound() {
        if (!VersionUtils.isAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).sound();
    }

    public boolean hasConsumeParticles() {
        if (!VersionUtils.isAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).hasConsumeParticles();
    }

    public ItemBuilder setConsumeAnimation(ItemUseAnimation animation) {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().animation(animation).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemBuilder setConsumeParticles(boolean consumeParticles) {
        if (!VersionUtils.isAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().hasConsumeParticles(consumeParticles).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemBuilder setConsumeSeconds(float consumeSeconds) {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().consumeSeconds(consumeSeconds).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemBuilder setConsumeEffects(List<ConsumeEffect> effects) {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().effects(effects).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemBuilder setConsumeSound(Sound sound){
        return setConsumeSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(sound)));
    }

    public ItemBuilder setConsumeSound(Key sound) {
        if (!VersionUtils.isAfter(1, 21, 2)) {
            throw new UnsupportedOperationException();
        }
        Consumable consumable = stack.getDataOrDefault(DataComponentTypes.CONSUMABLE,
                Consumable.consumable().build()).toBuilder().sound(sound).build();
        stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        return this;
    }

    public ItemBuilder setConsumableComponent(@Nullable Consumable consumable) {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        if (consumable == null) {
            stack.unsetData(DataComponentTypes.CONSUMABLE);
        } else {
            stack.setData(DataComponentTypes.CONSUMABLE, consumable);
        }
        return this;
    }

    public ItemBuilder clearConsumableComponent() {
        return setConsumableComponent(null);
    }

    public Consumable getConsumableComponent() {
        if (!VersionUtils.isAfter(1, 21, 4)) {
            throw new UnsupportedOperationException();
        }
        return stack.getData(DataComponentTypes.CONSUMABLE);
    }

    //FOOD COMPONENT

    public boolean canAlwaysEat() {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).canAlwaysEat();
    }

    public int getNutrition() {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).nutrition();
    }

    public float getSaturation() {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        return stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).saturation();
    }

    public ItemBuilder setCanAlwaysEat(boolean canAlwaysEat) {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        FoodProperties food = stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).toBuilder().canAlwaysEat(canAlwaysEat).build();
        stack.setData(DataComponentTypes.FOOD, food);
        return this;
    }

    public ItemBuilder setNutrition(int nutrition) {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        FoodProperties food = stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).toBuilder().nutrition(nutrition).build();
        stack.setData(DataComponentTypes.FOOD, food);
        return this;
    }

    public ItemBuilder setSaturation(float saturation) {
        if (!VersionUtils.isAfter(1, 20, 5)) {
            throw new UnsupportedOperationException();
        }
        FoodProperties food = stack.getDataOrDefault(DataComponentTypes.FOOD,
                FoodProperties.food().build()).toBuilder().saturation(saturation).build();
        stack.setData(DataComponentTypes.FOOD, food);
        return this;
    }

    public ItemBuilder setFoodComponent(FoodProperties food) {
        if (food != null) {
            stack.setData(DataComponentTypes.FOOD, food);
        } else {
            stack.unsetData(DataComponentTypes.FOOD);
        }
        return this;
    }

    public ItemBuilder clearFoodComponent() {
        return setFoodComponent(null);
    }

    public FoodProperties getFoodComponent() {
        return stack.getData(DataComponentTypes.FOOD);
    }

    public ItemBuilder setKineticContactCooldownTicks(int ticks) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();
        if (weapon != null) {
            inheritProperties(builder, weapon);
        }
        builder.delayTicks(ticks);
        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticDamageMultiplier(float multiplier) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.damageMultiplier(multiplier);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticDelayTicks(int ticks) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.delayTicks(ticks);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticForwardMovement(float multiplier) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.forwardMovement(multiplier);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticHitSound(Sound value) {
        return setKineticHitSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }


    public ItemBuilder setKineticHitSound(Key value) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.hitSound(value);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticSound(Sound value) {
        return setKineticSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }

    public ItemBuilder setKineticSound(Key value) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.sound(value);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticDamageConditions(KineticWeapon.Condition condition) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.damageConditions(condition);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticDismountConditions(KineticWeapon.Condition condition) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.dismountConditions(condition);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setKineticKnockbackConditions(KineticWeapon.Condition condition) {
        KineticWeapon weapon = stack.getData(DataComponentTypes.KINETIC_WEAPON);
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.knockbackConditions(condition);

        stack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setPiercingDismounts(boolean value) {
        PiercingWeapon weapon = stack.getData(DataComponentTypes.PIERCING_WEAPON);
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.dismounts(value);

        stack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setPiercingDealsKnockback(boolean value) {
        PiercingWeapon weapon = stack.getData(DataComponentTypes.PIERCING_WEAPON);
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.dealsKnockback(value);

        stack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
        return this;
    }


    public ItemBuilder setPiercingHitSound(Sound value) {
        return setPiercingHitSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }

    public ItemBuilder setPiercingHitSound(Key value) {
        PiercingWeapon weapon = stack.getData(DataComponentTypes.PIERCING_WEAPON);
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.hitSound(value);

        stack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
        return this;
    }

    public ItemBuilder setPiercingSound(Sound value) {
        return setPiercingSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }

    public ItemBuilder setPiercingSound(Key value) {
        PiercingWeapon weapon = stack.getData(DataComponentTypes.PIERCING_WEAPON);
        PiercingWeapon.Builder builder = PiercingWeapon.piercingWeapon();

        if (weapon != null) {
            inheritProperties(builder, weapon);
        }

        builder.sound(value);

        stack.setData(DataComponentTypes.PIERCING_WEAPON, builder.build());
        return this;
    }

    public boolean isPiercingDismounts() {
        return stack.getDataOrDefault(DataComponentTypes.PIERCING_WEAPON,
                PiercingWeapon.piercingWeapon().build()).dismounts();
    }

    public boolean isPiercingDealsKnockback() {
        return stack.getDataOrDefault(DataComponentTypes.PIERCING_WEAPON,
                PiercingWeapon.piercingWeapon().build()).dealsKnockback();
    }

    public ItemBuilder setEnchantmentGlintOverride(Boolean value) {
        meta.setEnchantmentGlintOverride(value);
        return this;
    }

    public Boolean getEnchantmentGlintOverride() {
        if (meta.hasEnchantmentGlintOverride()) {
            return meta.getEnchantmentGlintOverride();
        }
        return null;
    }

    public boolean isEquippableCanBeSheared() {
        return stack.getDataOrDefault(DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HEAD).build()).canBeSheared();
    }

    public ItemBuilder setEquippableCanBeSheared(boolean canBeSheared) {
        Equippable.Builder builder = stack.getDataOrDefault(DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()).toBuilder();
        builder.canBeSheared(canBeSheared);
        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder clearEquippable() {
        stack.unsetData(DataComponentTypes.EQUIPPABLE);
        return this;
    }

    public ItemBuilder setEquippableSlot(EquipmentSlot slot) {
        Equippable equippable = stack.getData(DataComponentTypes.EQUIPPABLE);
        if (equippable != null && equippable.slot().equals(slot)) {
            return this;
        }
        Equippable.Builder builder = Equippable.equippable(slot);
        if (equippable != null) {
            inheritProperties(builder, equippable);
        }
        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder setEquippableEquipSound(Sound value) {
        return setEquippableEquipSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }

        public ItemBuilder setEquippableEquipSound(Key value) {
        Equippable.Builder builder = stack.getDataOrDefault(DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()).toBuilder();
        builder.equipSound(value);
        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder setEquippableShearingSound(Sound value) {
        return setEquippableShearingSound(RegistryKey.SOUND_EVENT.typedKey(Registry.SOUNDS.getKeyOrThrow(value)));
    }

    public ItemBuilder setEquippableShearingSound(Key value) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.shearSound(value);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder setEquippableAllowedEntities(EntityType entityType) {
        return setEquippableAllowedEntities(RegistrySet.keySet(RegistryKey.ENTITY_TYPE, Stream.of(entityType)
                .map(e -> TypedKey.create(RegistryKey.ENTITY_TYPE, e.getKey())).toList()));
    }

    public ItemBuilder setEquippableAllowedEntities(RegistryKeySet<@NotNull EntityType> entityType) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();
        builder.allowedEntities(entityType);
        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder setEquippableAllowedEntities(Collection<EntityType> entityType) {
        return setEquippableAllowedEntities(RegistrySet.keySet(RegistryKey.ENTITY_TYPE, entityType.stream()
                .map(e -> TypedKey.create(RegistryKey.ENTITY_TYPE, e.getKey())).toList()));
    }

    public boolean isEquipmentSwappable() {
        Equippable eq = stack.getData(DataComponentTypes.EQUIPPABLE);
        return eq != null && eq.swappable();
    }

    public ItemBuilder setEquipmentSwappable(boolean value) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.swappable(value);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public boolean isEquipmentDispensable() {
        Equippable eq = stack.getData(DataComponentTypes.EQUIPPABLE);
        return eq != null && eq.dispensable();
    }

    public ItemBuilder setEquipmentDispensable(boolean value) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.dispensable(value);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public boolean isEquipmentEquipOnInteract() {
        Equippable eq = stack.getData(DataComponentTypes.EQUIPPABLE);
        return eq != null && eq.equipOnInteract();
    }

    public ItemBuilder setEquipmentEquipOnInteract(boolean value) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.equipOnInteract(value);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public boolean isEquipmentDamageOnHurt() {
        Equippable eq = stack.getData(DataComponentTypes.EQUIPPABLE);
        return eq != null && eq.damageOnHurt();
    }

    public ItemBuilder setEquipmentDamageOnHurt(boolean value) {
        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.damageOnHurt(value);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public ItemBuilder setEquipmentCameraOverlay(Key key) {
        if (key == null && !stack.hasData(DataComponentTypes.EQUIPPABLE)) {
            return this;
        }

        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.cameraOverlay(key);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    public boolean hasEquippableComponent() {
        return stack.hasData(DataComponentTypes.EQUIPPABLE);
    }

    public ItemBuilder setEquipmentModel(Key key) {
        if (key == null && !stack.hasData(DataComponentTypes.EQUIPPABLE)) {
            return this;
        }

        Equippable.Builder builder = stack.getDataOrDefault(
                DataComponentTypes.EQUIPPABLE,
                Equippable.equippable(EquipmentSlot.HAND).build()
        ).toBuilder();

        builder.assetId(key);

        stack.setData(DataComponentTypes.EQUIPPABLE, builder.build());
        return this;
    }

    /**
     * @since 1.21.2
     */
    public boolean isGlider() {
        return meta.isGlider();
    }

    /**
     * @since 1.21.2
     */
    public ItemBuilder setGlider(boolean value) {
        meta.setGlider(value);
        return this;
    }

    public boolean isUnbreakable() {
        return meta.isUnbreakable();
    }

    public ItemBuilder setUnbreakable(boolean value) {
        meta.setUnbreakable(value);
        return this;
    }

    /**
     * @since 1.20.5
     * @deprecated 1.21.2
     */
    public boolean isFireResistent() {
        return meta.isFireResistant();
    }

    /**
     * @since 1.20.5
     */
    public Boolean isHideToolTip() {
        return meta.isHideTooltip();
    }

    /**
     * @since 1.20.5
     * @deprecated 1.21.2
     */
    public ItemBuilder setFireResistent(boolean value) {
        meta.setFireResistant(value);
        return this;
    }

    /**
     * @since 1.20.5
     */
    public ItemBuilder setHideToolTip(boolean value) {
        meta.setHideTooltip(value);
        return this;
    }

    public boolean isMetaClass(Class<? extends ItemMeta> metaClass) {
        return metaClass.isInstance(meta);
    }

    public ItemBuilder setAxolotlVariant(Axolotl.Variant type) {
        if (meta instanceof AxolotlBucketMeta axolotlBucketMeta) {
            axolotlBucketMeta.setVariant(type);
        }
        return this;
    }

    public Material getType() {
        return stack.getType();
    }

    public ItemBuilder setBookAuthor(String author) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.setAuthor(author);
        }
        return this;
    }

    public ItemBuilder setBookGeneration(BookMeta.Generation type) {
        if (meta instanceof BookMeta bookMeta) {
            bookMeta.setGeneration(type);
        }
        return this;
    }

    public ItemBuilder addBannerPattern(Pattern pattern) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.addPattern(pattern);
        }
        return this;
    }

    public ItemBuilder setBannerPattern(int index, Pattern pattern) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPattern(index, pattern);
        }
        return this;
    }

    public List<Pattern> getBannerPatterns() {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.getPatterns();
        }
        return List.of();
    }

    public ItemBuilder setBannerPatterns(List<Pattern> list) {
        if (meta instanceof BannerMeta bannerMeta) {
            bannerMeta.setPatterns(list);
        }
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(color);
        } else if (meta instanceof PotionMeta potionMeta) {
            potionMeta.setColor(color);
        }
        return this;
    }

    public FireworkEffect getFireworkEffect() {
        if (meta instanceof FireworkEffectMeta fireworkMeta) {
            return fireworkMeta.getEffect();
        }
        return null;
    }

    public ItemBuilder setFireworkEffect(FireworkEffect build) {
        if (meta instanceof FireworkEffectMeta fireworkMeta) {
            fireworkMeta.setEffect(build);
        }
        return this;
    }

    public ItemBuilder setCompassLodestone(boolean tracked, Location location) {
        if (meta instanceof CompassMeta compassMeta) {
            compassMeta.setLodestoneTracked(tracked);
            compassMeta.setLodestone(location);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(Integer amount) {
        meta.setCustomModelData(amount);
        return this;
    }

    public ItemBuilder setDamage(int amount) {
        if (!VersionUtils.isAfter(1, 13)) {
            stack.setDurability((short) Math.max(0, Math.min(amount, stack.getType().getMaxDurability())));
        } else if (meta instanceof Damageable damageable) {
            damageable.setDamage(amount);
        }
        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment ench) {
        meta.removeEnchant(ench);
        return this;
    }

    public ItemBuilder setEnchantment(Enchantment ench, int lv) {
        meta.addEnchant(ench, lv, true);
        return this;
    }

    public ItemBuilder setFireworkPower(int power) {
        if (meta instanceof FireworkMeta fireworkMeta) {
            fireworkMeta.setPower(power);
        }
        return this;
    }

    public ItemBuilder setMusicInstrument(MusicInstrument type) {
        if (meta instanceof MusicInstrumentMeta musicInstrumentMeta) {
            musicInstrumentMeta.setInstrument(type);
        }
        return this;
    }

    public boolean hasItemFlag(ItemFlag flag) {
        return meta.hasItemFlag(flag);
    }

    public ItemBuilder setItemFlag(ItemFlag flag, boolean add) {
        if (add) {
            meta.addItemFlags(flag);
        } else {
            meta.removeItemFlags(flag);
        }
        return this;
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        return meta.getAttributeModifiers();
    }

    public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        meta.addAttributeModifier(attribute, attributeModifier);
        return this;
    }

    public ItemBuilder removeAttributeModifier(EquipmentSlot slot) {
        meta.removeAttributeModifier(slot);
        return this;
    }

    public ItemBuilder setItemFlags(ItemFlag[] values, boolean value) {
        for (ItemFlag itemFlag : values) {
            setItemFlag(itemFlag, value);
        }
        return this;
    }

    public ItemBuilder setItemModel(NamespacedKey key) {
        meta.setItemModel(key);
        return this;
    }

    public ItemBuilder setMaxDamage(int amount) {
        if (meta instanceof Damageable damageable) {
            damageable.setMaxDamage(amount);
        }
        return this;
    }

    public ItemBuilder setMaxStackSize(Integer value) {
        meta.setMaxStackSize(value);
        return this;
    }

    public ItemBuilder removeCustomEffect(PotionEffectType effect) {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.removeCustomEffect(effect);
        } else if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            suspiciousStewMeta.removeCustomEffect(effect);
        }
        return this;
    }

    public ItemBuilder addCustomEffect(PotionEffect effect) {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.addCustomEffect(effect, true);
        } else if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            suspiciousStewMeta.addCustomEffect(effect, true);
        }
        return this;
    }

    public ItemBuilder clearCustomEffects() {
        if (meta instanceof PotionMeta potionMeta) {
            potionMeta.clearCustomEffects();
        } else if (meta instanceof SuspiciousStewMeta suspiciousStewMeta) {
            suspiciousStewMeta.clearCustomEffects();
        }
        return this;
    }

    public ItemBuilder setRarity(ItemRarity rarity) {
        meta.setRarity(rarity);
        return this;
    }

    public ItemBuilder setRepairCost(int amount) {
        if (meta instanceof Repairable repairable) {
            repairable.setRepairCost(amount);
        }
        return this;
    }

    public int getDamage() {
        if (!VersionUtils.isAfter(1, 13)) {
            return stack.getDurability();
        } else if (meta instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }

    public ItemBuilder setSkullOwner(String name) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(name);
        }
        return this;
    }

    public ItemBuilder setTooltipStyle(NamespacedKey namespacedKey) {
        meta.setTooltipStyle(namespacedKey);
        return this;
    }

    public ItemBuilder setTrim(ArmorTrim armorTrim) {
        if (meta instanceof ArmorMeta armorMeta) {
            armorMeta.setTrim(armorTrim);
        }
        return this;
    }

    public ItemBuilder setTropicalFishBodyColor(DyeColor color) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setBodyColor(color);
        }
        return this;
    }

    public ItemBuilder setTropicalFishPatternColor(DyeColor color) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setPatternColor(color);
        }
        return this;
    }

    public ItemBuilder setTropicalFishPattern(TropicalFish.Pattern pattern) {
        if (meta instanceof TropicalFishBucketMeta tropicalFishBucketMeta) {
            tropicalFishBucketMeta.setPattern(pattern);
        }
        return this;
    }

    private void inheritProperties(Equippable.Builder builder, Equippable equippable) {
        builder.canBeSheared(equippable.canBeSheared());
        builder.allowedEntities(equippable.allowedEntities());
        builder.assetId(equippable.assetId());
        builder.cameraOverlay(equippable.cameraOverlay());
        builder.damageOnHurt(equippable.damageOnHurt());
        builder.dispensable(equippable.dispensable());
        builder.equipOnInteract(equippable.equipOnInteract());
        builder.shearSound(equippable.shearSound());
        builder.equipSound(equippable.equipSound());
        builder.swappable(equippable.swappable());
    }

    private void inheritProperties(KineticWeapon.Builder builder, KineticWeapon weapon) {
        builder.delayTicks(weapon.delayTicks());
        builder.contactCooldownTicks(weapon.contactCooldownTicks());
        builder.sound(weapon.sound());
        builder.damageConditions(weapon.damageConditions());
        builder.damageMultiplier(weapon.damageMultiplier());
        builder.dismountConditions(weapon.dismountConditions());
        builder.forwardMovement(weapon.forwardMovement());
        builder.hitSound(weapon.hitSound());
        builder.knockbackConditions(weapon.knockbackConditions());
    }

    private void inheritProperties(PiercingWeapon.Builder builder, PiercingWeapon weapon) {
        builder.sound(weapon.sound());
        builder.hitSound(weapon.hitSound());
        builder.dismounts(weapon.dismounts());
        builder.dealsKnockback(weapon.dealsKnockback());
    }
}
