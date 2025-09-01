package emanondev.itemedit.consumableeffects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableApplyEffects;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyEffects implements ConsumableApplyEffects {

    private List<PotionEffect> effects = new ArrayList<>();
    private float probability = 1;

    @Override
    public @NotNull PotionEffect addEffect(@NotNull PotionEffect potionEffect) {
        effects.add(potionEffect);
        return potionEffect;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("probability", probability);
        map.put("effects", effects);
        return map;
    }
}
