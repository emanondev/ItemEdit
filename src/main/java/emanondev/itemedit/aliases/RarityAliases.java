package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.ItemRarity;

public class RarityAliases extends EnumAliasSet<ItemRarity> {

    public RarityAliases() {
        super(ItemEdit.get(), ItemRarity.class);
    }
}