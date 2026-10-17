package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;

public class BannerPatternAliases extends RegistryAliasSet<PatternType> implements IAliasSet<PatternType> {
    public BannerPatternAliases() {
        super("banner_pattern", ItemEdit.get(), Registry.BANNER_PATTERN);
    }
}