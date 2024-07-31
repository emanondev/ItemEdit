package emanondev.itemedit.aliases;

import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;

public class BannerPatternAliasesNew extends RegistryAliasSet<PatternType> implements BannerPatternAliases {
    public BannerPatternAliasesNew() {
        super("banner_pattern", Registry.BANNER_PATTERN);
    }
}