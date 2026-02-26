package emanondev.itemedit.implementations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KineticWeaponComponentCondition implements KineticWeaponComponent.Condition {

    private int maxDurationTicks;
    private float minSpeed;
    private float minRelativeSpeed;

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of("maxDurationTicks", getMaxDurationTicks(),
                "minSpeed", getMinSpeed(),
                "minRelativeSpeed", getMinRelativeSpeed());
    }
}
