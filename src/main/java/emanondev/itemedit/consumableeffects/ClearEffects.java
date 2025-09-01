package emanondev.itemedit.consumableeffects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableApplyEffects;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableClearEffects;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClearEffects implements ConsumableClearEffects {


    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
