package emanondev.itemedit.aliases;

import org.bukkit.Registry;
import org.bukkit.Sound;

public class SoundAliases extends RegistryAliasSet<Sound> {
    public SoundAliases() {
        super("sound", Registry.SOUNDS);
    }
}
