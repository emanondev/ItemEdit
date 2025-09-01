package emanondev.itemedit.consumableeffects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableApplyEffects;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableRemoveEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveEffects implements ConsumableRemoveEffect {

    private List<PotionEffectType> effectTypes = new ArrayList<>();

    @Override
    public @NotNull PotionEffectType addEffectType(@NotNull PotionEffectType type) {
        effectTypes.add(type);
        return type;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("effectsTypes", effectTypes);
        return map;
    }
}
