package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;

public class AnimationAliases extends EnumAliasSet<ConsumableComponent.Animation> {
    public AnimationAliases() {
        super("animation", ItemEdit.get(), ConsumableComponent.Animation.class);
    }
}
