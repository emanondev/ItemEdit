package emanondev.itemedit.aliases;

import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimPattern;

@SuppressWarnings("UnstableApiUsage")
public class TrimPatternAliasesNew extends RegistryAliasSet<TrimPattern> implements TrimPatternAliases {
    public TrimPatternAliasesNew() {
        super("trim_pattern", Registry.TRIM_PATTERN);
    }
}