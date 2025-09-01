package emanondev.itemedit.consumableeffects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.meta.components.consumable.effects.ConsumableClearEffects;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeleportRandomly implements ConsumableClearEffects {

    private float diameter = 5;

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("diameter", diameter);
        return map;
    }
}
