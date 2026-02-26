package emanondev.itemedit.implementations;

import org.bukkit.inventory.meta.components.consumable.effects.ConsumableClearEffects;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClearEffects implements ConsumableClearEffects {

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
