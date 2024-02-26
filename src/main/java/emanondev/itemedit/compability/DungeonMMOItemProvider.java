package emanondev.itemedit.compability;

import com.civious.dungeonmmo.api.DungeonMMOAPI;
import com.civious.dungeonmmo.api.ItemProvider;
import emanondev.itemedit.ItemEdit;
import org.bukkit.inventory.ItemStack;

public class DungeonMMOItemProvider implements ItemProvider {

    public static void register(){
        DungeonMMOAPI.getInstance().registerItemProvider("itemedit",new DungeonMMOItemProvider());
    }

    @Override
    public ItemStack generateItem(String s) {
        return ItemEdit.get().getServerStorage().getItem(s);
    }
}