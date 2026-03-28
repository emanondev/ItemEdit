package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;

public class AnimationAliases extends EnumAliasSet<ItemUseAnimation> {
    public AnimationAliases() {
        super("animation", ItemEdit.get(), ItemUseAnimation.class);
    }
}
