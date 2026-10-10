package emanondev.itemedit.command.itemfood;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.ItemFoodCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.VersionUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ApplyStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.ClearAllStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.PlaySound;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.RemoveStatusEffects;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect.TeleportRandomly;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Info extends SubCmd {
    public Info(ItemFoodCommand itemFoodCommand) {
        super("info", itemFoodCommand, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player player = (Player) sender;
        ItemStack item = getItemInMainHand(player);
        boolean hasFood = item.hasData(DataComponentTypes.FOOD);
        boolean supportsConsumable = VersionUtils.isAfter(1, 20, 5);
        Consumable consumable = supportsConsumable ? item.getData(DataComponentTypes.CONSUMABLE) : null;

        if (!hasFood && consumable == null) {
            Util.sendMessage2(player, translate("not_food", player));
            return;
        }

        String eatSeconds = consumable == null ? "-" : String.format(Locale.ENGLISH, "%.2f", consumable.consumeSeconds());
        String eatTicks = consumable == null ? "-" : String.valueOf(Math.round(consumable.consumeSeconds() * 20));
        String nutrition = hasFood ? String.valueOf(item.getData(DataComponentTypes.FOOD).nutrition()) : "-";
        String saturation = hasFood
                ? String.format(Locale.ENGLISH, "%.2f", item.getData(DataComponentTypes.FOOD).saturation())
                : "-";
        String canAlwaysEat = hasFood ? String.valueOf(item.getData(DataComponentTypes.FOOD).canAlwaysEat()) : "-";
        String animation = consumable == null ? "-" : consumable.animation().toString();
        String sound = consumable == null ? "-" : consumable.sound().toString();
        String particles = consumable == null ? "-" : String.valueOf(consumable.hasConsumeParticles());
        String remainder = "-";
        if (VersionUtils.isAfter(1, 21)) {
            ItemStack convertsTo = item.getItemMeta().getUseRemainder();
            if (convertsTo != null) {
                remainder = convertsTo.getType().getKey().toString();
            }
        }

        Util.sendMessage2(player, translateList("message", player,
                "%eatseconds%", eatSeconds,
                "%eatticks%", eatTicks,
                "%nutrition%", nutrition,
                "%saturation%", saturation,
                "%animation%", animation,
                "%sound%", sound,
                "%consumeparticles%", particles,
                "%canalwayseat%", canAlwaysEat,
                "%convertto%", remainder));

        if (consumable != null && VersionUtils.isAfter(1, 21, 4)) {
            sendConsumeEffects(player, consumable.consumeEffects());
        }
    }

    private void sendConsumeEffects(Player player, List<ConsumeEffect> effects) {
        int potionEffects = effects.stream()
                .filter(ApplyStatusEffects.class::isInstance)
                .map(ApplyStatusEffects.class::cast)
                .mapToInt(effect -> effect.effects().size())
                .sum();
        if (potionEffects > 0) {
            Util.sendMessage2(player, translateList("apply_effect_prefix", player,
                    "%effects%", String.valueOf(potionEffects)));
        }

        int index = 0;
        for (ConsumeEffect effect : effects) {
            if (effect instanceof ApplyStatusEffects apply) {
                for (PotionEffect potion : apply.effects()) {
                    index++;
                    int duration = potion.getDuration();
                    String durationSeconds = duration < 0 ? "∞" : String.format(Locale.ENGLISH, "%.1f", duration / 20.0);
                    Util.sendMessage2(player, translateList("apply_effect", player,
                            "%index%", String.valueOf(index),
                            "%type%", potion.getType().getKey().toString(),
                            "%level%", String.valueOf(potion.getAmplifier() + 1),
                            "%duration_s%", durationSeconds,
                            "%duration_ticks%", String.valueOf(duration),
                            "%hasparticle%", String.valueOf(potion.hasParticles()),
                            "%isambient%", String.valueOf(potion.isAmbient()),
                            "%hasicon%", String.valueOf(potion.hasIcon()),
                            "%chance_perc%", String.format(Locale.ENGLISH, "%.1f", apply.probability() * 100)));
                }
                continue;
            }

            if (effect instanceof TeleportRandomly teleport) {
                Util.sendMessage2(player, translate("consume_effect_teleport", player,
                        "%diameter%", String.valueOf(teleport.diameter())));
            } else if (effect instanceof PlaySound playSound) {
                Util.sendMessage2(player, translate("consume_effect_sound", player,
                        "%sound%", playSound.sound().toString()));
            } else if (effect instanceof RemoveStatusEffects remove) {
                Util.sendMessage2(player, translate("consume_effect_remove", player,
                        "%effects%", remove.removeEffects().values().stream()
                                .map(Object::toString).collect(Collectors.joining(", "))));
            } else if (effect instanceof ClearAllStatusEffects) {
                Util.sendMessage2(player, translate("consume_effect_clear", player));
            } else {
                Util.sendMessage2(player, translate("consume_effect_unknown", player,
                        "%type%", effect.getClass().getSimpleName()));
            }
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        return List.of();
    }
}
