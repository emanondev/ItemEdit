package emanondev.itemedit.consumableeffects;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.components.consumable.effects.*;

public class ConsumableEffectsUtil {

    public static Class<? extends ConsumableEffect> getInterface(String id){
        switch (id){
            case "apply_effects":
                return ConsumableApplyEffects.class;
            case "remove_effects":
                return ConsumableRemoveEffect.class;
            case "clear_all_effects":
                return ConsumableClearEffects.class;
            case "teleport_randomly":
                return ConsumableTeleportRandomly.class;
            case "play_sound":
                return ConsumablePlaySound.class;
            default:
                return null;
        }
    }

    public static Class<? extends ConsumableEffect> getClass(String id){
        switch (id){
            case "apply_effects":
                return ApplyEffects.class;
            case "remove_effects":
                return RemoveEffects.class;
            case "clear_all_effects":
                return ClearEffects.class;
            case "teleport_randomly":
                return TeleportRandomly.class;
            case "play_sound":
                return PlaySound.class;
            default:
                return null;
        }
    }

    public static NamespacedKey getKey(Class<? extends ConsumableEffect> id){
        switch (id){
            case ConsumableApplyEffects.class:
                return ConsumableApplyEffects.KEY;
            case ConsumableRemoveEffect.class:
                return ConsumableRemoveEffect.class;
            case "clear_all_effects":
                return ConsumableClearEffects.class;
            case "teleport_randomly":
                return ConsumableTeleportRandomly.class;
            case "play_sound":
                return ConsumablePlaySound.class;
            default:
                return null;
        }
    }
}
