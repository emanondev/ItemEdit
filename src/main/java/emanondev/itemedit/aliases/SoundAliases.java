package emanondev.itemedit.aliases;

import emanondev.itemedit.ItemEdit;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class SoundAliases extends RegistryAliasSet<Sound> {
    public SoundAliases() {
        super("sound", ItemEdit.get(), Registry.SOUNDS);
    }
}
