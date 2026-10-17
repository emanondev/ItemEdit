package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimPattern;

public class TrimPatternAliases extends RegistryAliasSet<TrimPattern> implements IAliasSet<TrimPattern> {
    public TrimPatternAliases() {
        super("trim_pattern", ItemEdit.get(), Registry.TRIM_PATTERN);
    }
}