package emanondev.itemedit.aliases;
import org.bukkit.inventory.meta.BookMeta.Generation;

public class GenAliases extends EnumAliasSet<Generation>{

	public GenAliases() {
		super("book_type",Generation.class);
	}
}
