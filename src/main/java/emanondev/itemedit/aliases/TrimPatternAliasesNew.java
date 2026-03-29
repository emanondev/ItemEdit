package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimPatternAliasesNew extends RegistryAliasSet<TrimPattern> implements TrimPatternAliases {
    public TrimPatternAliasesNew() {
        super("trim_pattern", ItemEdit.get(), Registry.TRIM_PATTERN);
    }
}