package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.meta.BookMeta.Generation;

public class GenAliases extends EnumAliasSet<Generation> {

    public GenAliases() {
        super("book_type", ItemEdit.get(), Generation.class);
    }
}
