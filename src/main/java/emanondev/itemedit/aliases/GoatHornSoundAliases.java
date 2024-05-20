package emanondev.itemedit.aliases;

import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.Collection;
import java.util.stream.Collectors;

public class GoatHornSoundAliases extends AliasSet<MusicInstrument> {

    public GoatHornSoundAliases() {
        super("goat_horn_sound");
        try {//force load the class or throw an exception if absent
            Registry.INSTRUMENT.stream().collect(Collectors.toList());
        } catch (Throwable t) {
            MusicInstrument.values();
        }
    }

    @Override
    public String getName(MusicInstrument type) {
        return type.getKey().getNamespace().equals(NamespacedKey.MINECRAFT) ?
                type.getKey().getKey() : type.getKey().toString();
    }

    @Override
    public Collection<MusicInstrument> getValues() {
        try {//force load the class or throw an exception if absent
            return Registry.INSTRUMENT.stream().collect(Collectors.toList());
        } catch (Throwable t) {
            return MusicInstrument.values();
        }
    }
}
