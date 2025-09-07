package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.entity.TropicalFish;

public class TropicalFishPatternAliases extends EnumAliasSet<TropicalFish.Pattern> {

    public TropicalFishPatternAliases() {
        super("tropical_fish_pattern", ItemEdit.get(), TropicalFish.Pattern.class);
    }
}
